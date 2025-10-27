package com.nutrition.application.service;

import com.nutrition.application.dto.config.ActivityLevelResponse;
import com.nutrition.application.dto.config.GoalResponse;
import com.nutrition.infrastructure.repository.ActivityLevelConfigRepository;
import com.nutrition.infrastructure.repository.GoalConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationService {

    private final ActivityLevelConfigRepository activityLevelConfigRepository;
    private final GoalConfigRepository goalConfigRepository;

    @Transactional(readOnly = true)
    public List<ActivityLevelResponse> getActivityLevels() {
        log.info("Fetching all active activity levels");
        return activityLevelConfigRepository.findByActiveOrderByDisplayOrder(true)
                .stream()
                .map(ActivityLevelResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getGoals() {
        log.info("Fetching all active goals");
        return goalConfigRepository.findByActiveOrderByDisplayOrder(true)
                .stream()
                .map(GoalResponse::fromEntity)
                .collect(Collectors.toList());
    }


}
