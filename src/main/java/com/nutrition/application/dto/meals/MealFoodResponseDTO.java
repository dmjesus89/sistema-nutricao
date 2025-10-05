package com.nutrition.application.dto.meals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nutrition.application.dto.food.FoodResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("food")
    private FoodResponse food;

    @JsonProperty("quantity")
    private Double quantity;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("totalCalories")
    private BigDecimal totalCalories;

    @JsonProperty("totalCarbs")
    private BigDecimal totalCarbs;

    @JsonProperty("totalProtein")
    private BigDecimal totalProtein;

    @JsonProperty("totalFat")
    private BigDecimal totalFat;

    @JsonProperty("totalFiber")
    private BigDecimal totalFiber;

    @JsonProperty("totalSodium")
    private BigDecimal totalSodium;

}