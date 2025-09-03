package com.nutrition.application.dto.meals;

import com.nutrition.domain.entity.Meal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraFoodResponse {

    private Long id;
    private Long mealPlanId;
    private FoodSummaryResponse food;

    // Quantity and nutrition
    private BigDecimal quantityGrams;
    private String servingDescription;
    private BigDecimal calculatedCalories;
    private BigDecimal calculatedCarbs;
    private BigDecimal calculatedProtein;
    private BigDecimal calculatedFat;

    // Metadata
    private LocalDateTime consumedAt;
    private Meal.MealType mealTypeHint;
    private String mealTypeDisplay;
    private String notes;

    private LocalDateTime createdAt;
}
