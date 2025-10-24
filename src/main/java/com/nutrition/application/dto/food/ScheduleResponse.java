package com.nutrition.application.dto.food;

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

    private Long id;
    private String dosageTime; // Formatted as HH:mm
    private String label;
    private LocalDateTime createdAt;
}
