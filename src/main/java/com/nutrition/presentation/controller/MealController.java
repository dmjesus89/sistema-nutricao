package com.nutrition.presentation.controller;

import com.nutrition.application.dto.meals.DailyConsumedMealsDTO;
import com.nutrition.application.dto.meals.MealConsumptionDTO;
import com.nutrition.application.dto.meals.MealConsumptionResponseDTO;
import com.nutrition.application.dto.meals.MealCreateDTO;
import com.nutrition.application.dto.meals.MealHistoryDTO;
import com.nutrition.application.dto.meals.MealResponseDTO;
import com.nutrition.application.service.MealService;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.security.CurrentUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


@RestController
@RequestMapping("/api/v1/meals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Profile", description = "Endpoints para gestão do perfil do usuário")
@SecurityRequirement(name = "Bearer Authentication")
public class MealController {

    private final MealService mealService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MealResponseDTO> createMeal(@CurrentUser User user, @Valid @RequestBody MealCreateDTO createDTO) {
        MealResponseDTO meal = mealService.createMeal(createDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(meal);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MealResponseDTO>> getUserMeals(@CurrentUser User currentUser) {
        log.info("Fetching meals for user: {}", currentUser.getId());
        List<MealResponseDTO> meals = mealService.getUserMeals(currentUser);
        return ResponseEntity.ok(meals);
    }

    @PutMapping("/{mealId}/consumption")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MealConsumptionResponseDTO> toggleMealConsumption(
            @PathVariable Long mealId,
            @CurrentUser User currentUser,
            @Valid @RequestBody MealConsumptionDTO consumptionDTO) {

        MealConsumptionResponseDTO response = mealService.toggleMealConsumption(mealId, currentUser, consumptionDTO);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/consumed/today")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DailyConsumedMealsDTO> getTodayConsumedMeals(@CurrentUser User currentUser) {
        log.info("Fetching today's consumed meals for user: {}", currentUser.getId());
        DailyConsumedMealsDTO consumedMeals = mealService.getTodayConsumedMeals(currentUser);
        return ResponseEntity.ok(consumedMeals);
    }

    @GetMapping("/consumed/date")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DailyConsumedMealsDTO> getConsumedMealsForDate(
            @CurrentUser User currentUser,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Fetching consumed meals for user: {} on date: {}", currentUser.getId(), date);
        DailyConsumedMealsDTO consumedMeals = mealService.getConsumedMealsForDate(currentUser, date);
        return ResponseEntity.ok(consumedMeals);
    }

    @GetMapping("/consumed/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MealHistoryDTO> getMealHistory(
            @CurrentUser User currentUser,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Fetching meal history for user: {} from {} to {}",
                currentUser.getId(), startDate, endDate);
        MealHistoryDTO history = mealService.getMealHistory(currentUser, startDate, endDate);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/consumed/recent")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MealHistoryDTO> getRecentMealHistory(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "7") int days) {

        log.info("Fetching recent {} days meal history for user: {}", days, currentUser.getId());
        MealHistoryDTO history = mealService.getRecentMealHistory(currentUser, days);
        return ResponseEntity.ok(history);
    }
}