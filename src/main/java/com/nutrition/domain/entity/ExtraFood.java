package com.nutrition.domain.entity;

import com.nutrition.domain.entity.food.Food;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "extra_foods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class ExtraFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(name = "quantity_grams", nullable = false, precision = 8, scale = 2)
    private BigDecimal quantityGrams;

    @Column(name = "serving_description", length = 100)
    private String servingDescription;

    // Pre-calculated nutrition values for this quantity
    @Column(name = "calculated_calories", nullable = false, precision = 8, scale = 2)
    private BigDecimal calculatedCalories;

    @Column(name = "calculated_carbs", nullable = false, precision = 8, scale = 2)
    private BigDecimal calculatedCarbs;

    @Column(name = "calculated_protein", nullable = false, precision = 8, scale = 2)
    private BigDecimal calculatedProtein;

    @Column(name = "calculated_fat", nullable = false, precision = 8, scale = 2)
    private BigDecimal calculatedFat;

    @Column(name = "consumed_at", nullable = false)
    private LocalDateTime consumedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type_hint", length = 20)
    private Meal.MealType mealTypeHint;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public String getMealTypeDisplay() {
        return mealTypeHint != null ? mealTypeHint.getDisplayName() : "Extra";
    }
}


