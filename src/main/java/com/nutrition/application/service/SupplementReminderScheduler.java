package com.nutrition.application.service;

import com.nutrition.domain.entity.food.UserSupplement;
import com.nutrition.infrastructure.repository.UserSupplementRepository;
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

    private final UserSupplementRepository userSupplementRepository;
    private final BrevoEmailService emailService;

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

            // Find all user supplements with email reminders enabled
            List<UserSupplement> userSupplements = userSupplementRepository
                    .findByEmailReminderEnabledTrue();

            log.info("Found {} user supplements with reminders enabled", userSupplements.size());

            for (UserSupplement userSupplement : userSupplements) {
                if (shouldSendReminder(userSupplement, currentTime, currentDay)) {
                    queueSupplementReminder(userSupplement);
                }
            }

        } catch (Exception e) {
            log.error("Error checking supplement reminders: {}", e.getMessage(), e);
        }
    }

    private boolean shouldSendReminder(UserSupplement userSupplement, LocalTime currentTime, DayOfWeek currentDay) {
        // Check if dosage time is set
        if (userSupplement.getDosageTime() == null) {
            return false;
        }

        // Check if current time is within 15 minutes of dosage time
        LocalTime dosageTime = userSupplement.getDosageTime();
        long minutesDiff = Math.abs(java.time.Duration.between(currentTime, dosageTime).toMinutes());

        if (minutesDiff > 15) {
            return false;
        }

        // Check frequency
        UserSupplement.Frequency frequency = userSupplement.getFrequency();
        if (frequency == null || frequency == UserSupplement.Frequency.DAILY) {
            return true;
        }

        if (frequency == UserSupplement.Frequency.WEEKLY) {
            // Check if today is in the list of days
            String daysOfWeek = userSupplement.getDaysOfWeek();
            if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
                List<String> days = Arrays.asList(daysOfWeek.split(","));
                return days.contains(currentDay.toString());
            }
        }

        return false;
    }

    private void queueSupplementReminder(UserSupplement userSupplement) {
        try {
            String supplementName = userSupplement.getSupplement().getName();
            String userName = userSupplement.getUser().getFirstName();
            String userEmail = userSupplement.getUser().getEmail();
            String dosageTime = userSupplement.getDosageTime() != null ?
                    userSupplement.getDosageTime().toString() : "N/A";
            String recommendedDosage = userSupplement.getSupplement().getRecommendedDosage() != null ?
                    userSupplement.getSupplement().getRecommendedDosage() : "Ver instruções";

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
