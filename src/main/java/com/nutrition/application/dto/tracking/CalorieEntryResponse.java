package com.nutrition.application.dto.tracking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CalorieEntryResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("entryType")
    private String entryType;

    @JsonProperty("entryTypeDisplay")
    private String entryTypeDisplay;

    @JsonProperty("calories")
    private BigDecimal calories;

    @JsonProperty("carbs")
    private BigDecimal carbs;

    @JsonProperty("protein")
    private BigDecimal protein;

    @JsonProperty("fat")
    private BigDecimal fat;

    @JsonProperty("description")
    private String description;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("consumedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime consumedAt;

    @JsonProperty("foodId")
    private Long foodId;

    @JsonProperty("foodName")
    private String foodName;

    @JsonProperty("mealId")
    private Long mealId;

    @JsonProperty("mealName")
    private String mealName;

    @JsonProperty("quantityGrams")
    private BigDecimal quantityGrams;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String updatedAt;
}
