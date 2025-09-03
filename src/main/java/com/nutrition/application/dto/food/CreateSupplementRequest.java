package com.nutrition.application.dto.food;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CreateSupplementRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String description;

    @Size(max = 100, message = "Marca deve ter no máximo 100 caracteres")
    private String brand;

    @NotNull(message = "Categoria é obrigatória")
    private String category;

    @NotNull(message = "Forma é obrigatória")
    private String form;

    @NotNull(message = "Tamanho da porção é obrigatório")
    @DecimalMin(value = "0.01", message = "Tamanho da porção deve ser positivo")
    @Digits(integer = 6, fraction = 2)
    @JsonProperty("servingSize")
    private BigDecimal servingSize;

    @NotNull(message = "Unidade da porção é obrigatória")
    @JsonProperty("servingUnit")
    private String servingUnit;

    @DecimalMin(value = "0.1", message = "Porções por embalagem devem ser positivas")
    @Digits(integer = 4, fraction = 0)
    @JsonProperty("servingsPerContainer")
    private BigDecimal servingsPerContainer;

    @DecimalMin(value = "0.0", message = "Calorias devem ser positivas")
    @Digits(integer = 4, fraction = 2)
    @JsonProperty("caloriesPerServing")
    private BigDecimal caloriesPerServing;

    @DecimalMin(value = "0.0", message = "Carboidratos devem ser positivos")
    @Digits(integer = 4, fraction = 2)
    @JsonProperty("carbsPerServing")
    private BigDecimal carbsPerServing;

    @DecimalMin(value = "0.0", message = "Proteína deve ser positiva")
    @Digits(integer = 4, fraction = 2)
    @JsonProperty("proteinPerServing")
    private BigDecimal proteinPerServing;

    @DecimalMin(value = "0.0", message = "Gordura deve ser positiva")
    @Digits(integer = 4, fraction = 2)
    @JsonProperty("fatPerServing")
    private BigDecimal fatPerServing;

    @Size(max = 200, message = "Ingrediente principal deve ter no máximo 200 caracteres")
    @JsonProperty("mainIngredient")
    private String mainIngredient;

    @DecimalMin(value = "0.0", message = "Quantidade do ingrediente deve ser positiva")
    @Digits(integer = 8, fraction = 2)
    @JsonProperty("ingredientAmount")
    private BigDecimal ingredientAmount;

    @Size(max = 20, message = "Unidade do ingrediente deve ter no máximo 20 caracteres")
    @JsonProperty("ingredientUnit")
    private String ingredientUnit;

    @Size(max = 500, message = "Dosagem recomendada deve ter no máximo 500 caracteres")
    @JsonProperty("recommendedDosage")
    private String recommendedDosage;

    @Size(max = 1000, message = "Instruções de uso devem ter no máximo 1000 caracteres")
    @JsonProperty("usageInstructions")
    private String usageInstructions;

    @Size(max = 1000, message = "Avisos devem ter no máximo 1000 caracteres")
    private String warnings;

    @Size(max = 500, message = "Informações regulatórias devem ter no máximo 500 caracteres")
    @JsonProperty("regulatoryInfo")
    private String regulatoryInfo;
}

