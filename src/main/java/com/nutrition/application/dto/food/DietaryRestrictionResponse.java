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
public class DietaryRestrictionResponse {

    private Long id;

    @JsonProperty("restrictionType")
    private String restrictionType;

    @JsonProperty("restrictionDisplay")
    private String restrictionDisplay;

    private String severity;

    @JsonProperty("severityDisplay")
    private String severityDisplay;

    private String notes;
    private Boolean active;

    @JsonProperty("createdAt")
    private String createdAt;
}
