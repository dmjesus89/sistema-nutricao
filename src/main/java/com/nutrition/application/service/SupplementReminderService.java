package com.nutrition.application.service;

import com.nutrition.domain.entity.food.Supplement;
import com.nutrition.domain.entity.food.UserSupplementPreference;
import com.nutrition.infrastructure.repository.UserSupplementPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupplementReminderService {

    private final UserSupplementPreferenceRepository preferenceRepository;
    private final EmailService emailService;

    @Value("${app.email.from:noreply@nutrisystem.com}")
    private String fromEmail;

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

        // Find all preferences with email reminders enabled
        List<UserSupplementPreference> preferences = preferenceRepository.findByEmailReminderEnabledTrue();

        log.info("Found {} supplement preferences with email reminders enabled", preferences.size());

        for (UserSupplementPreference preference : preferences) {
            try {
                if (shouldSendReminder(preference, currentTime, currentDay)) {
                    sendReminderEmail(preference);
                }
            } catch (Exception e) {
                log.error("Error sending reminder for preference ID {}: {}", preference.getId(), e.getMessage(), e);
            }
        }

        log.info("Supplement reminder scheduler completed");
    }

    /**
     * Determines if a reminder should be sent based on time and frequency settings
     */
    private boolean shouldSendReminder(UserSupplementPreference preference, LocalTime currentTime, DayOfWeek currentDay) {
        // Check if dosage time is set
        if (preference.getDosageTime() == null) {
            return false;
        }

        // Check if current time matches dosage time (within 30 minutes)
        LocalTime dosageTime = preference.getDosageTime();
        long minutesDifference = Math.abs(
                currentTime.toSecondOfDay() / 60 - dosageTime.toSecondOfDay() / 60
        );

        if (minutesDifference > 30) {
            return false;
        }

        // Check frequency
        String frequency = preference.getFrequency();
        if (frequency == null || frequency.equals("DAILY")) {
            return true; // Daily reminders always send
        }

        if (frequency.equals("WEEKLY")) {
            // Check if today is one of the selected days
            String daysOfWeek = preference.getDaysOfWeek();
            if (daysOfWeek == null || daysOfWeek.isEmpty()) {
                return false;
            }

            String currentDayAbbr = getDayAbbreviation(currentDay);
            return daysOfWeek.contains(currentDayAbbr);
        }

        if (frequency.equals("MONTHLY")) {
            // Send reminder on the first day of the month
            return LocalDate.now().getDayOfMonth() == 1;
        }

        return false;
    }

    /**
     * Sends reminder email to user based on their preferred locale
     */
    private void sendReminderEmail(UserSupplementPreference preference) {
        try {
            Supplement supplement = preference.getSupplement();
            String userEmail = preference.getUser().getEmail();
            String userName = preference.getUser().getFirstName();
            String locale = preference.getUser().getPreferredLocale();

            if (locale == null || locale.isEmpty()) {
                locale = "en"; // Default to English
            }

            // Load email template based on locale
            String emailContent = loadEmailTemplate(locale);

            // Replace placeholders
            emailContent = emailContent.replace("{userName}", userName)
                    .replace("{supplementName}", supplement.getName())
                    .replace("{dosage}", supplement.getRecommendedDosage() != null ? supplement.getRecommendedDosage() : "As recommended")
                    .replace("{time}", preference.getDosageTime().toString());

            // Get subject line based on locale
            String subject = getSubject(locale);

            // Send email
            emailService.sendHtmlEmail(userEmail, subject, emailContent);

            log.info("Sent supplement reminder to {} for supplement: {}", userEmail, supplement.getName());

        } catch (Exception e) {
            log.error("Failed to send supplement reminder email", e);
            throw new RuntimeException("Failed to send supplement reminder email", e);
        }
    }

    /**
     * Loads email template from resources based on locale
     */
    private String loadEmailTemplate(String locale) throws IOException {
        String templatePath = String.format("email-templates/%s/supplement-reminder.html", locale);

        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Email template not found for locale: {}. Falling back to English", locale);
            // Fallback to English if locale template not found
            ClassPathResource resource = new ClassPathResource("email-templates/en/supplement-reminder.html");
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Gets email subject based on locale
     */
    private String getSubject(String locale) {
        return switch (locale) {
            case "es" -> "⏰ Recordatorio de Suplemento - NutriSystem";
            case "pt" -> "⏰ Lembrete de Suplemento - NutriSystem";
            default -> "⏰ Supplement Reminder - NutriSystem";
        };
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
}
