package com.nutrition.application.dto.tracking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyCaloriesSummary {
    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("totalCalories")
    private BigDecimal totalCalories;
    @JsonProperty("totalCarbs")
    private BigDecimal totalCarbs;
    @JsonProperty("totalProtein")
    private BigDecimal totalProtein;
    @JsonProperty("totalFat")
    private BigDecimal totalFat;
    @JsonProperty("targetCalories")
    private BigDecimal targetCalories;
    @JsonProperty("remainingCalories")
    private BigDecimal remainingCalories;
    @JsonProperty("progressPercentage")
    private Double progressPercentage;
    @JsonProperty("totalEntries")
    private Integer totalEntries;
    @JsonProperty("manualEntries")
    private Integer manualEntries;
    @JsonProperty("foodEntries")
    private Integer foodEntries;
    @JsonProperty("mealEntries")
    private Integer mealEntries;
    @JsonProperty("hydrationProgress")
    private Double hydrationProgress;
}