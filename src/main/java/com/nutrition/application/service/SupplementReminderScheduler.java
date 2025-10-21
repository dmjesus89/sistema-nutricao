package com.nutrition.application.service;

import com.nutrition.domain.entity.food.UserSupplementPreference;
import com.nutrition.infrastructure.repository.UserSupplementPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplementReminderScheduler {

    private final UserSupplementPreferenceRepository preferenceRepository;
    private final EmailService emailService;

    /**
     * Runs every 15 minutes to check for supplement reminders that need to be sent
     */
    @Scheduled(cron = "0 */15 * * * *") // Every 15 minutes
    @Transactional(readOnly = true)
    public void checkSupplementReminders() {
        log.info("Checking for supplement reminders to send");

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalTime currentTime = now.toLocalTime();
            DayOfWeek currentDay = now.getDayOfWeek();

            // Find all preferences with email reminders enabled
            List<UserSupplementPreference> preferences = preferenceRepository
                    .findByEmailReminderEnabledTrueAndPreferenceType(
                            UserSupplementPreference.PreferenceType.CURRENTLY_USING
                    );

            log.info("Found {} supplement preferences with reminders enabled", preferences.size());

            for (UserSupplementPreference preference : preferences) {
                if (shouldSendReminder(preference, currentTime, currentDay)) {
                    queueSupplementReminder(preference);
                }
            }

        } catch (Exception e) {
            log.error("Error checking supplement reminders: {}", e.getMessage(), e);
        }
    }

    private boolean shouldSendReminder(UserSupplementPreference preference, LocalTime currentTime, DayOfWeek currentDay) {
        // Check if dosage time is set
        if (preference.getDosageTime() == null) {
            return false;
        }

        // Check if current time is within 15 minutes of dosage time
        LocalTime dosageTime = preference.getDosageTime();
        long minutesDiff = Math.abs(java.time.Duration.between(currentTime, dosageTime).toMinutes());

        if (minutesDiff > 15) {
            return false;
        }

        // Check frequency
        String frequency = preference.getFrequency();
        if (frequency == null || frequency.equals("DAILY")) {
            return true;
        }

        if (frequency.equals("WEEKLY")) {
            // Default to Monday for weekly
            return currentDay == DayOfWeek.MONDAY;
        }

        if (frequency.equals("CUSTOM")) {
            // Check if today is in the list of days
            String daysOfWeek = preference.getDaysOfWeek();
            if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
                List<String> days = Arrays.asList(daysOfWeek.split(","));
                return days.contains(currentDay.toString());
            }
        }

        return false;
    }

    private void queueSupplementReminder(UserSupplementPreference preference) {
        try {
            String supplementName = preference.getSupplement().getName();
            String userName = preference.getUser().getFirstName();
            String userEmail = preference.getUser().getEmail();
            String dosageTime = preference.getDosageTime() != null ?
                    preference.getDosageTime().toString() : "N/A";
            String recommendedDosage = preference.getSupplement().getRecommendedDosage() != null ?
                    preference.getSupplement().getRecommendedDosage() : "Ver instruções";

            emailService.sendSupplementReminderEmail(
                    userEmail,
                    userName,
                    supplementName,
                    dosageTime,
                    recommendedDosage
            );

            log.info("Sent supplement reminder for user {} - supplement: {}",
                    userEmail, supplementName);

        } catch (Exception e) {
            log.error("Error sending supplement reminder: {}", e.getMessage(), e);
        }
    }
}
