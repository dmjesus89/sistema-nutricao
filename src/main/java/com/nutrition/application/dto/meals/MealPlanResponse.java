package com.nutrition.application.dto.meals;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.nutrition.domain.entity.MealPlan.MealPlanStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanResponse {

    private Long id;
    private Long userId;
    private LocalDate date;
    private MealPlanStatus status;

    // Target macros
    private BigDecimal targetCalories;
    private BigDecimal targetCarbs;
    private BigDecimal targetProtein;
    private BigDecimal targetFat;

    // Consumed macros
    private BigDecimal consumedCalories;
    private BigDecimal consumedCarbs;
    private BigDecimal consumedProtein;
    private BigDecimal consumedFat;

    // Remaining macros
    private BigDecimal remainingCalories;
    private BigDecimal remainingCarbs;
    private BigDecimal remainingProtein;
    private BigDecimal remainingFat;

    // Progress
    private Double completionPercentage;
    private Integer completedMeals;
    private Integer totalMeals;

    // Metadata
    private Boolean isGenerated;
    private LocalDateTime generatedAt;
    private String notes;

    // Related data
    private List<MealResponse> meals;
    private List<ExtraFoodResponse> extraFoods;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
