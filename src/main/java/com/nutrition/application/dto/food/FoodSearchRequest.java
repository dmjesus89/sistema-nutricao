package com.nutrition.application.dto.food;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodSearchRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("minCalories")
    private BigDecimal minCalories;

    @JsonProperty("maxCalories")
    private BigDecimal maxCalories;

    @JsonProperty("minProtein")
    private BigDecimal minProtein;

    @JsonProperty("maxCarbs")
    private BigDecimal maxCarbs;

    @JsonProperty("maxFat")
    private BigDecimal maxFat;

    @JsonProperty("minFiber")
    private BigDecimal minFiber;

    @JsonProperty("maxSodium")
    private BigDecimal maxSodium;

    @JsonProperty("verifiedOnly")
    private Boolean verifiedOnly;

    @JsonProperty("highProtein")
    private Boolean highProtein; // >= 20g per 100g

    @JsonProperty("lowCarb")
    private Boolean lowCarb; // <= 5g per 100g

    @JsonProperty("highFiber")
    private Boolean highFiber; // >= 6g per 100g

    @JsonProperty("userPreference")
    private String userPreference; // FAVORITE, DISLIKED, etc.
}
