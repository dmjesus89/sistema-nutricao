package com.nutrition.application.dto.food;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for user supplement tracking with frequency information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSupplementResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("supplement")
    private SupplementResponse supplement;

    @JsonProperty("frequency")
    private String frequency;

    @JsonProperty("frequencyDisplay")
    private String frequencyDisplay;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("dosageTime")
    private String dosageTime; // HH:mm format

    @JsonProperty("daysOfWeek")
    private String daysOfWeek;

    @JsonProperty("emailReminderEnabled")
    private Boolean emailReminderEnabled;

    @JsonProperty("lastTakenAt")
    private LocalDateTime lastTakenAt;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
