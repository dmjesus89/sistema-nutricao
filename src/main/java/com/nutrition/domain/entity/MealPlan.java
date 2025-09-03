package com.nutrition.domain.entity;

import com.nutrition.domain.entity.auth.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "meal_plans",
        uniqueConstraints = @UniqueConstraint(name = "uk_meal_plans_user_date",
                columnNames = {"user_id", "date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MealPlanStatus status = MealPlanStatus.ACTIVE;

    // Target Macros (from user profile calculations)
    @Column(name = "target_calories", nullable = false, precision = 8, scale = 2)
    private BigDecimal targetCalories;

    @Column(name = "target_carbs", nullable = false, precision = 8, scale = 2)
    private BigDecimal targetCarbs;

    @Column(name = "target_protein", nullable = false, precision = 8, scale = 2)
    private BigDecimal targetProtein;

    @Column(name = "target_fat", nullable = false, precision = 8, scale = 2)
    private BigDecimal targetFat;

    // Consumed Macros (calculated from meals + extra foods)
    @Column(name = "consumed_calories", nullable = false, precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal consumedCalories = BigDecimal.ZERO;

    @Column(name = "consumed_carbs", nullable = false, precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal consumedCarbs = BigDecimal.ZERO;

    @Column(name = "consumed_protein", nullable = false, precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal consumedProtein = BigDecimal.ZERO;

    @Column(name = "consumed_fat", nullable = false, precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal consumedFat = BigDecimal.ZERO;

    // Generation metadata
    @Column(name = "is_generated", nullable = false)
    @Builder.Default
    private Boolean isGenerated = true;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Relationships
    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Meal> meals;

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExtraFood> extraFoods;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper methods
    public BigDecimal getRemainingCalories() {
        return targetCalories.subtract(consumedCalories);
    }

    public BigDecimal getRemainingCarbs() {
        return targetCarbs.subtract(consumedCarbs);
    }

    public BigDecimal getRemainingProtein() {
        return targetProtein.subtract(consumedProtein);
    }

    public BigDecimal getRemainingFat() {
        return targetFat.subtract(consumedFat);
    }

    public double getCompletionPercentage() {
        if (targetCalories.compareTo(BigDecimal.ZERO) == 0) return 0;
        return consumedCalories.divide(targetCalories, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public boolean isCompleted() {
        return status == MealPlanStatus.COMPLETED;
    }

    public enum MealPlanStatus {
        ACTIVE, COMPLETED, CANCELLED, ARCHIVED
    }
}