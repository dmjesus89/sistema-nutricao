


package com.nutrition.domain.entity;


import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "meals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Target Macros (calculated from meal foods)
    @Column(name = "target_calories", nullable = false, precision = 8, scale = 2)
    private BigDecimal targetCalories;

    @Column(name = "target_carbs", nullable = false, precision = 8, scale = 2)
    private BigDecimal targetCarbs;

    @Column(name = "target_protein", nullable = false, precision = 8, scale = 2)
    private BigDecimal targetProtein;

    @Column(name = "target_fat", nullable = false, precision = 8, scale = 2)
    private BigDecimal targetFat;

    // Consumed Macros (from check-ins)
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

    // Completion status
    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    @Column(name = "order_index", nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;

    // Relationships
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealFood> mealFoods;

    @OneToOne(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MealCheckIn checkIn;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isCheckedIn() {
        return checkIn != null;
    }

    public double getCompletionPercentage() {
        if (checkIn == null) return 0.0;
        return checkIn.getCompletionPercentage();
    }

    public void complete() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
    }

    public enum MealType {
        BREAKFAST("Café da Manhã"),
        MORNING_SNACK("Lanche da Manhã"),
        LUNCH("Almoço"),
        AFTERNOON_SNACK("Lanche da Tarde"),
        DINNER("Jantar"),
        EVENING_SNACK("Lanche da Noite"),
        PRE_WORKOUT("Pré-Treino"),
        POST_WORKOUT("Pós-Treino");

        private final String displayName;

        MealType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}


