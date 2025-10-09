package com.nutrition.application.dto.config;

import com.nutrition.domain.entity.config.ActivityLevelConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLevelResponse {

    private Long id;
    private String code;
    private String displayName;
    private String description;
    private BigDecimal multiplier;
    private Integer displayOrder;

    public static ActivityLevelResponse fromEntity(ActivityLevelConfig entity) {
        return ActivityLevelResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .multiplier(entity.getMultiplier())
                .displayOrder(entity.getDisplayOrder())
                .build();
    }
}
