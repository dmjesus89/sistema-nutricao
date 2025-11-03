package com.nutrition.presentation.controller;

import com.nutrition.application.dto.profile.CreateProfileRequest;
import com.nutrition.application.dto.profile.ProfileRecommendationsResponse;
import com.nutrition.application.dto.profile.ProfileResponse;
import com.nutrition.application.dto.profile.TotalDailyEnergyExpenditureCalculationResponse;
import com.nutrition.application.dto.profile.UpdateProfileRequest;
import com.nutrition.application.dto.profile.WeightHistoryResponse;
import com.nutrition.application.dto.profile.WeightStatsResponse;
import com.nutrition.application.dto.profile.WeightUpdateRequest;
import com.nutrition.application.service.ProfileService;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Profile", description = "Endpoints para gestão do perfil do usuário")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    @Operation(summary = "Criar perfil", description = "Cria o perfil inicial do usuário com dados básicos")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProfileResponse> createProfile(@Valid @CurrentUser User user, @RequestBody CreateProfileRequest request) {
        log.info("Profile creation request received");
        ProfileResponse response = profileService.createProfile(user, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(summary = "Atualizar perfil", description = "Atualiza dados do perfil do usuário")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @CurrentUser User user, @RequestBody UpdateProfileRequest request) {
        log.info("Profile update request received");
        ProfileResponse response = profileService.updateProfile(user, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Obter perfil", description = "Retorna o perfil completo do usuário atual")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProfileResponse> getProfile(@CurrentUser User user) {
        log.info("Profile retrieval request received");
        ProfileResponse response = profileService.getProfile(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "Remover perfil", description = "Remover perfil do usuário")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@Valid @CurrentUser User user) {
        log.info("Profile delete request received");
        profileService.deleteProfile(user);
    }

    @PostMapping("/weight")
    @Operation(summary = "Atualizar peso", description = "Adiciona ou atualiza registro de peso")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WeightHistoryResponse> updateWeight(
            @Valid @CurrentUser User user, @RequestBody WeightUpdateRequest request) {
        log.info("Weight update request received");
        WeightHistoryResponse response = profileService.updateWeight(user, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weight/history")
    @Operation(summary = "Histórico de peso", description = "Retorna histórico completo de peso do usuário")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<WeightHistoryResponse>> getWeightHistory(@CurrentUser User user) {
        log.info("Weight history request received");
        List<WeightHistoryResponse> response = profileService.getWeightHistory(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weight/stats")
    @Operation(summary = "Estatísticas de peso", description = "Retorna estatísticas e métricas do progresso de peso")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WeightStatsResponse> getWeightStats(@CurrentUser User user) {
        log.info("Weight stats request received");
        WeightStatsResponse response = profileService.getWeightStats(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/weight/{id}")
    @Operation(summary = "Remover peso", description = "Remove um registro específico de peso do histórico")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeightRecord(@CurrentUser User user, @PathVariable Long id) {
        log.info("Weight delete request received for id: {}", id);
        profileService.deleteWeightRecord(user, id);
    }

    @GetMapping("/totalDailyEnergyExpenditure")
    @Operation(summary = "Cálculo TotalDailyEnergyExpenditure", description = "Retorna cálculos detalhados de TotalDailyEnergyExpenditure e calorias diárias")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TotalDailyEnergyExpenditureCalculationResponse> getTotalDailyEnergyExpenditureCalculation(@CurrentUser User user) {
        log.info("TotalDailyEnergyExpenditure calculation request received");
        TotalDailyEnergyExpenditureCalculationResponse response = profileService.getTotalDailyEnergyExpenditureCalculation(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Obter recomendações", description = "Retorna recomendações personalizadas baseadas no perfil")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProfileRecommendationsResponse> getRecommendations(@CurrentUser User user) {
        log.info("Profile recommendations request received");
        ProfileRecommendationsResponse response = profileService.getRecommendations(user);
        return ResponseEntity.ok(response);
    }
}