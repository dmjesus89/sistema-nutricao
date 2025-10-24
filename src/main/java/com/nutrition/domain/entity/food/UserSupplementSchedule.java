package com.nutrition.domain.entity.food;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity representing a specific dosage time/schedule for a user's supplement.
 * Supports multiple times per day (e.g., morning and evening doses).
 */
@Entity
@Table(name = "user_supplement_schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSupplementSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_supplement_id", nullable = false)
    private UserSupplement userSupplement;

    @Column(name = "dosage_time", nullable = false)
    private LocalTime dosageTime;

    @Column(name = "label", length = 50)
    private String label; // e.g., "Morning", "Evening", "With lunch", "Before bed"

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
