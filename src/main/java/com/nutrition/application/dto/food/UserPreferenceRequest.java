package com.nutrition.application.dto.food;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceRequest {

    @NotNull(message = "Tipo de preferência é obrigatório")
    @JsonProperty("preferenceType")
    private String preferenceType;

    @Size(max = 500, message = "Notas devem ter no máximo 500 caracteres")
    private String notes;
}
