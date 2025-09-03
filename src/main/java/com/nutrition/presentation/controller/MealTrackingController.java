
// ===== MealTrackingController.java =====
package com.nutrition.presentation.controller;

import com.nutrition.application.dto.meals.ExtraFoodRequest;
import com.nutrition.application.dto.meals.ExtraFoodResponse;
import com.nutrition.application.dto.meals.FoodSummaryResponse;
import com.nutrition.application.dto.meals.MealCheckInRequest;
import com.nutrition.application.dto.meals.MealCheckInResponse;
import com.nutrition.application.dto.meals.MealFoodResponse;
import com.nutrition.application.dto.meals.MealFoodUpdateRequest;
import com.nutrition.application.service.MealTrackingService;
import com.nutrition.domain.entity.ExtraFood;
import com.nutrition.domain.entity.MealCheckIn;
import com.nutrition.domain.entity.MealFood;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/meals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Meal Tracking", description = "Rastreamento e check-in de refeições")
@SecurityRequirement(name = "Bearer Authentication")
public class MealTrackingController {

    private final MealTrackingService mealTrackingService;

    @PostMapping("/checkin")
    @Operation(summary = "Check-in de refeição",
            description = "Registra o consumo de uma refeição")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MealCheckInResponse> checkInMeal(
            @CurrentUser User user,
            @Valid @RequestBody MealCheckInRequest request) {

        log.info("User {} checking in meal {}", user.getId(), request.getMealId());

        MealCheckIn checkIn = mealTrackingService.checkInMeal(user.getId(), request);
        MealCheckInResponse response = convertCheckInToResponse(checkIn);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{mealId}/checkin/quick")
    @Operation(summary = "Check-in rápido",
            description = "Check-in rápido com 100% de consumo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MealCheckInResponse> quickCheckIn(
            @CurrentUser User user,
            @PathVariable Long mealId) {

        MealCheckIn checkIn = mealTrackingService.quickCheckIn(user.getId(), mealId);
        MealCheckInResponse response = convertCheckInToResponse(checkIn);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{mealId}/checkin/partial")
    @Operation(summary = "Check-in parcial",
            description = "Check-in com percentual específico de consumo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MealCheckInResponse> partialCheckIn(
            @CurrentUser User user,
            @PathVariable Long mealId,
            @RequestParam @Parameter(description = "Percentual consumido (0-100)")
            Integer completionPercentage) {

        MealCheckIn checkIn = mealTrackingService.partialCheckIn(user.getId(), mealId, completionPercentage);
        MealCheckInResponse response = convertCheckInToResponse(checkIn);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/checkin/{checkInId}")
    @Operation(summary = "Atualizar check-in",
            description = "Atualiza um check-in existente")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MealCheckInResponse> updateCheckIn(
            @CurrentUser User user,
            @PathVariable Long checkInId,
            @Valid @RequestBody MealCheckInRequest request) {

        MealCheckIn checkIn = mealTrackingService.updateCheckIn(checkInId, user.getId(), request);
        MealCheckInResponse response = convertCheckInToResponse(checkIn);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/checkin/{checkInId}")
    @Operation(summary = "Cancelar check-in",
            description = "Remove um check-in e reseta o status da refeição")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteCheckIn(
            @CurrentUser User user,
            @PathVariable Long checkInId) {

        mealTrackingService.deleteCheckIn(checkInId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/extra")
    @Operation(summary = "Adicionar alimento extra",
            description = "Adiciona um alimento não planejado ao plano do dia")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExtraFoodResponse> addExtraFood(
            @CurrentUser User user,
            @RequestParam(required = false)
            @Parameter(description = "Data (padrão: hoje)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody ExtraFoodRequest request) {

        LocalDate targetDate = date != null ? date : LocalDate.now();

        ExtraFood extraFood = mealTrackingService.addExtraFood(user.getId(), targetDate, request);
        ExtraFoodResponse response = convertExtraFoodToResponse(extraFood);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/extra/{extraFoodId}")
    @Operation(summary = "Remover alimento extra",
            description = "Remove um alimento extra do plano")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeExtraFood(
            @CurrentUser User user,
            @PathVariable Long extraFoodId) {

        mealTrackingService.removeExtraFood(extraFoodId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/foods/{mealFoodId}")
    @Operation(summary = "Atualizar consumo de alimento",
            description = "Atualiza o status de consumo de um alimento específico da refeição")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MealFoodResponse> updateMealFoodConsumption(
            @CurrentUser User user,
            @PathVariable Long mealFoodId,
            @Valid @RequestBody MealFoodUpdateRequest request) {

        MealFood mealFood = mealTrackingService.updateMealFoodConsumption(user.getId(), request);
        MealFoodResponse response = convertMealFoodToResponse(mealFood);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkins")
    @Operation(summary = "Listar check-ins",
            description = "Obtém o histórico de check-ins do usuário")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MealCheckInResponse>> getCheckIns(
            @CurrentUser User user,
            @RequestParam(defaultValue = "7") Integer days,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusDays(days - 1);
        if (endDate == null) endDate = LocalDate.now();

        List<MealCheckIn> checkIns = mealTrackingService.getUserCheckIns(user.getId(), startDate, endDate);
        List<MealCheckInResponse> responses = checkIns.stream()
                .map(this::convertCheckInToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/checkins/today")
    @Operation(summary = "Check-ins de hoje",
            description = "Obtém os check-ins do dia atual")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MealCheckInResponse>> getTodayCheckIns(@CurrentUser User user) {

        List<MealCheckIn> checkIns = mealTrackingService.getTodayCheckIns(user.getId());
        List<MealCheckInResponse> responses = checkIns.stream()
                .map(this::convertCheckInToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/extra")
    @Operation(summary = "Listar alimentos extras",
            description = "Obtém o histórico de alimentos extras do usuário")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ExtraFoodResponse>> getExtraFoods(
            @CurrentUser User user,
            @RequestParam(defaultValue = "7") Integer days,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusDays(days - 1);
        if (endDate == null) endDate = LocalDate.now();

        List<ExtraFood> extraFoods = mealTrackingService.getUserExtraFoods(user.getId(), startDate, endDate);
        List<ExtraFoodResponse> responses = extraFoods.stream()
                .map(this::convertExtraFoodToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/extra/today")
    @Operation(summary = "Alimentos extras de hoje",
            description = "Obtém os alimentos extras do dia atual")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ExtraFoodResponse>> getTodayExtraFoods(@CurrentUser User user) {

        List<ExtraFood> extraFoods = mealTrackingService.getTodayExtraFoods(user.getId());
        List<ExtraFoodResponse> responses = extraFoods.stream()
                .map(this::convertExtraFoodToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/batch-checkin")
    @Operation(summary = "Check-in em lote",
            description = "Faz check-in de todas as refeições de um dia")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MealCheckInResponse>> batchCheckInDay(
            @CurrentUser User user,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "100")
            @Parameter(description = "Percentual padrão de consumo")
            Integer defaultCompletionPercentage) {

        LocalDate targetDate = date != null ? date : LocalDate.now();

        List<MealCheckIn> checkIns = mealTrackingService.batchCheckInDay(
                user.getId(), targetDate, defaultCompletionPercentage);

        List<MealCheckInResponse> responses = checkIns.stream()
                .map(this::convertCheckInToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/stats")
    @Operation(summary = "Estatísticas de refeições",
            description = "Obtém estatísticas de consumo e satisfação das refeições")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MealTrackingService.MealCompletionStats> getMealStats(
            @CurrentUser User user,
            @RequestParam(defaultValue = "30") Integer days) {

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        MealTrackingService.MealCompletionStats stats =
                mealTrackingService.getMealCompletionStats(user.getId(), startDate, endDate);

        return ResponseEntity.ok(stats);
    }

    // Helper methods
    private MealCheckInResponse convertCheckInToResponse(MealCheckIn checkIn) {
        return MealCheckInResponse.builder()
                .id(checkIn.getId())
                .mealId(checkIn.getMeal().getId())
                .userId(checkIn.getUser().getId())
                .completionPercentage(checkIn.getCompletionPercentage())
                .actualCalories(checkIn.getActualCalories())
                .actualCarbs(checkIn.getActualCarbs())
                .actualProtein(checkIn.getActualProtein())
                .actualFat(checkIn.getActualFat())
                .effectiveCalories(checkIn.getEffectiveCalories())
                .effectiveCarbs(checkIn.getEffectiveCarbs())
                .effectiveProtein(checkIn.getEffectiveProtein())
                .effectiveFat(checkIn.getEffectiveFat())
                .satisfactionRating(checkIn.getSatisfactionRating())
                .satisfactionDescription(checkIn.getSatisfactionDescription())
                .notes(checkIn.getNotes())
                .checkedInAt(checkIn.getCheckedInAt())
                .createdAt(checkIn.getCreatedAt())
                .build();
    }

    private ExtraFoodResponse convertExtraFoodToResponse(ExtraFood extraFood) {
        FoodSummaryResponse food = FoodSummaryResponse.builder()
                .id(extraFood.getFood().getId())
                .name(extraFood.getFood().getName())
                .brand(extraFood.getFood().getBrand())
                .category(extraFood.getFood().getCategory())
                .categoryDisplay(extraFood.getFood().getCategory().getDisplayName())
                .caloriesPer100g(extraFood.getFood().getCaloriesPer100g())
                .carbsPer100g(extraFood.getFood().getCarbsPer100g())
                .proteinPer100g(extraFood.getFood().getProteinPer100g())
                .fatPer100g(extraFood.getFood().getFatPer100g())
                .servingSize(extraFood.getFood().getServingSize())
                .servingUnit(extraFood.getFood().getServingUnit())
                .isVerified(extraFood.getFood().getIsVerified())
                .isActive(extraFood.getFood().getIsActive())
                .build();

        return ExtraFoodResponse.builder()
                .id(extraFood.getId())
                .mealPlanId(extraFood.getMealPlan().getId())
                .food(food)
                .quantityGrams(extraFood.getQuantityGrams())
                .servingDescription(extraFood.getServingDescription())
                .calculatedCalories(extraFood.getCalculatedCalories())
                .calculatedCarbs(extraFood.getCalculatedCarbs())
                .calculatedProtein(extraFood.getCalculatedProtein())
                .calculatedFat(extraFood.getCalculatedFat())
                .consumedAt(extraFood.getConsumedAt())
                .mealTypeHint(extraFood.getMealTypeHint())
                .mealTypeDisplay(extraFood.getMealTypeDisplay())
                .notes(extraFood.getNotes())
                .createdAt(extraFood.getCreatedAt())
                .build();
    }

    private MealFoodResponse convertMealFoodToResponse(MealFood mealFood) {
        FoodSummaryResponse food = FoodSummaryResponse.builder()
                .id(mealFood.getFood().getId())
                .name(mealFood.getFood().getName())
                .brand(mealFood.getFood().getBrand())
                .category(mealFood.getFood().getCategory())
                .categoryDisplay(mealFood.getFood().getCategory().getDisplayName())
                .caloriesPer100g(mealFood.getFood().getCaloriesPer100g())
                .carbsPer100g(mealFood.getFood().getCarbsPer100g())
                .proteinPer100g(mealFood.getFood().getProteinPer100g())
                .fatPer100g(mealFood.getFood().getFatPer100g())
                .servingSize(mealFood.getFood().getServingSize())
                .servingUnit(mealFood.getFood().getServingUnit())
                .isVerified(mealFood.getFood().getIsVerified())
                .isActive(mealFood.getFood().getIsActive())
                .build();

        return MealFoodResponse.builder()
                .id(mealFood.getId())
                .mealId(mealFood.getMeal().getId())
                .food(food)
                .quantityGrams(mealFood.getQuantityGrams())
                .consumedQuantity(mealFood.getConsumedQuantity())
                .servingDescription(mealFood.getServingDescription())
                .calculatedCalories(mealFood.getCalculatedCalories())
                .calculatedCarbs(mealFood.getCalculatedCarbs())
                .calculatedProtein(mealFood.getCalculatedProtein())
                .calculatedFat(mealFood.getCalculatedFat())
                .actualCalories(mealFood.getActualCalories())
                .actualCarbs(mealFood.getActualCarbs())
                .actualProtein(mealFood.getActualProtein())
                .actualFat(mealFood.getActualFat())
                .isConsumed(mealFood.getIsConsumed())
                .consumedAt(mealFood.getConsumedAt())
                .notes(mealFood.getNotes())
                .createdAt(mealFood.getCreatedAt())
                .updatedAt(mealFood.getUpdatedAt())
                .build();
    }
}