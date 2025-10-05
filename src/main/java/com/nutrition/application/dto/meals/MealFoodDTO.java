package com.nutrition.application.dto.meals;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodDTO {

    @NotNull(message = "ID do alimento é obrigatório")
    @JsonProperty("foodId")
    private Long foodId;

    @NotNull(message = "Quantidade é obrigatória")
    @JsonProperty("quantity")
    private BigDecimal quantity; // quantidade em gramas ou na unidade de porção

    @JsonProperty("unit")
    private String unit; // "g", "ml", "porção", etc.

    @JsonProperty("foodName")
    private String foodName;

    @JsonProperty("calories")
    public BigDecimal calories;

    @JsonProperty("carbs")
    public BigDecimal carbs;

    @JsonProperty("protein")
    public BigDecimal protein;

    @JsonProperty("fat")
    public BigDecimal fat;

    @JsonProperty("fiber")
    public BigDecimal fiber;

    @JsonProperty("sodium")
    public BigDecimal sodium;

}
