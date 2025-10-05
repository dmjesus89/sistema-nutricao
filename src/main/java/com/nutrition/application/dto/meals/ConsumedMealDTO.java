package com.nutrition.application.dto.meals;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumedMealDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("mealTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss.SSS")
    private LocalTime mealTime;

    @JsonProperty("consumedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime consumedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("consumedDate")
    private LocalDate consumedDate;

    @JsonProperty("totalCalories")
    private BigDecimal totalCalories;

    @JsonProperty("totalCarbs")
    private BigDecimal totalCarbs;

    @JsonProperty("totalProtein")
    private BigDecimal totalProtein;

    @JsonProperty("totalFat")
    private BigDecimal totalFat;

    @JsonProperty("totalFiber")
    private BigDecimal totalFiber;

    @JsonProperty("totalSodium")
    private BigDecimal totalSodium;

    @JsonProperty("foods")
    private List<MealFoodDTO> foods;

    @JsonProperty("notes")
    private String notes;
}