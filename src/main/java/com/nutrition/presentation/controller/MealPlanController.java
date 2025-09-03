package com.nutrition.presentation.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meal-plans")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Meal Plans", description = "Gestão de planos alimentares diários")
@SecurityRequirement(name = "Bearer Authentication")
public class MealPlanController {

//    private final MealPlanningService mealPlanningService;
//    private final MealTrackingService mealTrackingService;
//    private final MealPlanRepository mealPlanRepository;
//
//    @PostMapping("/generate")
//    @Operation(summary = "Gerar plano alimentar",
//            description = "Gera um novo plano alimentar personalizado baseado no perfil do usuário")
//    @ApiResponse(responseCode = "201", description = "Plano gerado com sucesso")
//    @ApiResponse(responseCode = "400", description = "Dados inválidos")
//    @ApiResponse(responseCode = "409", description = "Já existe plano para esta data")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<MealPlanResponse> generateMealPlan(
//            @CurrentUser User user,
//            @Valid @RequestBody MealPlanGenerationRequest request) {
//
//        log.info("User {} requesting meal plan generation for date {}", user.getId(), request.getDate());
//
//        MealPlan mealPlan = mealPlanningService.generateMealPlan(user, request);
//        MealPlanResponse response = convertToResponse(mealPlan);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @PostMapping("/generate/today")
//    @Operation(summary = "Gerar plano para hoje",
//            description = "Gera um plano alimentar para a data atual usando configurações padrão")
//    @ApiResponse(responseCode = "201", description = "Plano gerado com sucesso")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<MealPlanResponse> generateTodayMealPlan(@CurrentUser User user) {
//
//        MealPlan mealPlan = mealPlanningService.generateDailyMealPlan(user, LocalDate.now());
//        MealPlanResponse response = convertToResponse(mealPlan);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @PostMapping("/generate/tomorrow")
//    @Operation(summary = "Gerar plano para amanhã",
//            description = "Gera um plano alimentar para o dia seguinte")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<MealPlanResponse> generateTomorrowMealPlan(@CurrentUser User user) {
//
//        MealPlan mealPlan = mealPlanningService.generateDailyMealPlan(user, LocalDate.now().plusDays(1));
//        MealPlanResponse response = convertToResponse(mealPlan);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "Obter plano alimentar", description = "Busca um plano alimentar específico")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<MealPlanResponse> getMealPlan(
//            @CurrentUser User user,
//            @PathVariable Long id) {
//
//        MealPlan mealPlan = mealPlanRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Plano alimentar não encontrado"));
//
//        if (!mealPlan.getUser().getId().equals(user.getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        MealPlanResponse response = convertToResponse(mealPlan);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/date/{date}")
//    @Operation(summary = "Obter plano por data", description = "Busca o plano alimentar para uma data específica")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<MealPlanResponse> getMealPlanByDate(
//            @CurrentUser User user,
//            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//
//        return mealPlanRepository.findByUserAndDate(user, date)
//                .map(this::convertToResponse)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/today")
//    @Operation(summary = "Obter plano de hoje", description = "Busca o plano alimentar para a data atual")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<MealPlanResponse> getTodayMealPlan(@CurrentUser User user) {
//
//        return mealPlanRepository.findByUserAndDate(user, LocalDate.now())
//                .map(this::convertToResponse)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping
//    @Operation(summary = "Listar planos alimentares",
//            description = "Lista os planos alimentares do usuário com filtros opcionais")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<List<MealPlanResponse>> getMealPlans(
//            @CurrentUser User user,
//            @RequestParam(defaultValue = "7")
//            @Parameter(description = "Número de dias para buscar") Integer days,
//            @RequestParam(required = false)
//            @Parameter(description = "Data inicial (ISO)")
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam(required = false)
//            @Parameter(description = "Data final (ISO)")
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        List<MealPlan> mealPlans;
//
//        if (startDate != null && endDate != null) {
//            mealPlans = mealPlanRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);
//        } else {
//            LocalDate start = LocalDate.now().minusDays(days - 1);
//            LocalDate end = LocalDate.now();
//            mealPlans = mealPlanRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end);
//        }
//
//        List<MealPlanResponse> responses = mealPlans.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(responses);
//    }
//
//    @GetMapping("/recent")
//    @Operation(summary = "Planos recentes", description = "Obtém os 7 planos mais recentes do usuário")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<List<MealPlanResponse>> getRecentMealPlans(@CurrentUser User user) {
//
//        List<MealPlan> recentPlans = mealPlanRepository.findTop7ByUserOrderByDateDesc(user);
//        List<MealPlanResponse> responses = recentPlans.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(responses);
//    }
//
//    @PutMapping("/{id}/regenerate")
//    @Operation(summary = "Regenerar plano", description = "Regenera um plano alimentar existente")
//    @ApiResponse(responseCode = "200", description = "Plano regenerado com sucesso")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<MealPlanResponse> regenerateMealPlan(
//            @CurrentUser User user,
//            @PathVariable Long id) {
//
//        MealPlan regeneratedPlan = mealPlanningService.regenerateMealPlan(id, user.getId());
//        MealPlanResponse response = convertToResponse(regeneratedPlan);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Deletar plano", description = "Remove um plano alimentar")
//    @ApiResponse(responseCode = "204", description = "Plano removido com sucesso")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<Void> deleteMealPlan(
//            @CurrentUser User user,
//            @PathVariable Long id) {
//
//        mealPlanningService.deleteMealPlan(id, user.getId());
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/{id}/summary")
//    @Operation(summary = "Resumo do plano", description = "Obtém resumo nutricional do plano alimentar")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<DailyNutritionSummary> getMealPlanSummary(
//            @CurrentUser User user,
//            @PathVariable Long id) {
//
//        MealPlan mealPlan = mealPlanRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Plano alimentar não encontrado"));
//
//        if (!mealPlan.getUser().getId().equals(user.getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        DailyNutritionSummary summary = createNutritionSummary(mealPlan);
//        return ResponseEntity.ok(summary);
//    }
//
//    @GetMapping("/stats")
//    @Operation(summary = "Estatísticas dos planos",
//            description = "Obtém estatísticas dos planos alimentares do usuário")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<MealPlanStatsResponse> getMealPlanStats(
//            @CurrentUser User user,
//            @RequestParam(defaultValue = "30")
//            @Parameter(description = "Dias para análise") Integer days) {
//
//        LocalDate endDate = LocalDate.now();
//        LocalDate startDate = endDate.minusDays(days - 1);
//
//        MealPlanStatsResponse stats = generateMealPlanStats(user, startDate, endDate);
//        return ResponseEntity.ok(stats);
//    }
//
//    // Helper methods
//    private MealPlanResponse convertToResponse(MealPlan mealPlan) {
//        // Convert meals
//        List<MealResponse> meals = mealPlan.getMeals() != null ?
//                mealPlan.getMeals().stream()
//                        .map(this::convertMealToResponse)
//                        .collect(Collectors.toList()) : List.of();
//
//        // Convert extra foods
//        List<ExtraFoodResponse> extraFoods = mealPlan.getExtraFoods() != null ?
//                mealPlan.getExtraFoods().stream()
//                        .map(this::convertExtraFoodToResponse)
//                        .collect(Collectors.toList()) : List.of();
//
//        // Calculate progress
//        int completedMeals = (int) mealPlan.getMeals().stream()
//                .filter(meal -> meal.getIsCompleted())
//                .count();
//
//        return MealPlanResponse.builder()
//                .id(mealPlan.getId())
//                .userId(mealPlan.getUser().getId())
//                .date(mealPlan.getDate())
//                .status(mealPlan.getStatus())
//                .targetCalories(mealPlan.getTargetCalories())
//                .targetCarbs(mealPlan.getTargetCarbs())
//                .targetProtein(mealPlan.getTargetProtein())
//                .targetFat(mealPlan.getTargetFat())
//                .consumedCalories(mealPlan.getConsumedCalories())
//                .consumedCarbs(mealPlan.getConsumedCarbs())
//                .consumedProtein(mealPlan.getConsumedProtein())
//                .consumedFat(mealPlan.getConsumedFat())
//                .remainingCalories(mealPlan.getRemainingCalories())
//                .remainingCarbs(mealPlan.getRemainingCarbs())
//                .remainingProtein(mealPlan.getRemainingProtein())
//                .remainingFat(mealPlan.getRemainingFat())
//                .completionPercentage(mealPlan.getCompletionPercentage())
//                .completedMeals(completedMeals)
//                .totalMeals(mealPlan.getMeals() != null ? mealPlan.getMeals().size() : 0)
//                .isGenerated(mealPlan.getIsGenerated())
//                .generatedAt(mealPlan.getGeneratedAt())
//                .notes(mealPlan.getNotes())
//                .meals(meals)
//                .extraFoods(extraFoods)
//                .createdAt(mealPlan.getCreatedAt())
//                .updatedAt(mealPlan.getUpdatedAt())
//                .build();
//    }
//
//    private MealResponse convertMealToResponse(Meal meal) {
//        // Convert meal foods
//        List<MealFoodResponse> foods = meal.getMealFoods() != null ?
//                meal.getMealFoods().stream()
//                        .map(this::convertMealFoodToResponse)
//                        .collect(Collectors.toList()) : List.of();
//
//        // Convert check-in
//        MealCheckInResponse checkIn = meal.getCheckIn() != null ?
//                convertCheckInToResponse(meal.getCheckIn()) : null;
//
//        return MealResponse.builder()
//                .id(meal.getId())
//                .mealPlanId(meal.getMealPlan().getId())
//                .mealType(meal.getMealType())
//                .mealTypeDisplay(meal.getMealType().getDisplayName())
//                .name(meal.getName())
//                .description(meal.getDescription())
//                .targetCalories(meal.getTargetCalories())
//                .targetCarbs(meal.getTargetCarbs())
//                .targetProtein(meal.getTargetProtein())
//                .targetFat(meal.getTargetFat())
//                .consumedCalories(meal.getConsumedCalories())
//                .consumedCarbs(meal.getConsumedCarbs())
//                .consumedProtein(meal.getConsumedProtein())
//                .consumedFat(meal.getConsumedFat())
//                .isCompleted(meal.getIsCompleted())
//                .completedAt(meal.getCompletedAt())
//                .scheduledTime(meal.getScheduledTime())
//                .orderIndex(meal.getOrderIndex())
//                .isCheckedIn(meal.isCheckedIn())
//                .completionPercentage(meal.getCompletionPercentage())
//                .satisfactionRating(checkIn != null ? checkIn.getSatisfactionRating() : null)
//                .foods(foods)
//                .checkIn(checkIn)
//                .createdAt(meal.getCreatedAt())
//                .updatedAt(meal.getUpdatedAt())
//                .build();
//    }
//
//    private MealFoodResponse convertMealFoodToResponse(MealFood mealFood) {
//        FoodSummaryResponse food = FoodSummaryResponse.builder()
//                .id(mealFood.getFood().getId())
//                .name(mealFood.getFood().getName())
//                .brand(mealFood.getFood().getBrand())
//                .category(mealFood.getFood().getCategory())
//                .categoryDisplay(mealFood.getFood().getCategory().getDisplayName())
//                .caloriesPer100g(mealFood.getFood().getCaloriesPer100g())
//                .carbsPer100g(mealFood.getFood().getCarbsPer100g())
//                .proteinPer100g(mealFood.getFood().getProteinPer100g())
//                .fatPer100g(mealFood.getFood().getFatPer100g())
//                .servingSize(mealFood.getFood().getServingSize())
//                .servingUnit(mealFood.getFood().getServingUnit())
//                .isVerified(mealFood.getFood().getIsVerified())
//                .isActive(mealFood.getFood().getIsActive())
//                .build();
//
//        return MealFoodResponse.builder()
//                .id(mealFood.getId())
//                .mealId(mealFood.getMeal().getId())
//                .food(food)
//                .quantityGrams(mealFood.getQuantityGrams())
//                .consumedQuantity(mealFood.getConsumedQuantity())
//                .servingDescription(mealFood.getServingDescription())
//                .calculatedCalories(mealFood.getCalculatedCalories())
//                .calculatedCarbs(mealFood.getCalculatedCarbs())
//                .calculatedProtein(mealFood.getCalculatedProtein())
//                .calculatedFat(mealFood.getCalculatedFat())
//                .actualCalories(mealFood.getActualCalories())
//                .actualCarbs(mealFood.getActualCarbs())
//                .actualProtein(mealFood.getActualProtein())
//                .actualFat(mealFood.getActualFat())
//                .isConsumed(mealFood.getIsConsumed())
//                .consumedAt(mealFood.getConsumedAt())
//                .notes(mealFood.getNotes())
//                .createdAt(mealFood.getCreatedAt())
//                .updatedAt(mealFood.getUpdatedAt())
//                .build();
//    }
//
//    private ExtraFoodResponse convertExtraFoodToResponse(ExtraFood extraFood) {
//        FoodSummaryResponse food = FoodSummaryResponse.builder()
//                .id(extraFood.getFood().getId())
//                .name(extraFood.getFood().getName())
//                .brand(extraFood.getFood().getBrand())
//                .category(extraFood.getFood().getCategory())
//                .categoryDisplay(extraFood.getFood().getCategory().getDisplayName())
//                .caloriesPer100g(extraFood.getFood().getCaloriesPer100g())
//                .carbsPer100g(extraFood.getFood().getCarbsPer100g())
//                .proteinPer100g(extraFood.getFood().getProteinPer100g())
//                .fatPer100g(extraFood.getFood().getFatPer100g())
//                .servingSize(extraFood.getFood().getServingSize())
//                .servingUnit(extraFood.getFood().getServingUnit())
//                .isVerified(extraFood.getFood().getIsVerified())
//                .isActive(extraFood.getFood().getIsActive())
//                .build();
//
//
//        return ExtraFoodResponse.builder()
//                .id(extraFood.getId())
//                .mealPlanId(extraFood.getMealPlan().getId())
//                .food(food)
//                .quantityGrams(extraFood.getQuantityGrams())
//                .servingDescription(extraFood.getServingDescription())
//                .calculatedCalories(extraFood.getCalculatedCalories())
//                .calculatedCarbs(extraFood.getCalculatedCarbs())
//                .calculatedProtein(extraFood.getCalculatedProtein())
//                .calculatedFat(extraFood.getCalculatedFat())
//                .consumedAt(extraFood.getConsumedAt())
//                .mealTypeHint(extraFood.getMealTypeHint())
//                .mealTypeDisplay(extraFood.getMealTypeDisplay())
//                .notes(extraFood.getNotes())
//                .createdAt(extraFood.getCreatedAt())
//                .build();
//    }
//
//    private MealCheckInResponse convertCheckInToResponse(MealCheckIn checkIn) {
//        return MealCheckInResponse.builder()
//                .id(checkIn.getId())
//                .mealId(checkIn.getMeal().getId())
//                .userId(checkIn.getUser().getId())
//                .completionPercentage(checkIn.getCompletionPercentage())
//                .actualCalories(checkIn.getActualCalories())
//                .actualCarbs(checkIn.getActualCarbs())
//                .actualProtein(checkIn.getActualProtein())
//                .actualFat(checkIn.getActualFat())
//                .effectiveCalories(checkIn.getEffectiveCalories())
//                .effectiveCarbs(checkIn.getEffectiveCarbs())
//                .effectiveProtein(checkIn.getEffectiveProtein())
//                .effectiveFat(checkIn.getEffectiveFat())
//                .satisfactionRating(checkIn.getSatisfactionRating())
//                .satisfactionDescription(checkIn.getSatisfactionDescription())
//                .notes(checkIn.getNotes())
//                .checkedInAt(checkIn.getCheckedInAt())
//                .createdAt(checkIn.getCreatedAt())
//                .build();
//    }
//
//    private DailyNutritionSummary createNutritionSummary(MealPlan mealPlan) {
//        // Calculate planned nutrition from meals
//        BigDecimal plannedCalories = mealPlan.getMeals().stream()
//                .map(Meal::getConsumedCalories)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal plannedCarbs = mealPlan.getMeals().stream()
//                .map(Meal::getConsumedCarbs)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal plannedProtein = mealPlan.getMeals().stream()
//                .map(Meal::getConsumedProtein)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal plannedFat = mealPlan.getMeals().stream()
//                .map(Meal::getConsumedFat)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // Calculate extra nutrition
//        BigDecimal extraCalories = mealPlan.getExtraFoods().stream()
//                .map(ExtraFood::getCalculatedCalories)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal extraCarbs = mealPlan.getExtraFoods().stream()
//                .map(ExtraFood::getCalculatedCarbs)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal extraProtein = mealPlan.getExtraFoods().stream()
//                .map(ExtraFood::getCalculatedProtein)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal extraFat = mealPlan.getExtraFoods().stream()
//                .map(ExtraFood::getCalculatedFat)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // Total consumed
//        BigDecimal totalCalories = plannedCalories.add(extraCalories);
//        BigDecimal totalCarbs = plannedCarbs.add(extraCarbs);
//        BigDecimal totalProtein = plannedProtein.add(extraProtein);
//        BigDecimal totalFat = plannedFat.add(extraFat);
//
//        // Progress percentages
//        Double caloriesProgress = calculateProgress(totalCalories, mealPlan.getTargetCalories());
//        Double carbsProgress = calculateProgress(totalCarbs, mealPlan.getTargetCarbs());
//        Double proteinProgress = calculateProgress(totalProtein, mealPlan.getTargetProtein());
//        Double fatProgress = calculateProgress(totalFat, mealPlan.getTargetFat());
//
//        // Meal completion
//        int completedMeals = (int) mealPlan.getMeals().stream().filter(Meal::getIsCompleted).count();
//        int totalMeals = mealPlan.getMeals().size();
//        Double mealCompletionRate = totalMeals > 0 ? (double) completedMeals / totalMeals * 100 : 0.0;
//
//        return DailyNutritionSummary.builder()
//                .date(mealPlan.getDate())
//                .targetCalories(mealPlan.getTargetCalories())
//                .targetCarbs(mealPlan.getTargetCarbs())
//                .targetProtein(mealPlan.getTargetProtein())
//                .targetFat(mealPlan.getTargetFat())
//                .plannedCalories(plannedCalories)
//                .plannedCarbs(plannedCarbs)
//                .plannedProtein(plannedProtein)
//                .plannedFat(plannedFat)
//                .extraCalories(extraCalories)
//                .extraCarbs(extraCarbs)
//                .extraProtein(extraProtein)
//                .extraFat(extraFat)
//                .totalCalories(totalCalories)
//                .totalCarbs(totalCarbs)
//                .totalProtein(totalProtein)
//                .totalFat(totalFat)
//                .remainingCalories(mealPlan.getRemainingCalories())
//                .remainingCarbs(mealPlan.getRemainingCarbs())
//                .remainingProtein(mealPlan.getRemainingProtein())
//                .remainingFat(mealPlan.getRemainingFat())
//                .caloriesProgress(caloriesProgress)
//                .carbsProgress(carbsProgress)
//                .proteinProgress(proteinProgress)
//                .fatProgress(fatProgress)
//                .completedMeals(completedMeals)
//                .totalMeals(totalMeals)
//                .mealCompletionRate(mealCompletionRate)
//                .build();
//    }
//
//    private Double calculateProgress(BigDecimal consumed, BigDecimal target) {
//        if (target.compareTo(BigDecimal.ZERO) == 0) return 0.0;
//        return consumed.divide(target, 4, RoundingMode.HALF_UP)
//                .multiply(BigDecimal.valueOf(100))
//                .doubleValue();
//    }
//
//    private MealPlanStatsResponse generateMealPlanStats(User user, LocalDate startDate, LocalDate endDate) {
//        // Implementation would aggregate various statistics
//        // This is a simplified version
//        Long activePlans = mealPlanRepository.countByUserIdAndStatus(user.getId(), MealPlan.MealPlanStatus.ACTIVE);
//        Long completedPlans = mealPlanRepository.countByUserIdAndStatus(user.getId(), MealPlan.MealPlanStatus.COMPLETED);
//        Double averageCalories = mealPlanRepository.getAverageConsumedCalories(user.getId(), startDate, endDate);
//
//        return MealPlanStatsResponse.builder()
//                .userId(user.getId())
//                .startDate(startDate)
//                .endDate(endDate)
//                .totalDays((int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1)
//                .activePlans(activePlans)
//                .completedPlans(completedPlans)
//                .averageCompletionPercentage(75.0) // Would calculate from actual data
//                .averageCaloriesConsumed(averageCalories != null ? BigDecimal.valueOf(averageCalories) : BigDecimal.ZERO)
//                .currentStreak(0) // Would calculate streak
//                .longestStreak(0) // Would calculate from history
//                .build();
//    }
}