package com.nutrition.presentation.controller;

import com.nutrition.application.dto.tracking.CalorieEntryResponse;
import com.nutrition.application.dto.tracking.CalorieSummaryResponse;
import com.nutrition.application.dto.tracking.DailyCaloriesSummary;
import com.nutrition.application.dto.tracking.FoodCalorieRequest;
import com.nutrition.application.dto.tracking.ManualCalorieRequest;
import com.nutrition.application.dto.tracking.MealCalorieRequest;
import com.nutrition.application.service.CalorieTrackingService;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calories")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Calorie Tracking", description = "Rastreamento de calorias consumidas")
@SecurityRequirement(name = "Bearer Authentication")
public class CalorieTrackingController {

    private final CalorieTrackingService calorieTrackingService;

    @PostMapping("/manual")
    @Operation(summary = "Registrar calorias manualmente",
            description = "Permite registrar calorias de forma livre com descrição personalizada")
    @ApiResponse(responseCode = "201", description = "Calorias registradas com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "422", description = "Erro de validação")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CalorieEntryResponse> addManualCalories(
            @CurrentUser User user,
            @Valid @RequestBody ManualCalorieRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            log.info("Manual calorie entry request from user {}: {} calories",
                    user.getId(), request.getCalories());

            CalorieEntryResponse response = calorieTrackingService.addManualCalories(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error adding manual calories for user {}: {}", user.getId(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/balance/today")
    @Operation(summary = "Resumo calórico de hoje",
            description = "Retorna o resumo das calorias consumidas vs target para hoje")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CalorieSummaryResponse> getBalanceTodayCalorieSummary(@CurrentUser User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            log.info("Today's calorie summary request from user {}", user.getId());
            CalorieSummaryResponse summary = calorieTrackingService.getDailyCalorieSummary(user, LocalDate.now());
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            log.error("Error fetching today's calorie summary for user {}: {}", user.getId(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/food")
    @Operation(summary = "Registrar calorias de alimento",
            description = "Registra calorias baseado em um alimento específico e quantidade")
    @ApiResponse(responseCode = "201", description = "Calorias do alimento registradas com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Alimento não encontrado")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CalorieEntryResponse> addFoodCalories(@CurrentUser User user, @Valid @RequestBody FoodCalorieRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            log.info("Food calorie entry request from user {}: food {} with {}g",
                    user.getId(), request.getFoodId(), request.getQuantityGrams());

            CalorieEntryResponse response = calorieTrackingService.addFoodCalories(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error adding food calories for user {}: {}", user.getId(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/meal")
    @Operation(summary = "Registrar calorias de refeição",
            description = "Registra calorias baseado em uma refeição planejada e percentual consumido")
    @ApiResponse(responseCode = "201", description = "Calorias da refeição registradas com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Refeição não encontrada")
    @ApiResponse(responseCode = "403", description = "Refeição não pertence ao usuário")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CalorieEntryResponse> addMealCalories(
            @CurrentUser User user,
            @Valid @RequestBody MealCalorieRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            log.info("Meal calorie entry request from user {}: meal {} with {}%",
                    user.getId(), request.getMealId(),
                    request.getConsumptionPercentage() != null ? request.getConsumptionPercentage() : 100);

            CalorieEntryResponse response = calorieTrackingService.addMealCalories(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error adding meal calories for user {}: {}", user.getId(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/daily/{date}")
    @Operation(summary = "Obter entradas de uma data",
            description = "Lista todas as entradas de calorias de uma data específica")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CalorieEntryResponse>> getDailyEntries(
            @CurrentUser User user,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<CalorieEntryResponse> entries = calorieTrackingService.getDailyEntries(user, date);
            return ResponseEntity.ok(entries);

        } catch (Exception e) {
            log.error("Error fetching daily entries for user {} on {}: {}", user.getId(), date, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/today")
    @Operation(summary = "Obter entradas de hoje", description = "Lista todas as entradas de calorias de hoje")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CalorieEntryResponse>> getTodayEntries(@CurrentUser User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<CalorieEntryResponse> entries = calorieTrackingService.getDailyEntries(user, LocalDate.now());
            return ResponseEntity.ok(entries);

        } catch (Exception e) {
            log.error("Error fetching today's entries for user {}: {}", user.getId(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/summary/{date}")
    @Operation(summary = "Resumo diário de calorias", description = "Obtém resumo completo das calorias consumidas em uma data específica")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DailyCaloriesSummary> getDailySummary(
            @CurrentUser User user,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            DailyCaloriesSummary summary = calorieTrackingService.getDailySummary(user, date);
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            log.error("Error fetching daily summary for user {} on {}: {}", user.getId(), date, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/summary/daily/{date}")
    @Operation(summary = "Resumo calórico diário", description = "Retorna o resumo das calorias consumidas vs target para uma data específica")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DailyCaloriesSummary> getTodaySummary(@CurrentUser User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            DailyCaloriesSummary summary = calorieTrackingService.getDailySummary(user, LocalDate.now());
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            log.error("Error fetching today's summary for user {}: {}", user.getId(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/recent")
    @Operation(summary = "Entradas recentes", description = "Lista as entradas de calorias mais recentes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CalorieEntryResponse>> getRecentEntries(
            @CurrentUser User user,
            @RequestParam(defaultValue = "7")
            @Min(1) @Max(30)
            @Parameter(description = "Número de dias para buscar (1-30)") Integer days) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<CalorieEntryResponse> entries = calorieTrackingService.getRecentEntries(user, days);
            return ResponseEntity.ok(entries);

        } catch (Exception e) {
            log.error("Error fetching recent entries for user {}: {}", user.getId(), e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{entryId}")
    @Operation(summary = "Deletar entrada de calorias", description = "Remove uma entrada específica de calorias")
    @ApiResponse(responseCode = "204", description = "Entrada removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Entrada não encontrada")
    @ApiResponse(responseCode = "403", description = "Entrada não pertence ao usuário")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteEntry(
            @CurrentUser User user,
            @PathVariable @Positive Long entryId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            calorieTrackingService.deleteEntry(user, entryId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("Error deleting entry {} for user {}: {}", entryId, user.getId(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/balance/daily/{date}")
    @Operation(summary = "Resumo calórico diário", description = "Retorna o resumo das calorias consumidas vs target para uma data específica")
    @ApiResponse(responseCode = "200", description = "Resumo calórico retornado com sucesso")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CalorieSummaryResponse> getDailyCalorieSummary(
            @CurrentUser User user,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            log.info("Daily calorie summary request from user {} for date {}", user.getId(), date);
            CalorieSummaryResponse summary = calorieTrackingService.getDailyCalorieSummary(user, date);
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            log.error("Error fetching daily calorie summary for user {} on {}: {}",
                    user.getId(), date, e.getMessage());
            throw e;
        }
    }



    @GetMapping("/balance/weekly")
    @Operation(summary = "Resumo calórico semanal", description = "Retorna o resumo das calorias dos últimos 7 dias")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CalorieSummaryResponse>> getWeeklyCalorieSummary(@CurrentUser User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            log.info("Weekly calorie summary request from user {}", user.getId());
            List<CalorieSummaryResponse> weeklySummary = calorieTrackingService.getWeeklyCalorieSummary(user);
            return ResponseEntity.ok(weeklySummary);

        } catch (Exception e) {
            log.error("Error fetching weekly calorie summary for user {}: {}", user.getId(), e.getMessage());
            throw e;
        }
    }


}