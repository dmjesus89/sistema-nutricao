// WaterIntakeRequest.java
package com.nutrition.application.dto.tracking;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterIntakeRequest {

    @JsonProperty("amount")
    @NotNull(message = "Quantidade de água é obrigatória")
    @DecimalMin(value = "0.1", message = "Quantidade deve ser maior que zero")
    private BigDecimal amount;

    @JsonProperty("notes")
    private String notes;
}