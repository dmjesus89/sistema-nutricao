package com.nutrition.application.dto.tracking;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class CalorieSummaryResponse {

    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("dailyCalorieTarget")
    private BigDecimal dailyCalorieTarget;

    @JsonProperty("totalCaloriesConsumed")
    private BigDecimal totalCaloriesConsumed;

    @JsonProperty("calorieBalance")
    private BigDecimal calorieBalance; // target - consumed (positivo = deficit, negativo = excesso)

    @JsonProperty("calorieBalancePercentage")
    private BigDecimal calorieBalancePercentage; // % do target consumido

    @JsonProperty("status")
    private String status; // "DEFICIT", "SURPLUS", "ON_TARGET"

    @JsonProperty("statusDisplay")
    private String statusDisplay;

    @JsonProperty("remainingCalories")
    private BigDecimal remainingCalories; // calorias restantes para atingir o target

    @JsonProperty("totalEntries")
    private Integer totalEntries; // número total de entradas do dia

    // Breakdown por macronutrientes
    @JsonProperty("totalCarbs")
    private BigDecimal totalCarbs;

    @JsonProperty("totalProtein")
    private BigDecimal totalProtein;

    @JsonProperty("totalFat")
    private BigDecimal totalFat;
}