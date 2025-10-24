package com.nutrition.application.dto.food;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating supplement frequency and reminder settings
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSupplementFrequencyRequest {

    @NotBlank(message = "Frequência é obrigatória")
    @Pattern(regexp = "DAILY|WEEKLY|TWICE_WEEKLY|THREE_TIMES_WEEKLY|MONTHLY",
            message = "Frequência inválida")
    @JsonProperty("frequency")
    private String frequency;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("dosageTime")
    private String dosageTime; // HH:mm format

    @JsonProperty("daysOfWeek")
    private String daysOfWeek; // Comma-separated: "MONDAY,WEDNESDAY,FRIDAY"

    @JsonProperty("emailReminderEnabled")
    @Builder.Default
    private Boolean emailReminderEnabled = false;
}
