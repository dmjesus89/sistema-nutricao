package com.nutrition.application.dto.meals;

import com.nutrition.domain.entity.Meal;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanGenerationRequest {

    @NotNull(message = "Data é obrigatória")
    @Future(message = "Data deve ser futura")
    private LocalDate date;

    @DecimalMin(value = "800.0", message = "Calorias mínimas: 800")
    @DecimalMax(value = "5000.0", message = "Calorias máximas: 5000")
    private BigDecimal targetCalories;

    @DecimalMin(value = "0.0", message = "Carboidratos não podem ser negativos")
    private BigDecimal targetCarbs;

    @DecimalMin(value = "0.0", message = "Proteínas não podem ser negativas")
    private BigDecimal targetProtein;

    @DecimalMin(value = "0.0", message = "Gorduras não podem ser negativas")
    private BigDecimal targetFat;

    private List<Meal.MealType> preferredMealTypes;

    private List<Long> excludedFoodIds;

    private List<Long> priorityFoodIds;

    private String notes;

    @Builder.Default
    private Boolean useUserPreferences = true;

    @Builder.Default
    private Boolean respectDietaryRestrictions = true;
}
