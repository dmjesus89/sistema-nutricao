package com.nutrition.application.dto.food;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("dosageTime")
    private String dosageTime; // Formatted as HH:mm

    @JsonProperty("label")
    private String label;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}
