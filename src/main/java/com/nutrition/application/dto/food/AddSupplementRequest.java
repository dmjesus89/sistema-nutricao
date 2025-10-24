package com.nutrition.application.dto.food;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for adding a supplement to user's tracking list
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddSupplementRequest {

    @NotBlank(message = "Frequência é obrigatória")
    @Pattern(regexp = "DAILY|WEEKLY|TWICE_WEEKLY|THREE_TIMES_WEEKLY|MONTHLY",
            message = "Frequência inválida")
    @JsonProperty("frequency")
    private String frequency;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("dosageTime")
    @Deprecated // Use schedules instead for multiple doses per day
    private String dosageTime; // HH:mm format - kept for backward compatibility

    @JsonProperty("daysOfWeek")
    private String daysOfWeek; // Comma-separated: "MONDAY,WEDNESDAY,FRIDAY"

    @NotNull(message = "emailReminderEnabled é obrigatório")
    @JsonProperty("emailReminderEnabled")
    @Builder.Default
    private Boolean emailReminderEnabled = false;

    /**
     * Multiple dosage schedules per day (NEW - preferred over dosageTime)
     * If provided, this takes precedence over dosageTime
     */
    @Valid
    @JsonProperty("schedules")
    private List<AddScheduleRequest> schedules;
}
