package com.nutrition.application.dto.food;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFoodRequest {

    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String description;

    @Size(max = 100, message = "Marca deve ter no máximo 100 caracteres")
    private String brand;

    private String category;

    @Size(max = 50, message = "Código de barras deve ter no máximo 50 caracteres")
    private String barcode;

    @DecimalMin(value = "0.0", message = "Calorias devem ser positivas")
    @Digits(integer = 4, fraction = 2)
    @JsonProperty("caloriesPer100g")
    private BigDecimal caloriesPer100g;

    @DecimalMin(value = "0.0", message = "Carboidratos devem ser positivos")
    @Digits(integer = 3, fraction = 2)
    @JsonProperty("carbsPer100g")
    private BigDecimal carbsPer100g;

    @DecimalMin(value = "0.0", message = "Proteína deve ser positiva")
    @Digits(integer = 3, fraction = 2)
    @JsonProperty("proteinPer100g")
    private BigDecimal proteinPer100g;

    @DecimalMin(value = "0.0", message = "Gordura deve ser positiva")
    @Digits(integer = 3, fraction = 2)
    @JsonProperty("fatPer100g")
    private BigDecimal fatPer100g;

}
