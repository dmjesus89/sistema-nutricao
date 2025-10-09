package com.nutrition.presentation.controller;

import com.nutrition.application.dto.config.ActivityLevelResponse;
import com.nutrition.application.dto.config.GoalResponse;
import com.nutrition.application.service.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Configuration", description = "Endpoints para obter configurações do sistema")
@SecurityRequirement(name = "Bearer Authentication")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping("/activity-levels")
    @Operation(summary = "Listar níveis de atividade", description = "Retorna todos os níveis de atividade disponíveis")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ActivityLevelResponse>> getActivityLevels() {
        log.info("Request to fetch activity levels");
        List<ActivityLevelResponse> activityLevels = configurationService.getActivityLevels();
        return ResponseEntity.ok(activityLevels);
    }

    @GetMapping("/goals")
    @Operation(summary = "Listar objetivos", description = "Retorna todos os objetivos disponíveis")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<GoalResponse>> getGoals() {
        log.info("Request to fetch goals");
        List<GoalResponse> goals = configurationService.getGoals();
        return ResponseEntity.ok(goals);
    }
}
