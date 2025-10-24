package com.nutrition.application.dto.food;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleRequest {

    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Dosage time must be in HH:mm format (e.g., 08:00)")
    private String dosageTime;

    @Size(max = 50, message = "Label must not exceed 50 characters")
    private String label;
}
