package com.nutrition.application.dto.tracking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyWaterSummary {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
    @JsonProperty("targetAmount")
    private BigDecimal targetAmount;
    @JsonProperty("remainingMl")
    private BigDecimal remainingMl;
    @JsonProperty("percentageOfTarget")
    private Double percentageOfTarget;
    @JsonProperty("intakeCount")
    private Integer intakeCount;
    @JsonProperty("intakes")
    private List<WaterIntakeResponse> intakes;


}