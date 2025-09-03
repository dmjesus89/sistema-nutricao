package com.nutrition.application.dto.meals;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class MealCheckInRequest {

    @NotNull(message = "ID da refeição é obrigatório")
    private Long mealId;

    @Min(value = 0, message = "Percentual mínimo: 0")
    @Max(value = 100, message = "Percentual máximo: 100")
    @Builder.Default
    private Integer completionPercentage = 100;

    @DecimalMin(value = "0.0", message = "Calorias não podem ser negativas")
    private BigDecimal actualCalories;

    @DecimalMin(value = "0.0", message = "Carboidratos não podem ser negativos")
    private BigDecimal actualCarbs;

    @DecimalMin(value = "0.0", message = "Proteínas não podem ser negativas")
    private BigDecimal actualProtein;

    @DecimalMin(value = "0.0", message = "Gorduras não podem ser negativas")
    private BigDecimal actualFat;

    @Min(value = 1, message = "Avaliação mínima: 1")
    @Max(value = 5, message = "Avaliação máxima: 5")
    private Integer satisfactionRating;

    @Size(max = 500, message = "Notas não podem exceder 500 caracteres")
    private String notes;
}
