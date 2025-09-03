package com.nutrition.application.dto.profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightUpdateRequest {

    @NotNull(message = "Peso é obrigatório")
    @DecimalMin(value = "30.0", message = "Peso deve ser no mínimo 30 kg")
    @DecimalMax(value = "300.0", message = "Peso deve ser no máximo 300 kg")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal weight;

    @JsonProperty("recordedDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate recordedDate; // Se null, usa data atual

    @Size(max = 500, message = "Notas devem ter no máximo 500 caracteres")
    private String notes;
}
