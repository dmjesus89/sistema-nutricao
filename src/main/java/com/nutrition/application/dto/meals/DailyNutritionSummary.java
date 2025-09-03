package com.nutrition.application.dto.meals;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyNutritionSummary {

    private LocalDate date;

    // Targets
    private BigDecimal targetCalories;
    private BigDecimal targetCarbs;
    private BigDecimal targetProtein;
    private BigDecimal targetFat;

    // Consumed from planned meals
    private BigDecimal plannedCalories;
    private BigDecimal plannedCarbs;
    private BigDecimal plannedProtein;
    private BigDecimal plannedFat;

    // Consumed from extra foods
    private BigDecimal extraCalories;
    private BigDecimal extraCarbs;
    private BigDecimal extraProtein;
    private BigDecimal extraFat;

    // Total consumed
    private BigDecimal totalCalories;
    private BigDecimal totalCarbs;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;

    // Remaining
    private BigDecimal remainingCalories;
    private BigDecimal remainingCarbs;
    private BigDecimal remainingProtein;
    private BigDecimal remainingFat;

    // Progress percentages
    private Double caloriesProgress;
    private Double carbsProgress;
    private Double proteinProgress;
    private Double fatProgress;

    // Meal completion
    private Integer completedMeals;
    private Integer totalMeals;
    private Double mealCompletionRate;
}