package com.nutrition.application.dto.config;

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

    private Long id;
    private String code;
    private String displayName;
    private String description;
    private String calorieAdjustmentType;
    private BigDecimal calorieAdjustmentValue;
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
