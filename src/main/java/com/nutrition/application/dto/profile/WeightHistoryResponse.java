package com.nutrition.application.dto.profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class WeightHistoryResponse {

    private Long id;

    private BigDecimal weight;

    @JsonProperty("recordedDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate recordedDate;

    private String notes;

    @JsonProperty("weightDifference")
    private BigDecimal weightDifference; // Diferença para o peso anterior

    @JsonProperty("isRecent")
    private Boolean isRecent;

    @JsonProperty("createdAt")
    private String createdAt;
}
