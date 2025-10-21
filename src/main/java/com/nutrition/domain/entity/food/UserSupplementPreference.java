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

@Entity
@Table(name = "user_supplement_preferences",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "supplement_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSupplementPreference {

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
    @Column(name = "preference_type", nullable = false)
    private PreferenceType preferenceType;

    @Column(name = "notes", length = 500)
    private String notes;

    // Time routine fields for email alerts
    @Column(name = "dosage_time")
    private LocalTime dosageTime; // Time of day to take the supplement

    @Column(name = "frequency")
    private String frequency; // DAILY, WEEKLY, MONTHLY

    @Column(name = "days_of_week", length = 100)
    private String daysOfWeek; // Comma-separated days (e.g., "MONDAY,WEDNESDAY,FRIDAY")

    @Column(name = "email_reminder_enabled", nullable = false)
    @Builder.Default
    private Boolean emailReminderEnabled = false;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PreferenceType {
        CURRENTLY_USING("Usando atualmente", "Suplemento em uso atual");

        private final String displayName;
        private final String description;

        PreferenceType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isPositive() {
            return this == CURRENTLY_USING;
        }
    }
}
