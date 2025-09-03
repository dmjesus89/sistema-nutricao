package com.nutrition.domain.entity;

import com.nutrition.domain.entity.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_checkins",
        uniqueConstraints = @UniqueConstraint(name = "uk_meal_checkins_meal",
                columnNames = {"meal_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class MealCheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "completion_percentage", nullable = false)
    @Builder.Default
    private Integer completionPercentage = 100;

    // Actual consumed values (can override calculated values)
    @Column(name = "actual_calories", precision = 8, scale = 2)
    private BigDecimal actualCalories;

    @Column(name = "actual_carbs", precision = 8, scale = 2)
    private BigDecimal actualCarbs;

    @Column(name = "actual_protein", precision = 8, scale = 2)
    private BigDecimal actualProtein;

    @Column(name = "actual_fat", precision = 8, scale = 2)
    private BigDecimal actualFat;

    @Column(name = "satisfaction_rating")
    private Integer satisfactionRating; // 1-5 stars

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "checked_in_at", nullable = false)
    @Builder.Default
    private LocalDateTime checkedInAt = LocalDateTime.now();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Helper methods
    public boolean isFullyConsumed() {
        return completionPercentage >= 100;
    }

    public boolean isPartiallyConsumed() {
        return completionPercentage > 0 && completionPercentage < 100;
    }

    public BigDecimal getEffectiveCalories() {
        if (actualCalories != null) return actualCalories;
        return meal.getTargetCalories()
                .multiply(BigDecimal.valueOf(completionPercentage))
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getEffectiveCarbs() {
        if (actualCarbs != null) return actualCarbs;
        return meal.getTargetCarbs()
                .multiply(BigDecimal.valueOf(completionPercentage))
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getEffectiveProtein() {
        if (actualProtein != null) return actualProtein;
        return meal.getTargetProtein()
                .multiply(BigDecimal.valueOf(completionPercentage))
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getEffectiveFat() {
        if (actualFat != null) return actualFat;
        return meal.getTargetFat()
                .multiply(BigDecimal.valueOf(completionPercentage))
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    public String getSatisfactionDescription() {
        if (satisfactionRating == null) return "Não avaliado";
        return switch (satisfactionRating) {
            case 1 -> "Muito insatisfeito";
            case 2 -> "Insatisfeito";
            case 3 -> "Neutro";
            case 4 -> "Satisfeito";
            case 5 -> "Muito satisfeito";
            default -> "Inválido";
        };
    }
}
