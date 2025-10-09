package com.nutrition.application.dto.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nutrition.domain.entity.config.GoalConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {


    @JsonProperty("id")
    private Long id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("calorieAdjustmentType")
    private String calorieAdjustmentType;

    @JsonProperty("calorieAdjustmentValue")
    private BigDecimal calorieAdjustmentValue;

    @JsonProperty("displayOrder")
    private Integer displayOrder;

    public static GoalResponse fromEntity(GoalConfig entity) {
        return GoalResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .calorieAdjustmentType(entity.getCalorieAdjustmentType().name())
                .calorieAdjustmentValue(entity.getCalorieAdjustmentValue())
                .displayOrder(entity.getDisplayOrder())
                .build();
    }
}
