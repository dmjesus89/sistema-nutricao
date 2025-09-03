package com.nutrition.application.dto.meals;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanStatsResponse {

    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;

    // Plan completion
    private Long activePlans;
    private Long completedPlans;
    private Double averageCompletionPercentage;

    // Nutrition averages
    private BigDecimal averageCaloriesConsumed;
    private BigDecimal averageCarbsConsumed;
    private BigDecimal averageProteinConsumed;
    private BigDecimal averageFatConsumed;

    // Meal completion by type
    private Map<String, Long> mealTypeCompletionCounts;

    // Satisfaction
    private Double averageSatisfactionRating;
    private Map<Integer, Long> satisfactionDistribution;

    // Extra foods
    private Long totalExtraFoods;
    private BigDecimal totalExtraCalories;

    // Streaks
    private Integer currentStreak;
    private Integer longestStreak;
}
