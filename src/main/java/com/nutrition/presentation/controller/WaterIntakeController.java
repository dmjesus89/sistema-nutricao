package com.nutrition.presentation.controller;

import com.nutrition.application.dto.tracking.DailyWaterSummary;
import com.nutrition.application.dto.tracking.WaterIntakeRequest;
import com.nutrition.application.dto.tracking.WaterIntakeResponse;
import com.nutrition.application.service.WaterIntakeService;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/water")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Water Intake", description = "Controle de consumo de água")
@SecurityRequirement(name = "Bearer Authentication")
public class WaterIntakeController {

    private final WaterIntakeService waterIntakeService;

    @PostMapping("/intake")
    @Operation(summary = "Registrar consumo de água",
            description = "Adiciona uma nova entrada de consumo de água")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WaterIntakeResponse> addWaterIntake(
            @CurrentUser User user,
            @Valid @RequestBody WaterIntakeRequest request) {

        log.info("Water intake request from user {}: {}ml", user.getId(), request.getAmount());
        WaterIntakeResponse response = waterIntakeService.addWaterIntake(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/summary/today")
    @Operation(summary = "Resumo de hoje",
            description = "Retorna o resumo do consumo de água de hoje")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DailyWaterSummary> getTodaySummary(@CurrentUser User user) {
        DailyWaterSummary summary = waterIntakeService.getTodaySummary(user);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary/{date}")
    @Operation(summary = "Resumo por data",
            description = "Retorna o resumo do consumo de água para uma data específica")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DailyWaterSummary> getDailySummary(
            @CurrentUser User user,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        DailyWaterSummary summary = waterIntakeService.getDailySummary(user, date);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/history")
    @Operation(summary = "Histórico dos últimos 10 dias",
            description = "Retorna resumos diários dos últimos 10 dias")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<DailyWaterSummary>> getDailyHistory(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<DailyWaterSummary> history = waterIntakeService.getDailyHistory(user, page, size);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/{intakeId}")
    @Operation(summary = "Remover registro",
            description = "Remove um registro específico de consumo de água")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteIntake(
            @CurrentUser User user,
            @PathVariable Long intakeId) {

        waterIntakeService.deleteIntake(user, intakeId);
        return ResponseEntity.noContent().build();
    }
}