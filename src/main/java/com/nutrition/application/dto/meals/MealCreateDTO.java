package com.nutrition.application.dto.meals;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealCreateDTO {

    @NotNull(message = "Horário da refeição é obrigatório")
    @JsonProperty("mealTime")
    private LocalTime mealTime;

    @NotBlank(message = "Nome da refeição é obrigatório")
    private String name;

    @NotEmpty(message = "Pelo menos um alimento deve ser adicionado à refeição")
    @Valid
    private List<MealFoodDTO> foods;


}