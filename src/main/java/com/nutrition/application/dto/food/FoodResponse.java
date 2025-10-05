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
public class FoodResponse {

    private Long id;
    private String name;
    private String description;
    private String brand;
    private String category;

    @JsonProperty("categoryDisplay")
    private String categoryDisplay;

    private String barcode;

    @JsonProperty("caloriesPer100g")
    private BigDecimal caloriesPer100g;

    @JsonProperty("carbsPer100g")
    private BigDecimal carbsPer100g;

    @JsonProperty("proteinPer100g")
    private BigDecimal proteinPer100g;

    @JsonProperty("fatPer100g")
    private BigDecimal fatPer100g;

    @JsonProperty("fiberPer100g")
    private BigDecimal fiberPer100g;

    @JsonProperty("sugarPer100g")
    private BigDecimal sugarPer100g;

    @JsonProperty("sodiumPer100g")
    private BigDecimal sodiumPer100g;

    @JsonProperty("saturatedFatPer100g")
    private BigDecimal saturatedFatPer100g;

    @JsonProperty("quantityEquivalence")
    private String quantityEquivalence;

    @JsonProperty("servingSize")
    private BigDecimal servingSize;

    @JsonProperty("servingDescription")
    private String servingDescription;

    @JsonProperty("caloriesPerServing")
    private BigDecimal caloriesPerServing;

    @JsonProperty("carbsPerServing")
    private BigDecimal carbsPerServing;

    @JsonProperty("proteinPerServing")
    private BigDecimal proteinPerServing;

    @JsonProperty("fatPerServing")
    private BigDecimal fatPerServing;

    private String source;
    private Boolean verified;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("isHighProtein")
    private Boolean isHighProtein;

    @JsonProperty("isHighFiber")
    private Boolean isHighFiber;

    @JsonProperty("isLowSodium")
    private Boolean isLowSodium;

    @JsonProperty("userPreference")
    private String userPreference; // FAVORITE, RESTRICTION, AVOID, DISLIKE, etc.

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;
}
