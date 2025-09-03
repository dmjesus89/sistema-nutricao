package com.nutrition.application.dto.meals;

import com.nutrition.domain.entity.food.Food;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodSummaryResponse {

    private Long id;
    private String name;
    private String brand;
    private Food.FoodCategory category;
    private String categoryDisplay;

    // Nutrition per 100g
    private BigDecimal caloriesPer100g;
    private BigDecimal carbsPer100g;
    private BigDecimal proteinPer100g;
    private BigDecimal fatPer100g;

    // Serving info
    private BigDecimal servingSize;
    private String servingUnit;

    private Boolean isVerified;
    private Boolean isActive;
}
