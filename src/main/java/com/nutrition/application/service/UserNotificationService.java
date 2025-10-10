package com.nutrition.application.service;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Scheduled task that runs every hour to check for users who need welcome emails
     * Cron expression: At minute 0, every hour
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void checkAndSendPendingWelcomeEmails() {
        log.debug("Checking for pending welcome emails...");

        try {
            // Find all enabled users who haven't received welcome email yet
            List<User> usersNeedingWelcomeEmail = userRepository.findAll().stream()
                .filter(User::isEnabled)
                .filter(user -> !user.getWelcomeEmailSent())
                .collect(Collectors.toList());

            if (usersNeedingWelcomeEmail.isEmpty()) {
                log.debug("No pending welcome emails found");
                return;
            }

            log.info("Found {} users needing welcome emails", usersNeedingWelcomeEmail.size());

            for (User user : usersNeedingWelcomeEmail) {
                sendWelcomeEmailAndMarkSent(user);
            }

            log.info("Welcome email check completed. Processed {} users", usersNeedingWelcomeEmail.size());
        } catch (Exception e) {
            log.error("Error during welcome email check: {}", e.getMessage(), e);
        }
    }

    /**
     * Send welcome email to a user and mark it as sent
     */
    @Transactional
    public void sendWelcomeEmailAndMarkSent(User user) {
        try {
            // Send welcome email asynchronously
            emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

            // Mark as sent in database
            user.setWelcomeEmailSent(true);
            user.setWelcomeEmailSentAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Welcome email sent and marked for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending welcome email to user {}: {}", user.getEmail(), e.getMessage());
            // Don't throw exception - we'll retry in the next scheduled run
        }
    }

    /**
     * Manual method to resend welcome email to a user
     * Can be called from a controller endpoint if needed
     */
    @Transactional
    public void resendWelcomeEmail(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isEnabled()) {
            throw new IllegalStateException("User account is not enabled");
        }

        // Reset the flag and send again
        user.setWelcomeEmailSent(false);
        user.setWelcomeEmailSentAt(null);
        userRepository.save(user);

        sendWelcomeEmailAndMarkSent(user);
    }
}
