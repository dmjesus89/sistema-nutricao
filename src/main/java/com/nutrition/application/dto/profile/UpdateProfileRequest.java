package com.nutrition.application.dto.profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
public class UpdateProfileRequest {

    @Past(message = "Data de nascimento deve ser no passado")
    @JsonProperty("birthDate")
    private LocalDate birthDate;

    private String gender;

    @DecimalMin(value = "100.0", message = "Altura deve ser no mínimo 100 cm")
    @DecimalMax(value = "250.0", message = "Altura deve ser no máximo 250 cm")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal height;

    @NotNull(message = "Peso atual é obrigatório")
    @DecimalMin(value = "30.0", message = "Peso deve ser no mínimo 30 kg")
    @DecimalMax(value = "300.0", message = "Peso deve ser no máximo 300 kg")
    @Digits(integer = 3, fraction = 2)
    @JsonProperty("currentWeight")
    private BigDecimal currentWeight;

    @DecimalMin(value = "30.0", message = "Peso deve ser no mínimo 30 kg")
    @DecimalMax(value = "300.0", message = "Peso deve ser no máximo 300 kg")
    @Digits(integer = 3, fraction = 2)
    @JsonProperty("targetWeight")
    private BigDecimal targetWeight;

    @NotNull(message = "Data Alvo é obrigatória")
    @Future(message = "Data de Alvo deve ser no futuro")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("targetDate")
    private LocalDate targetDate;

    @JsonProperty("activityLevel")
    private String activityLevel;

    private String goal;

    @JsonProperty("basalMetabolicRate")
    private BigDecimal basalMetabolicRate;

    @JsonProperty("totalDailyEnergyExpenditure")
    private BigDecimal totalDailyEnergyExpenditure;

    @JsonProperty("dailyCalorieTarget")
    private BigDecimal dailyCalorieTarget;
}
