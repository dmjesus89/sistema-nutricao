package com.nutrition.application.dto.food;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesResponse {

    private List<FoodPreferenceResponse> foods;
    private List<SupplementPreferenceResponse> supplements;

    @JsonProperty("dietaryRestrictions")
    private List<DietaryRestrictionResponse> dietaryRestrictions;
}
