package com.nutrition.application.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalDailyEnergyExpenditureCalculationResponse {

    @JsonProperty("basalMetabolicRate")
    private BigDecimal basalMetabolicRate;

    @JsonProperty("totalDailyEnergyExpenditure")
    private BigDecimal totalDailyEnergyExpenditure;

    @JsonProperty("dailyCalorieTarget")
    private BigDecimal dailyCalorieTarget;

    @JsonProperty("calculationMethod")
    private String calculationMethod;

    @JsonProperty("activityMultiplier")
    private Double activityMultiplier;

    @JsonProperty("calorieAdjustment")
    private Integer calorieAdjustment;

    @JsonProperty("calculatedAt")
    private String calculatedAt;
}
