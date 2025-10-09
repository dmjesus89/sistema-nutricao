package com.nutrition.presentation.controller;

import com.nutrition.application.dto.meals.*;
import com.nutrition.application.service.MealService;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/meals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Meals", description = "Endpoints para gestão de refeições")
@SecurityRequirement(name = "Bearer Authentication")
public class MealController {

    private final MealService mealService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new meal template or one-time meal")
    public ResponseEntity<MealTemplateResponseDTO> createMeal(
            @CurrentUser User user,
            @Valid @RequestBody MealCreateDTO createDTO) {
        MealTemplateResponseDTO meal = mealService.createMeal(createDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(meal);
    }

    @PutMapping("/{mealId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update an existing meal template")
    public ResponseEntity<MealTemplateResponseDTO> updateMeal(
            @PathVariable Long mealId,
            @CurrentUser User user,
            @Valid @RequestBody MealUpdateDTO updateDTO) {
        MealTemplateResponseDTO meal = mealService.updateMeal(mealId, updateDTO, user);
        return ResponseEntity.ok(meal);
    }

    @DeleteMapping("/{mealId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete a meal template")
    public ResponseEntity<Void> deleteMeal(
            @PathVariable Long mealId,
            @CurrentUser User user) {
        mealService.deleteMeal(mealId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all meal templates for the current user")
    public ResponseEntity<List<MealTemplateResponseDTO>> getUserMealTemplates(@CurrentUser User user) {
        log.info("Fetching meal templates for user: {}", user.getId());
        List<MealTemplateResponseDTO> meals = mealService.getUserMealTemplates(user);
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/{mealId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get a specific meal by ID")
    public ResponseEntity<MealTemplateResponseDTO> getMealById(
            @PathVariable Long mealId,
            @CurrentUser User user) {
        MealTemplateResponseDTO meal = mealService.getMealById(mealId, user);
        return ResponseEntity.ok(meal);
    }

    @PostMapping("/{mealId}/consume")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Mark a meal as consumed for a specific date")
    public ResponseEntity<MealConsumptionResponseDTO> consumeMeal(
            @PathVariable Long mealId,
            @CurrentUser User user,
            @Valid @RequestBody(required = false) ConsumeMealDTO consumeDTO) {

        ConsumeMealDTO dto = consumeDTO != null ? consumeDTO : ConsumeMealDTO.builder().build();
        MealConsumptionResponseDTO response = mealService.consumeMeal(mealId, user, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{mealId}/consume")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Unmark a meal as consumed for a specific date")
    public ResponseEntity<MealConsumptionResponseDTO> unconsumeMeal(
            @PathVariable Long mealId,
            @CurrentUser User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        MealConsumptionResponseDTO response = mealService.unconsumeMeal(mealId, user, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/consumed/today")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all consumed meals for today")
    public ResponseEntity<DailyConsumedMealsDTO> getTodayConsumedMeals(@CurrentUser User user) {
        log.info("Fetching today's consumed meals for user: {}", user.getId());
        DailyConsumedMealsDTO consumedMeals = mealService.getTodayConsumedMeals(user);
        return ResponseEntity.ok(consumedMeals);
    }

    @GetMapping("/consumed/date")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all consumed meals for a specific date")
    public ResponseEntity<DailyConsumedMealsDTO> getConsumedMealsForDate(
            @CurrentUser User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Fetching consumed meals for user: {} on date: {}", user.getId(), date);
        DailyConsumedMealsDTO consumedMeals = mealService.getConsumedMealsForDate(user, date);
        return ResponseEntity.ok(consumedMeals);
    }

    @GetMapping("/consumed/history")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get meal consumption history for a date range")
    public ResponseEntity<MealHistoryDTO> getMealHistory(
            @CurrentUser User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Fetching meal history for user: {} from {} to {}",
                user.getId(), startDate, endDate);
        MealHistoryDTO history = mealService.getMealHistory(user, startDate, endDate);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/consumed/recent")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get recent meal consumption history")
    public ResponseEntity<MealHistoryDTO> getRecentMealHistory(
            @CurrentUser User user,
            @RequestParam(defaultValue = "7") int days) {

        log.info("Fetching recent {} days meal history for user: {}", days, user.getId());
        MealHistoryDTO history = mealService.getRecentMealHistory(user, days);
        return ResponseEntity.ok(history);
    }
}
