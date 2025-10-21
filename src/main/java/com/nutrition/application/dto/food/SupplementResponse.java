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
public class SupplementResponse {

    private Long id;
    private String name;
    private String description;
    private String brand;
    private String category;

    @JsonProperty("categoryDisplay")
    private String categoryDisplay;

    private String form;

    @JsonProperty("formDisplay")
    private String formDisplay;

    @JsonProperty("servingSize")
    private BigDecimal servingSize;

    @JsonProperty("servingUnit")
    private String servingUnit;

    @JsonProperty("servingSizeDescription")
    private String servingSizeDescription;

    @JsonProperty("servingsPerContainer")
    private BigDecimal servingsPerContainer;

    @JsonProperty("caloriesPerServing")
    private BigDecimal caloriesPerServing;

    @JsonProperty("carbsPerServing")
    private BigDecimal carbsPerServing;

    @JsonProperty("proteinPerServing")
    private BigDecimal proteinPerServing;

    @JsonProperty("fatPerServing")
    private BigDecimal fatPerServing;

    @JsonProperty("mainIngredient")
    private String mainIngredient;

    @JsonProperty("ingredientAmount")
    private BigDecimal ingredientAmount;

    @JsonProperty("ingredientUnit")
    private String ingredientUnit;

    @JsonProperty("mainIngredientDescription")
    private String mainIngredientDescription;

    @JsonProperty("recommendedDosage")
    private String recommendedDosage;

    @JsonProperty("usageInstructions")
    private String usageInstructions;

    private String warnings;

    @JsonProperty("regulatoryInfo")
    private String regulatoryInfo;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("hasNutritionalValue")
    private Boolean hasNutritionalValue;

    @JsonProperty("userPreference")
    private String userPreference;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;
}
