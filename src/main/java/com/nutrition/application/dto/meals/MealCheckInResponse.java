package com.nutrition.application.dto.meals;

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
public class MealCheckInResponse {

    private Long id;
    private Long mealId;
    private Long userId;

    private Integer completionPercentage;

    // Actual consumed values
    private BigDecimal actualCalories;
    private BigDecimal actualCarbs;
    private BigDecimal actualProtein;
    private BigDecimal actualFat;

    // Effective values (calculated based on completion percentage)
    private BigDecimal effectiveCalories;
    private BigDecimal effectiveCarbs;
    private BigDecimal effectiveProtein;
    private BigDecimal effectiveFat;

    private Integer satisfactionRating;
    private String satisfactionDescription;
    private String notes;

    private LocalDateTime checkedInAt;
    private LocalDateTime createdAt;
}
