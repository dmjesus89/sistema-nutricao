package com.nutrition.application.dto.meals;

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
public class MealFoodResponse {

    private Long id;
    private Long mealId;
    private FoodSummaryResponse food;

    // Quantities
    private BigDecimal quantityGrams;
    private BigDecimal consumedQuantity;
    private String servingDescription;

    // Calculated nutrition (for planned quantity)
    private BigDecimal calculatedCalories;
    private BigDecimal calculatedCarbs;
    private BigDecimal calculatedProtein;
    private BigDecimal calculatedFat;

    // Actual nutrition (for consumed quantity)
    private BigDecimal actualCalories;
    private BigDecimal actualCarbs;
    private BigDecimal actualProtein;
    private BigDecimal actualFat;

    // Status
    private Boolean isConsumed;
    private LocalDateTime consumedAt;
    private String notes;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
