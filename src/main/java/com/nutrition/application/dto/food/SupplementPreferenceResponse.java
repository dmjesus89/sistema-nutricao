package com.nutrition.application.dto.food;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplementPreferenceResponse {

    private Long id;
    private SupplementResponse supplement;

    @JsonProperty("preferenceType")
    private String preferenceType;

    @JsonProperty("preferenceDisplay")
    private String preferenceDisplay;

    private String notes;

    @JsonProperty("dosageTime")
    private String dosageTime;

    @JsonProperty("frequency")
    private String frequency;

    @JsonProperty("daysOfWeek")
    private String daysOfWeek;

    @JsonProperty("emailReminderEnabled")
    private Boolean emailReminderEnabled;

    @JsonProperty("createdAt")
    private String createdAt;
}
