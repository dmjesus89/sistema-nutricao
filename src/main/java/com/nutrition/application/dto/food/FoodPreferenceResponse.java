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
public class FoodPreferenceResponse {

    private Long id;
    private FoodResponse food;

    @JsonProperty("preferenceType")
    private String preferenceType;

    @JsonProperty("preferenceDisplay")
    private String preferenceDisplay;

    private String notes;

    @JsonProperty("createdAt")
    private String createdAt;
}
