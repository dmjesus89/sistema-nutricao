package com.nutrition.application.dto.profile;

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
public class ProfileResponse {

    private Long id;

    @JsonProperty("birthDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private Integer age;

    private String gender;

    @JsonProperty("genderDisplay")
    private String genderDisplay;

    private BigDecimal height;

    @JsonProperty("currentWeight")
    private BigDecimal currentWeight;

    @JsonProperty("targetWeight")
    private BigDecimal targetWeight;

    @JsonProperty("targetDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate targetDate;

    @JsonProperty("activityLevel")
    private String activityLevel;

    @JsonProperty("activityLevelDisplay")
    private String activityLevelDisplay;

    private String goal;

    @JsonProperty("goalDisplay")
    private String goalDisplay;

    @JsonProperty("basalMetabolicRate")
    private BigDecimal basalMetabolicRate;

    @JsonProperty("totalDailyEnergyExpenditure")
    private BigDecimal totalDailyEnergyExpenditure;

    @JsonProperty("dailyCalorieTarget")
    private BigDecimal dailyCalorieTarget;

    @JsonProperty("bodyMassIndex")
    private BigDecimal bodyMassIndex;

    @JsonProperty("bodyMassIndexCategory")
    private String bodyMassIndexCategory;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;

    @JsonProperty("daysToTarget")
    private Long daysToTarget;

    @JsonProperty("recommendedWeeklyWeightChange")
    private BigDecimal recommendedWeeklyWeightChange;

    @JsonProperty("dailyWaterIntake")
    private BigDecimal dailyWaterIntake;

    @JsonProperty("warnings")
    private List<String> warnings;
}