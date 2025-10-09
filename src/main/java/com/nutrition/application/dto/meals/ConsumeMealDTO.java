package com.nutrition.application.dto.meals;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumeMealDTO {

    @JsonProperty("consumptionDate")
    private LocalDate consumptionDate;

    private String notes;
}
