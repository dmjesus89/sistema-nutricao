package com.nutrition.application.dto.food;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateFoodRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String description;

    @Size(max = 100, message = "Marca deve ter no máximo 100 caracteres")
    private String brand;

    @NotNull(message = "Categoria é obrigatória")
    private String category;

    @Size(max = 50, message = "Código de barras deve ter no máximo 50 caracteres")
    private String barcode;

    @NotNull(message = "Calorias por 100g são obrigatórias")
    @DecimalMin(value = "0.0", message = "Calorias devem ser positivas")
    @DecimalMax(value = "9999.99", message = "Calorias devem ser menores que 10000")
    @Digits(integer = 4, fraction = 2)
    @JsonProperty("caloriesPer100g")
    private BigDecimal caloriesPer100g;

    @NotNull(message = "Carboidratos por 100g são obrigatórios")
    @DecimalMin(value = "0.0", message = "Carboidratos devem ser positivos")
    @DecimalMax(value = "999.99", message = "Carboidratos devem ser menores que 1000")
    @Digits(integer = 3, fraction = 2)
    @JsonProperty("carbsPer100g")
    private BigDecimal carbsPer100g;

    @NotNull(message = "Proteína por 100g é obrigatória")
    @DecimalMin(value = "0.0", message = "Proteína deve ser positiva")
    @DecimalMax(value = "999.99", message = "Proteína deve ser menor que 1000")
    @Digits(integer = 3, fraction = 2)
    @JsonProperty("proteinPer100g")
    private BigDecimal proteinPer100g;

    @NotNull(message = "Gordura por 100g é obrigatória")
    @DecimalMin(value = "0.0", message = "Gordura deve ser positiva")
    @DecimalMax(value = "999.99", message = "Gordura deve ser menor que 1000")
    @Digits(integer = 3, fraction = 2)
    @JsonProperty("fatPer100g")
    private BigDecimal fatPer100g;

    @DecimalMin(value = "0.0", message = "Fibra deve ser positiva")
    @Digits(integer = 3, fraction = 2)
    @JsonProperty("fiberPer100g")
    private BigDecimal fiberPer100g;

    @DecimalMin(value = "0.0", message = "Açúcar deve ser positivo")
    @Digits(integer = 3, fraction = 2)
    @JsonProperty("sugarPer100g")
    private BigDecimal sugarPer100g;

    @DecimalMin(value = "0.0", message = "Sódio deve ser positivo")
    @Digits(integer = 6, fraction = 2)
    @JsonProperty("sodiumPer100g")
    private BigDecimal sodiumPer100g; // em mg

    @DecimalMin(value = "0.0", message = "Gordura saturada deve ser positiva")
    @Digits(integer = 3, fraction = 2)
    @JsonProperty("saturatedFatPer100g")
    private BigDecimal saturatedFatPer100g;

    @DecimalMin(value = "0.1", message = "Tamanho da porção deve ser positivo")
    @Digits(integer = 4, fraction = 2)
    @JsonProperty("servingSize")
    private BigDecimal servingSize;

    @Size(max = 100, message = "Descrição da porção deve ter no máximo 100 caracteres")
    @JsonProperty("servingDescription")
    private String servingDescription;

    @Size(max = 100, message = "Fonte deve ter no máximo 100 caracteres")
    private String source;
}
