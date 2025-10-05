package com.nutrition.application.dto.tracking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualCalorieRequest {

    @NotNull(message = "Calorias são obrigatórias")
    @DecimalMin(value = "0.1", message = "Calorias devem ser positivas")
    @DecimalMax(value = "10000.0", message = "Calorias muito altas")
    private BigDecimal calories;

    @DecimalMin(value = "0.0", message = "Carboidratos não podem ser negativos")
    private BigDecimal carbs;

    @DecimalMin(value = "0.0", message = "Proteínas não podem ser negativas")
    private BigDecimal protein;

    @DecimalMin(value = "0.0", message = "Gorduras não podem ser negativas")
    private BigDecimal fat;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição muito longa")
    private String description;

    @Size(max = 1000, message = "Notas muito longas")
    private String notes;

    private LocalDate date; // Default: hoje

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime consumedAt; // Default: agora
}
