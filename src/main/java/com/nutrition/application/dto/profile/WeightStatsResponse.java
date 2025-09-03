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
public class WeightStatsResponse {

    @JsonProperty("currentWeight")
    private BigDecimal currentWeight;

    @JsonProperty("targetWeight")
    private BigDecimal targetWeight;

    @JsonProperty("initialWeight")
    private BigDecimal initialWeight;

    @JsonProperty("minWeight")
    private BigDecimal minWeight;

    @JsonProperty("maxWeight")
    private BigDecimal maxWeight;

    @JsonProperty("totalWeightChange")
    private BigDecimal totalWeightChange; // Diferença entre peso atual e inicial

    @JsonProperty("weightToGoal")
    private BigDecimal weightToGoal; // Diferença entre peso atual e meta

    @JsonProperty("totalMeasurements")
    private Long totalMeasurements;

    @JsonProperty("daysTracking")
    private Long daysTracking;

    @JsonProperty("averageWeeklyChange")
    private BigDecimal averageWeeklyChange;
}

