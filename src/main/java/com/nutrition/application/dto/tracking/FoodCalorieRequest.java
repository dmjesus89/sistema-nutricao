package com.nutrition.application.dto.tracking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodCalorieRequest {

    @NotNull(message = "ID do alimento é obrigatório")
    @Positive(message = "ID do alimento deve ser positivo")
    private Long foodId;

    @NotNull(message = "Quantidade é obrigatória")
    @DecimalMin(value = "0.1", message = "Quantidade deve ser positiva")
    @DecimalMax(value = "5000.0", message = "Quantidade muito alta")
    private BigDecimal quantityGrams;

    @Size(max = 1000, message = "Notas muito longas")
    private String notes;

    private LocalDate date; // Default: hoje

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime consumedAt; // Default: agora
}