package com.nutrition.application.service;

import com.nutrition.domain.entity.auth.EmailQueue;
import com.nutrition.infrastructure.repository.EmailQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailQueueService {

    private final EmailQueueRepository emailQueueRepository;
    private final BrevoEmailService emailService;

    @Value("${app.email.queue.max-retries:5}")
    private Integer defaultMaxRetries;

    @Value("${app.email.queue.enabled:true}")
    private Boolean queueEnabled;

    /**
     * Queue an email for sending
     */
    @Transactional
    public EmailQueue queueEmail(EmailQueue.EmailType emailType, String recipientEmail,
                                  String recipientName, String token, String additionalData) {
        EmailQueue emailQueue = EmailQueue.builder()
            .emailType(emailType)
            .recipientEmail(recipientEmail)
            .recipientName(recipientName)
            .token(token)
            .additionalData(additionalData)
            .status(EmailQueue.Status.PENDING)
            .retryCount(0)
            .maxRetries(defaultMaxRetries)
            .nextRetryAt(LocalDateTime.now())
            .build();

        emailQueue = emailQueueRepository.save(emailQueue);
        log.info("Email queued: type={}, recipient={}, id={}, max-retries={}",
            emailType, recipientEmail, emailQueue.getId(), defaultMaxRetries);
        return emailQueue;
    }

    /**
     * Process pending emails - runs based on configuration
     * Default: every 60 seconds with 5 second initial delay
     */
    @Scheduled(fixedDelayString = "${app.email.queue.polling-interval:60000}",
               initialDelayString = "${app.email.queue.initial-delay:5000}")
    @Transactional
    public void processPendingEmails() {
        if (!queueEnabled) {
            log.debug("Email queue processing is disabled");
            return;
        }

        List<EmailQueue> pendingEmails = emailQueueRepository.findPendingEmails(LocalDateTime.now());

        if (pendingEmails.isEmpty()) {
            log.debug("No pending emails to process");
            return;
        }

        log.info("Processing {} pending emails", pendingEmails.size());

        for (EmailQueue emailQueue : pendingEmails) {
            processEmailQueue(emailQueue);
        }
    }

    /**
     * Process a single email from the queue
     */
    @Transactional
    public void processEmailQueue(EmailQueue emailQueue) {
        try {
            log.info("Processing email: id={}, type={}, recipient={}, attempt={}/{}",
                emailQueue.getId(), emailQueue.getEmailType(), emailQueue.getRecipientEmail(),
                emailQueue.getRetryCount() + 1, emailQueue.getMaxRetries());

            // Mark as processing
            emailQueue.markAsProcessing();
            emailQueueRepository.save(emailQueue);

            // Send the email based on type
            boolean success = sendEmailByType(emailQueue);

            if (success) {
                emailQueue.markAsSent();
                emailQueueRepository.save(emailQueue);
                log.info("Email sent successfully: id={}, type={}, recipient={}",
                    emailQueue.getId(), emailQueue.getEmailType(), emailQueue.getRecipientEmail());
            } else {
                handleEmailFailure(emailQueue, "Email sending returned false");
            }

        } catch (Exception e) {
            log.error("Error processing email queue id={}: {}", emailQueue.getId(), e.getMessage(), e);
            handleEmailFailure(emailQueue, e.getMessage());
        }
    }

    /**
     * Send email based on type
     */
    private boolean sendEmailByType(EmailQueue emailQueue) {
        try {
            switch (emailQueue.getEmailType()) {
                case CONFIRMATION:
                    return emailService.sendConfirmationEmailSync(
                        emailQueue.getRecipientEmail(),
                        emailQueue.getRecipientName(),
                        emailQueue.getToken()
                    );

                case WELCOME:
                    return emailService.sendWelcomeEmailSync(
                        emailQueue.getRecipientEmail(),
                        emailQueue.getRecipientName()
                    );

                case PASSWORD_RESET:
                    return emailService.sendPasswordResetEmailSync(
                        emailQueue.getRecipientEmail(),
                        emailQueue.getRecipientName(),
                        emailQueue.getToken()
                    );

                default:
                    log.error("Unsupported email type for queue: {}. Only auth emails (CONFIRMATION, WELCOME, PASSWORD_RESET) use queue.",
                        emailQueue.getEmailType());
                    return false;
            }
        } catch (Exception e) {
            log.error("Error sending email type {}: {}", emailQueue.getEmailType(), e.getMessage());
            return false;
        }
    }

    /**
     * Handle email failure with retry logic
     */
    @Transactional
    protected void handleEmailFailure(EmailQueue emailQueue, String errorMessage) {
        emailQueue.incrementRetryCount();
        emailQueue.setLastError(errorMessage);

        if (emailQueue.canRetry()) {
            emailQueue.scheduleNextRetry();
            log.warn("Email failed, scheduled for retry: id={}, attempt={}/{}, next retry at={}",
                emailQueue.getId(), emailQueue.getRetryCount(), emailQueue.getMaxRetries(),
                emailQueue.getNextRetryAt());
        } else {
            emailQueue.markAsFailed(errorMessage);
            log.error("Email permanently failed after {} attempts: id={}, type={}, recipient={}",
                emailQueue.getRetryCount(), emailQueue.getId(), emailQueue.getEmailType(),
                emailQueue.getRecipientEmail());
        }

        emailQueueRepository.save(emailQueue);
    }

    /**
     * Get statistics about the email queue
     */
    public EmailQueueStats getStats() {
        Long pending = emailQueueRepository.countPendingEmails();
        Long failed = emailQueueRepository.countFailedEmails();
        return new EmailQueueStats(pending, failed);
    }

    /**
     * Retry a failed email manually
     */
    @Transactional
    public void retryFailedEmail(Long emailQueueId) {
        EmailQueue emailQueue = emailQueueRepository.findById(emailQueueId)
            .orElseThrow(() -> new RuntimeException("Email queue not found"));

        if (emailQueue.getStatus() == EmailQueue.Status.FAILED) {
            emailQueue.setStatus(EmailQueue.Status.PENDING);
            emailQueue.setRetryCount(0);
            emailQueue.setNextRetryAt(LocalDateTime.now());
            emailQueueRepository.save(emailQueue);
            log.info("Failed email reset for retry: id={}", emailQueueId);
        }
    }

    /**
     * Get all failed emails
     */
    public List<EmailQueue> getFailedEmails() {
        return emailQueueRepository.findFailedEmails();
    }

    public record EmailQueueStats(Long pending, Long failed) {}
}
