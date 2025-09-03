package com.nutrition.application.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRecommendationsResponse {

    private List<String> recommendations;

    @JsonProperty("profileCompleteness")
    private Boolean profileCompleteness;

    @JsonProperty("completenessWarnings")
    private List<String> completenessWarnings;

    @JsonProperty("goalWarnings")
    private List<String> goalWarnings;

    @JsonProperty("currentBodyMassIndex")
    private BigDecimal currentBodyMassIndex;

    @JsonProperty("bodyMassIndexCategory")
    private String bodyMassIndexCategory;

    @JsonProperty("weeksToGoal")
    private Integer weeksToGoal;

    @JsonProperty("dailyWaterIntake")
    private BigDecimal dailyWaterIntake;
}