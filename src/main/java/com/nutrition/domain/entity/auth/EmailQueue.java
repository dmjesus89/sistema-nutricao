package com.nutrition.domain.entity.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_queue")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_type", nullable = false)
    private EmailType emailType;

    @Column(name = "recipient_email", nullable = false, length = 100)
    private String recipientEmail;

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @Column(name = "token", length = 500)
    private String token;

    @Column(name = "additional_data", columnDefinition = "TEXT")
    private String additionalData;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    @Builder.Default
    private Integer maxRetries = 5;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum EmailType {
        CONFIRMATION,
        WELCOME,
        PASSWORD_RESET,
        MEAL_REMINDER,
        MEAL_CONSUMPTION,
        WEEKLY_SUMMARY
    }

    public enum Status {
        PENDING,
        PROCESSING,
        SENT,
        FAILED
    }

    public void incrementRetryCount() {
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsSent() {
        this.status = Status.SENT;
        this.sentAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = Status.FAILED;
        this.lastError = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsProcessing() {
        this.status = Status.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    public void scheduleNextRetry() {
        this.status = Status.PENDING;
        // Exponential backoff: 1min, 2min, 4min, 8min, 16min
        long delayMinutes = (long) Math.pow(2, retryCount);
        this.nextRetryAt = LocalDateTime.now().plusMinutes(delayMinutes);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canRetry() {
        return retryCount < maxRetries;
    }

    public boolean isReadyForRetry() {
        return status == Status.PENDING &&
               (nextRetryAt == null || LocalDateTime.now().isAfter(nextRetryAt));
    }
}
