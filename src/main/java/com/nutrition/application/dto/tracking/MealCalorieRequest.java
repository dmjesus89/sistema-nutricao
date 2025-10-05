package com.nutrition.application.dto.tracking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class MealCalorieRequest {

    @JsonProperty("mealId")
    @NotNull(message = "ID da refeição é obrigatório")
    @Positive(message = "ID da refeição deve ser positivo")
    private Long mealId;

    @JsonProperty("consumptionPercentage")
    @DecimalMin(value = "0.1", message = "Percentual deve ser positivo")
    @DecimalMax(value = "100.0", message = "Percentual não pode exceder 100%")
    private BigDecimal consumptionPercentage; // Default: 100%

    @JsonProperty("notes")
    @Size(max = 1000, message = "Notas muito longas")
    private String notes;

    @JsonProperty("notes")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date; // Default: hoje

    @JsonProperty("notes")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime consumedAt; // Default: agora
}