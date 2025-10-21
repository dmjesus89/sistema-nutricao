package com.nutrition.application.dto.food;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeRoutineRequest {

    @NotNull(message = "Dosage time is required")
    @JsonProperty("dosageTime")
    private String dosageTime; // HH:mm format

    @NotNull(message = "Frequency is required")
    @JsonProperty("frequency")
    private String frequency; // DAILY, WEEKLY, CUSTOM

    @JsonProperty("daysOfWeek")
    private String daysOfWeek; // Comma-separated days for CUSTOM frequency

    @JsonProperty("emailReminderEnabled")
    @Builder.Default
    private Boolean emailReminderEnabled = false;
}
