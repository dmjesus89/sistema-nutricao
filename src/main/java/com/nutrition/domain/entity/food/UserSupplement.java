package com.nutrition.domain.entity.food;

import com.nutrition.domain.entity.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity representing a user's supplement tracking with frequency and reminder settings.
 * This replaces the old "preference" concept - users simply track supplements they're taking.
 */
@Entity
@Table(name = "user_supplements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "supplement_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSupplement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplement_id", nullable = false)
    private Supplement supplement;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false)
    @Builder.Default
    private Frequency frequency = Frequency.DAILY;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "dosage_time")
    private LocalTime dosageTime; // Time of day to take the supplement

    @Column(name = "days_of_week", length = 100)
    private String daysOfWeek; // Comma-separated days for WEEKLY frequencies

    @Column(name = "email_reminder_enabled", nullable = false)
    @Builder.Default
    private Boolean emailReminderEnabled = false;

    @Column(name = "last_taken_at")
    private LocalDateTime lastTakenAt; // Track when user last took the supplement

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Frequency {
        DAILY("Diário", "Tomar todos os dias"),
        WEEKLY("Semanal", "Tomar uma vez por semana"),
        TWICE_WEEKLY("2x por semana", "Tomar duas vezes por semana"),
        THREE_TIMES_WEEKLY("3x por semana", "Tomar três vezes por semana"),
        MONTHLY("Mensal", "Tomar uma vez por mês");

        private final String displayName;
        private final String description;

        Frequency(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Mark this supplement as taken at the current time
     */
    public void markAsTaken() {
        this.lastTakenAt = LocalDateTime.now();
    }
}
