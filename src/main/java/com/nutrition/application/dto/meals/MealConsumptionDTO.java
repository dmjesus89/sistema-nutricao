package com.nutrition.application.dto.meals;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealConsumptionDTO {
    private Boolean consumed;
    private String notes;
}