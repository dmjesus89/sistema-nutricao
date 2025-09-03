package com.nutrition.application.dto.meals;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodUpdateRequest {

    @NotNull(message = "ID do alimento da refeição é obrigatório")
    private Long mealFoodId;

    @DecimalMin(value = "0.0", message = "Quantidade consumida não pode ser negativa")
    @DecimalMax(value = "2000.0", message = "Quantidade máxima: 2000g")
    private BigDecimal consumedQuantity;

    @Builder.Default
    private Boolean isConsumed = true;

    @Size(max = 500, message = "Notas não podem exceder 500 caracteres")
    private String notes;
}
