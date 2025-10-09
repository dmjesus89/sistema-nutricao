package com.nutrition.domain.entity.config;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "calorie_adjustment_type", nullable = false, length = 20)
    private CalorieAdjustmentType calorieAdjustmentType;

    @Column(name = "calorie_adjustment_value", precision = 7, scale = 2)
    private BigDecimal calorieAdjustmentValue;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum CalorieAdjustmentType {
        PERCENTAGE, // Percentage adjustment (e.g., -15% or +10%)
        FIXED,      // Fixed calorie amount (e.g., -500 or +300)
        NONE        // No adjustment (maintenance)
    }
}
