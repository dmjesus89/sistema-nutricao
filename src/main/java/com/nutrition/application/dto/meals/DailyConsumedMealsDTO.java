package com.nutrition.application.dto.meals;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class DailyConsumedMealsDTO {

    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("meals")
    private List<ConsumedMealDTO> meals;

    @JsonProperty("nutritionalSummary")
    private DailyNutritionalSummary nutritionalSummary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyNutritionalSummary {

        @JsonProperty("totalCalories")
        private BigDecimal totalCalories;

        @JsonProperty("totalCarbs")
        private BigDecimal totalCarbs;

        @JsonProperty("totalProtein")
        private BigDecimal totalProtein;

        @JsonProperty("totalFat")
        private BigDecimal totalFat;

        @JsonProperty("totalFiber")
        private BigDecimal totalFiber;

        @JsonProperty("totalSodium")
        private BigDecimal totalSodium;

        @JsonProperty("mealsCount")
        private Integer mealsCount;
    }
}