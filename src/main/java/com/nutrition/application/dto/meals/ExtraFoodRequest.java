package com.nutrition.application.dto.meals;

import com.nutrition.domain.entity.Meal;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ExtraFoodRequest {

    @NotNull(message = "ID do alimento é obrigatório")
    private Long foodId;

    @NotNull(message = "Quantidade é obrigatória")
    @DecimalMin(value = "0.1", message = "Quantidade mínima: 0.1g")
    @DecimalMax(value = "2000.0", message = "Quantidade máxima: 2000g")
    private BigDecimal quantityGrams;

    @Size(max = 100, message = "Descrição da porção não pode exceder 100 caracteres")
    private String servingDescription;

    private Meal.MealType mealTypeHint;

    private LocalDateTime consumedAt;

    @Size(max = 500, message = "Notas não podem exceder 500 caracteres")
    private String notes;
}
