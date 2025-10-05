package com.nutrition.application.dto.meals;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealHistoryDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyConsumedMealsDTO> dailyMeals;
    private Integer totalDays;
    private Integer totalMealsConsumed;
}