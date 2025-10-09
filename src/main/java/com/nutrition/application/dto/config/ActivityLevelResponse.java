package com.nutrition.application.dto.config;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("id")
    private Long id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("multiplier")
    private BigDecimal multiplier;
    @JsonProperty("displayOrder")
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
