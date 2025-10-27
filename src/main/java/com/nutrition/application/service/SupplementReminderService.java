package com.nutrition.application.service;

import com.nutrition.domain.entity.food.Supplement;
import com.nutrition.domain.entity.food.UserSupplement;
import com.nutrition.infrastructure.repository.UserSupplementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupplementReminderService {

    private final UserSupplementRepository userSupplementRepository;
    private final BrevoEmailService emailService;

    /**
     * Scheduled task that runs every hour to check for supplement reminders
     * Cron: 0 0 * * * * - Run at the start of every hour
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(readOnly = true)
    public void sendSupplementReminders() {
        log.info("Running supplement reminder scheduler...");

        LocalTime currentTime = LocalTime.now();
        LocalDate currentDate = LocalDate.now();
        DayOfWeek currentDay = currentDate.getDayOfWeek();

        // Find all user supplements with email reminders enabled
        List<UserSupplement> userSupplements = userSupplementRepository.findByEmailReminderEnabledTrue();

        log.info("Found {} user supplements with email reminders enabled", userSupplements.size());

        for (UserSupplement userSupplement : userSupplements) {
            try {
                // Check schedules first (for multiple doses per day)
                if (!userSupplement.getSchedules().isEmpty()) {
                    // Use schedules for reminder times
                    for (var schedule : userSupplement.getSchedules()) {
                        if (shouldSendReminderForSchedule(schedule, userSupplement, currentTime, currentDay)) {
                            sendReminderEmailForSchedule(userSupplement, schedule);
                        }
                    }
                } else {
                    // Fallback to old dosageTime field for backward compatibility
                    if (shouldSendReminder(userSupplement, currentTime, currentDay)) {
                        sendReminderEmail(userSupplement);
                    }
                }
            } catch (Exception e) {
                log.error("Error sending reminder for user supplement ID {}: {}", userSupplement.getId(), e.getMessage(), e);
            }
        }

        log.info("Supplement reminder scheduler completed");
    }

    /**
     * Determines if a reminder should be sent based on time and frequency settings
     */
    private boolean shouldSendReminder(UserSupplement userSupplement, LocalTime currentTime, DayOfWeek currentDay) {
        // Check if dosage time is set
        if (userSupplement.getDosageTime() == null) {
            return false;
        }

        // Check if current time matches dosage time (within 30 minutes)
        LocalTime dosageTime = userSupplement.getDosageTime();
        long minutesDifference = Math.abs(
                currentTime.toSecondOfDay() / 60 - dosageTime.toSecondOfDay() / 60
        );

        if (minutesDifference > 30) {
            return false;
        }

        // Check frequency
        UserSupplement.Frequency frequency = userSupplement.getFrequency();
        if (frequency == null || frequency == UserSupplement.Frequency.DAILY) {
            return true; // Daily reminders always send
        }

        if (frequency == UserSupplement.Frequency.WEEKLY) {
            // Check if today is one of the selected days
            String daysOfWeek = userSupplement.getDaysOfWeek();
            if (daysOfWeek == null || daysOfWeek.isEmpty()) {
                return false;
            }

            String currentDayAbbr = getDayAbbreviation(currentDay);
            return daysOfWeek.contains(currentDayAbbr);
        }

        if (frequency == UserSupplement.Frequency.MONTHLY) {
            // Send reminder on the first day of the month
            return LocalDate.now().getDayOfMonth() == 1;
        }

        return false;
    }

    /**
     * Send reminder email to user
     */
    private void sendReminderEmail(UserSupplement userSupplement) {
        try {
            Supplement supplement = userSupplement.getSupplement();
            String userEmail = userSupplement.getUser().getEmail();
            String userName = userSupplement.getUser().getFirstName();

            String supplementName = supplement.getName();
            String dosageTime = userSupplement.getDosageTime().toString();
            String recommendedDosage = supplement.getRecommendedDosage() != null
                ? supplement.getRecommendedDosage()
                : "As recommended";

            // Send email directly via Brevo
            emailService.sendSupplementReminderEmail(
                userEmail,
                userName,
                supplementName,
                dosageTime,
                recommendedDosage
            );

            log.info("Supplement reminder sent to {} for supplement: {}", userEmail, supplementName);

        } catch (Exception e) {
            log.error("Failed to send supplement reminder email", e);
        }
    }

    /**
     * Converts DayOfWeek to abbreviation used in database
     */
    private String getDayAbbreviation(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "MON";
            case TUESDAY -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY -> "THU";
            case FRIDAY -> "FRI";
            case SATURDAY -> "SAT";
            case SUNDAY -> "SUN";
        };
    }

    // ========== SCHEDULE-BASED REMINDER METHODS (Multiple doses per day) ==========

    /**
     * Determines if a reminder should be sent for a specific schedule
     */
    private boolean shouldSendReminderForSchedule(
            com.nutrition.domain.entity.food.UserSupplementSchedule schedule,
            UserSupplement userSupplement,
            LocalTime currentTime,
            DayOfWeek currentDay) {

        // Check if current time matches schedule dosage time (within 30 minutes)
        LocalTime dosageTime = schedule.getDosageTime();
        long minutesDifference = Math.abs(
                currentTime.toSecondOfDay() / 60 - dosageTime.toSecondOfDay() / 60
        );

        if (minutesDifference > 30) {
            return false;
        }

        // Check frequency
        UserSupplement.Frequency frequency = userSupplement.getFrequency();
        if (frequency == null || frequency == UserSupplement.Frequency.DAILY) {
            return true; // Daily reminders always send
        }

        if (frequency == UserSupplement.Frequency.WEEKLY) {
            // Check if today is one of the selected days
            String daysOfWeek = userSupplement.getDaysOfWeek();
            if (daysOfWeek == null || daysOfWeek.isEmpty()) {
                return false;
            }

            String currentDayAbbr = getDayAbbreviation(currentDay);
            return daysOfWeek.contains(currentDayAbbr);
        }

        if (frequency == UserSupplement.Frequency.MONTHLY) {
            // Send reminder on the first day of the month
            return LocalDate.now().getDayOfMonth() == 1;
        }

        return false;
    }

    /**
     * Send reminder email for a specific schedule
     */
    private void sendReminderEmailForSchedule(
            UserSupplement userSupplement,
            com.nutrition.domain.entity.food.UserSupplementSchedule schedule) {
        try {
            Supplement supplement = userSupplement.getSupplement();
            String userEmail = userSupplement.getUser().getEmail();
            String userName = userSupplement.getUser().getFirstName();

            // Build schedule label for display
            String scheduleLabel = schedule.getLabel() != null && !schedule.getLabel().isEmpty()
                    ? " (" + schedule.getLabel() + ")"
                    : "";

            String supplementName = supplement.getName();
            String dosageTime = schedule.getDosageTime().toString() + scheduleLabel;
            String recommendedDosage = supplement.getRecommendedDosage() != null
                ? supplement.getRecommendedDosage()
                : "As recommended";

            // Send email directly via Brevo
            emailService.sendSupplementReminderEmail(
                userEmail,
                userName,
                supplementName,
                dosageTime,
                recommendedDosage
            );

            log.info("Supplement reminder sent to {} for supplement: {} at {}",
                    userEmail, supplementName, dosageTime);

        } catch (Exception e) {
            log.error("Failed to send supplement reminder email for schedule", e);
        }
    }
}
