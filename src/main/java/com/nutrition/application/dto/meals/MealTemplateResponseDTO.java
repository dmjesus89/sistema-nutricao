package com.nutrition.application.dto.meals;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealTemplateResponseDTO {

    private Long id;

    private String name;

    @JsonProperty("mealTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime mealTime;

    @JsonProperty("isTemplate")
    private Boolean isTemplate;

    @JsonProperty("isOneTime")
    private Boolean isOneTime;

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

    private List<MealFoodResponseDTO> foods;

    @JsonProperty("isConsumedToday")
    private Boolean isConsumedToday;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;
}
