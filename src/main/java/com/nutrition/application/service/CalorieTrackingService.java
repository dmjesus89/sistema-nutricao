package com.nutrition.application.service;

import static com.nutrition.application.service.NutritionConstants.*;

import com.nutrition.application.dto.profile.ProfileResponse;
import com.nutrition.application.dto.tracking.CalorieEntryResponse;
import com.nutrition.application.dto.tracking.CalorieSummaryResponse;
import com.nutrition.application.dto.tracking.DailyCaloriesSummary;
import com.nutrition.application.dto.tracking.FoodCalorieRequest;
import com.nutrition.application.dto.tracking.ManualCalorieRequest;
import com.nutrition.application.dto.tracking.MealCalorieRequest;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.Food;
import com.nutrition.domain.entity.meal.Meal;
import com.nutrition.domain.entity.profile.UserProfile;
import com.nutrition.domain.entity.tracking.CalorieEntry;
import com.nutrition.infrastructure.exception.NotFoundException;
import com.nutrition.infrastructure.exception.UnprocessableEntityException;
import com.nutrition.infrastructure.repository.CalorieEntryRepository;
import com.nutrition.infrastructure.repository.FoodRepository;
import com.nutrition.infrastructure.repository.MealRepository;
import com.nutrition.infrastructure.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalorieTrackingService {

    private final CalorieEntryRepository calorieEntryRepository;
    private final FoodRepository foodRepository;
    private final MealRepository mealRepository;
    private final UserProfileRepository userProfileRepository;
    private final ProfileService profileService;
    private final WaterIntakeService waterIntakeService;

    @Transactional
    public CalorieEntryResponse addManualCalories(User user, ManualCalorieRequest request) {
        validateManualRequest(request);

        LocalDate date = request.getDate() != null ? request.getDate() : LocalDate.now();
        LocalDateTime consumedAt = request.getConsumedAt() != null ?
                request.getConsumedAt() : LocalDateTime.now();

        CalorieEntry entry = CalorieEntry.builder()
                .user(user)
                .entryType(CalorieEntry.EntryType.MANUAL)
                .date(date)
                .calories(request.getCalories())
                .carbs(request.getCarbs())
                .protein(request.getProtein())
                .fat(request.getFat())
                .description(request.getDescription())
                .notes(request.getNotes())
                .consumedAt(consumedAt)
                .build();

        entry = calorieEntryRepository.save(entry);

        log.info("Manual calorie entry added for user {}: {} calories",
                user.getId(), request.getCalories());

        return convertToResponse(entry);
    }

    public CalorieSummaryResponse getDailyCalorieSummary(User user, LocalDate date) {
        // Buscar todas as entradas do dia
        List<CalorieEntryResponse> dailyEntries = getDailyEntries(user, date);

        // Buscar o perfil do usuário para obter o target
        ProfileResponse profile = profileService.getProfile(user);

        // Calcular totais
        BigDecimal totalCalories = dailyEntries.stream()
                .map(CalorieEntryResponse::getCalories)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCarbs = dailyEntries.stream()
                .map(CalorieEntryResponse::getCarbs)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProtein = dailyEntries.stream()
                .map(CalorieEntryResponse::getProtein)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFat = dailyEntries.stream()
                .map(CalorieEntryResponse::getFat)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal dailyTarget = profile.getDailyCalorieTarget();

        // Calcular diferença (target - consumed)
        BigDecimal calorieBalance = dailyTarget.subtract(totalCalories);

        // Calcular percentual consumido
        BigDecimal consumedPercentage = BigDecimal.ZERO;
        if (dailyTarget.compareTo(BigDecimal.ZERO) > 0) {
            consumedPercentage = totalCalories
                    .divide(dailyTarget, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        // Determinar status
        String status = determineCalorieStatus(totalCalories, dailyTarget);
        String statusDisplay = getStatusDisplay(status);

        // Calorias restantes (só positivo)
        BigDecimal remainingCalories = calorieBalance.max(BigDecimal.ZERO);

        return CalorieSummaryResponse.builder()
                .date(date)
                .dailyCalorieTarget(dailyTarget)
                .totalCaloriesConsumed(totalCalories)
                .calorieBalance(calorieBalance)
                .calorieBalancePercentage(consumedPercentage)
                .status(status)
                .statusDisplay(statusDisplay)
                .remainingCalories(remainingCalories)
                .totalEntries(dailyEntries.size())
                .totalCarbs(totalCarbs)
                .totalProtein(totalProtein)
                .totalFat(totalFat)
                .build();
    }

    @Transactional
    public CalorieEntryResponse addFoodCalories(User user, FoodCalorieRequest request) {
        validateFoodRequest(request);

        Food food = foodRepository.findByIdAndActiveTrue(request.getFoodId())
                .orElseThrow(() -> new NotFoundException("Alimento não encontrado"));

        LocalDate date = request.getDate() != null ? request.getDate() : LocalDate.now();
        LocalDateTime consumedAt = request.getConsumedAt() != null ?
                request.getConsumedAt() : LocalDateTime.now();

        // Calculate nutrition based on quantity
        BigDecimal multiplier = request.getQuantityGrams().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        BigDecimal calories = food.getCaloriesPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal carbs = food.getCarbsPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal protein = food.getProteinPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal fat = food.getFatPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);

        String description = String.format("%.0fg de %s",
                request.getQuantityGrams().doubleValue(), food.getName());

        CalorieEntry entry = CalorieEntry.builder()
                .user(user)
                .entryType(CalorieEntry.EntryType.FOOD)
                .date(date)
                .food(food)
                .quantityGrams(request.getQuantityGrams())
                .calories(calories)
                .carbs(carbs)
                .protein(protein)
                .fat(fat)
                .description(description)
                .notes(request.getNotes())
                .consumedAt(consumedAt)
                .build();

        entry = calorieEntryRepository.save(entry);

        log.info("Food calorie entry added for user {}: {} calories from {}",
                user.getId(), calories, food.getName());

        return convertToResponse(entry);
    }

    @Transactional
    public CalorieEntryResponse addMealCalories(User user, MealCalorieRequest request) {
        validateMealRequest(request);

        Meal meal = mealRepository.findById(request.getMealId())
                .orElseThrow(() -> new NotFoundException("Refeição não encontrada"));

        // Verify meal belongs to user
        if (!meal.getUser().getId().equals(user.getId())) {
            throw new UnprocessableEntityException("Refeição não pertence ao usuário");
        }

        LocalDate date = request.getDate() != null ? request.getDate() : LocalDate.now();
        LocalDateTime consumedAt = request.getConsumedAt() != null ?
                request.getConsumedAt() : LocalDateTime.now();

        BigDecimal percentage = request.getConsumptionPercentage() != null ?
                request.getConsumptionPercentage() : BigDecimal.valueOf(100);

        BigDecimal multiplier = percentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        // Calculate nutrition based on meal targets and consumption percentage
        BigDecimal calories = meal.getTotalCalories().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal carbs = meal.getTotalCarbs().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal protein = meal.getTotalProtein().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal fat = meal.getTotalFat().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);

        String description = String.format("%.0f%% de %s",
                percentage.doubleValue(), meal.getName());

        CalorieEntry entry = CalorieEntry.builder()
                .user(user)
                .entryType(CalorieEntry.EntryType.MEAL)
                .date(date)
                .meal(meal)
                .calories(calories)
                .carbs(carbs)
                .protein(protein)
                .fat(fat)
                .description(description)
                .notes(request.getNotes())
                .consumedAt(consumedAt)
                .build();

        entry = calorieEntryRepository.save(entry);

        log.info("Meal calorie entry added for user {}: {} calories from {}", user.getId(), calories, meal.getName());

        return convertToResponse(entry);
    }

    public List<CalorieEntryResponse> getDailyEntries(User user, LocalDate date) {
        List<CalorieEntry> entries = calorieEntryRepository.findByUserAndDateOrderByConsumedAtDesc(user, date);

        return entries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public DailyCaloriesSummary getDailySummary(User user, LocalDate date) {
        List<CalorieEntry> entries = calorieEntryRepository.findByUserAndDateOrderByConsumedAtDesc(user, date);

        if (entries.isEmpty()) {
            return createEmptyDailySummary(user, date);
        }

        // Calculate basic totals
        BigDecimal totalCalories = entries.stream()
                .map(CalorieEntry::getCalories)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCarbs = entries.stream()
                .map(entry -> entry.getCarbs() != null ? entry.getCarbs() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProtein = entries.stream()
                .map(entry -> entry.getProtein() != null ? entry.getProtein() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFat = entries.stream()
                .map(entry -> entry.getFat() != null ? entry.getFat() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate additional nutrients (would need to be stored in CalorieEntry)
        BigDecimal totalFiber = calculateTotalFiber(entries);
        BigDecimal totalSodium = calculateTotalSodium(entries);
        BigDecimal totalSugar = calculateTotalSugar(entries);

        // Count by type
        Map<CalorieEntry.EntryType, Long> typeCounts = entries.stream()
                .collect(Collectors.groupingBy(CalorieEntry::getEntryType, Collectors.counting()));

        // Get user targets
        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        BigDecimal targetCalories = profile != null ? profile.getDailyCalorieTarget() : BigDecimal.valueOf(2000);

        // Calculate macro targets (could come from profile or use defaults)
        BigDecimal targetCarbs = calculateMacroTarget(targetCalories, 0.45, 4); // 45% carbs
        BigDecimal targetProtein = calculateMacroTarget(targetCalories, 0.25, 4); // 25% protein
        BigDecimal targetFat = calculateMacroTarget(targetCalories, 0.30, 9); // 30% fat

        // Calculate remaining amounts
        BigDecimal remainingCalories = targetCalories.subtract(totalCalories);
        BigDecimal remainingCarbs = targetCarbs.subtract(totalCarbs);
        BigDecimal remainingProtein = targetProtein.subtract(totalProtein);
        BigDecimal remainingFat = targetFat.subtract(totalFat);

        // Calculate progress percentages
        Double caloriesProgress = calculateProgressPercentage(totalCalories, targetCalories);
        Double carbsProgress = calculateProgressPercentage(totalCarbs, targetCarbs);
        Double proteinProgress = calculateProgressPercentage(totalProtein, targetProtein);
        Double fatProgress = calculateProgressPercentage(totalFat, targetFat);

        // Calculate macro distribution
        Double carbsPercentage = calculateMacroPercentage(totalCarbs, 4, totalCalories);
        Double proteinPercentage = calculateMacroPercentage(totalProtein, 4, totalCalories);
        Double fatPercentage = calculateMacroPercentage(totalFat, 9, totalCalories);

        // Target macro percentages
        Double targetCarbsPercentage = 45.0;
        Double targetProteinPercentage = 25.0;
        Double targetFatPercentage = 30.0;

        // Analyze status and generate alerts
        String overallStatus = determineOverallStatus(caloriesProgress);
        List<String> nutritionAlerts = generateNutritionAlerts(totalCalories, totalCarbs, totalProtein, totalFat, targetCalories);
        List<String> healthWarnings = generateHealthWarnings(totalCalories, totalSodium, totalSugar, profile);

        // Time analysis
        LocalDateTime firstEntryTime = entries.stream()
                .map(CalorieEntry::getConsumedAt)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime lastEntryTime = entries.stream()
                .map(CalorieEntry::getConsumedAt)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Integer hoursTracked = calculateHoursTracked(firstEntryTime, lastEntryTime);

        // Quality metrics
        BigDecimal averageCaloriesPerEntry = entries.isEmpty() ? BigDecimal.ZERO :
                totalCalories.divide(BigDecimal.valueOf(entries.size()), 2, RoundingMode.HALF_UP);

        Double nutritionQualityScore = calculateNutritionQualityScore(entries, totalCalories, totalFiber, totalSodium);

        Integer processedFoodEntries = countProcessedFoods(entries);
        Integer wholeFoodEntries = entries.size() - processedFoodEntries;

        // Predictions and recommendations
        BigDecimal estimatedRemainingDayCalories = estimateRemainingDayCalories(
                totalCalories, firstEntryTime, lastEntryTime);
        String recommendedNextMeal = recommendNextMeal(remainingCalories, LocalDateTime.now());

        // Historical comparisons
        BigDecimal yesterdayCalories = getYesterdayCalories(user, date);
        BigDecimal weeklyAverageCalories = getWeeklyAverageCalories(user, date);
        String trendDirection = determineTrendDirection(totalCalories, yesterdayCalories, weeklyAverageCalories);

        // Hydration (if implemented)
        BigDecimal dailyWaterIntakeTarget = profile != null ? profile.getDailyWaterIntake() : BigDecimal.valueOf(2000);
        BigDecimal dailyWaterIntakeConsumed = waterIntakeService.getTodaySummary(user).getTotalAmount(); // Would come from water tracking
        Double hydrationProgress = calculateProgressPercentage(dailyWaterIntakeConsumed, dailyWaterIntakeTarget);

        return DailyCaloriesSummary.builder()
                .date(date)
                .totalCalories(totalCalories)
                .totalCarbs(totalCarbs)
                .totalProtein(totalProtein)
                .totalFat(totalFat)
                .targetCalories(targetCalories)
                .remainingCalories(remainingCalories)
                .totalEntries(entries.size())
                .manualEntries(typeCounts.getOrDefault(CalorieEntry.EntryType.MANUAL, 0L).intValue())
                .foodEntries(typeCounts.getOrDefault(CalorieEntry.EntryType.FOOD, 0L).intValue())
                .mealEntries(typeCounts.getOrDefault(CalorieEntry.EntryType.MEAL, 0L).intValue())
                .hydrationProgress(hydrationProgress)
                .build();
    }

    // Helper methods for enhanced summary

    private BigDecimal calculateTotalFiber(List<CalorieEntry> entries) {
        // Would need to be calculated from food entries or stored separately
        return entries.stream()
                .filter(entry -> entry.getFood() != null && entry.getFood().getFiberPer100g() != null)
                .map(entry -> {
                    BigDecimal multiplier = entry.getQuantityGrams().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                    return entry.getFood().getFiberPer100g().multiply(multiplier);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalSodium(List<CalorieEntry> entries) {
        return entries.stream()
                .filter(entry -> entry.getFood() != null && entry.getFood().getSodiumPer100g() != null)
                .map(entry -> {
                    BigDecimal multiplier = entry.getQuantityGrams().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                    return entry.getFood().getSodiumPer100g().multiply(multiplier);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalSugar(List<CalorieEntry> entries) {
        return entries.stream()
                .filter(entry -> entry.getFood() != null && entry.getFood().getSugarPer100g() != null)
                .map(entry -> {
                    BigDecimal multiplier = entry.getQuantityGrams().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                    return entry.getFood().getSugarPer100g().multiply(multiplier);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateMacroTarget(BigDecimal calories, double percentage, int caloriesPerGram) {
        return calories.multiply(BigDecimal.valueOf(percentage))
                .divide(BigDecimal.valueOf(caloriesPerGram), 2, RoundingMode.HALF_UP);
    }

    private Double calculateMacroPercentage(BigDecimal macroGrams, int caloriesPerGram, BigDecimal totalCalories) {
        if (totalCalories.compareTo(BigDecimal.ZERO) == 0) return 0.0;

        BigDecimal macroCalories = macroGrams.multiply(BigDecimal.valueOf(caloriesPerGram));
        return macroCalories.divide(totalCalories, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    private String determineOverallStatus(Double caloriesProgress) {
        if (caloriesProgress < 50) return "UNDER_TARGET";
        if (caloriesProgress <= 110) return "ON_TRACK";
        if (caloriesProgress <= 150) return "OVER_TARGET";
        return "SIGNIFICANTLY_OVER";
    }

    private List<String> generateNutritionAlerts(BigDecimal calories, BigDecimal carbs,
                                                 BigDecimal protein, BigDecimal fat, BigDecimal target) {
        List<String> alerts = new ArrayList<>();

        if (calories.compareTo(target.multiply(BigDecimal.valueOf(1.5))) > 0) {
            alerts.add("Calorias muito acima da meta diária");
        }

        if (protein.compareTo(BigDecimal.valueOf(20)) < 0) {
            alerts.add("Baixo consumo de proteína para o dia");
        }

        // Add more nutrition-specific alerts
        return alerts;
    }

    private List<String> generateHealthWarnings(BigDecimal calories, BigDecimal sodium,
                                                BigDecimal sugar, UserProfile profile) {
        List<String> warnings = new ArrayList<>();

        if (calories.compareTo(BigDecimal.valueOf(800)) < 0) {
            warnings.add("ATENÇÃO: Consumo calórico muito baixo pode ser prejudicial à saúde");
        }

        if (sodium.compareTo(BigDecimal.valueOf(2300)) > 0) {
            warnings.add("Alto consumo de sódio - considere reduzir alimentos processados");
        }

        return warnings;
    }

    private Integer calculateHoursTracked(LocalDateTime first, LocalDateTime last) {
        if (first == null || last == null) return 0;
        return (int) java.time.Duration.between(first, last).toHours();
    }

    private Double calculateNutritionQualityScore(List<CalorieEntry> entries, BigDecimal totalCalories,
                                                  BigDecimal totalFiber, BigDecimal totalSodium) {
        double score = 50.0; // Base score

        // Add points for fiber
        if (totalFiber.compareTo(BigDecimal.valueOf(25)) >= 0) score += 20;
        else if (totalFiber.compareTo(BigDecimal.valueOf(15)) >= 0) score += 10;

        // Subtract points for high sodium
        if (totalSodium.compareTo(BigDecimal.valueOf(2300)) > 0) score -= 15;

        // Add points for whole foods vs processed
        long wholeFoodCount = entries.stream()
                .filter(entry -> entry.getEntryType() == CalorieEntry.EntryType.FOOD)
                .count();

        if (wholeFoodCount > entries.size() * 0.7) score += 15;

        return Math.max(0, Math.min(100, score));
    }

    private Integer countProcessedFoods(List<CalorieEntry> entries) {
        return (int) entries.stream()
                .filter(entry -> entry.getFood() != null)
                .filter(entry -> isProcessedFood(entry.getFood()))
                .count();
    }

    private boolean isProcessedFood(Food food) {
        // Logic to determine if a food is processed
        // Could be based on category, sodium content, ingredient count, etc.
        return food.getCategory() == Food.FoodCategory.SNACKS ||
                food.getCategory() == Food.FoodCategory.SWEETS_DESSERTS ||
                (food.getSodiumPer100g() != null && food.getSodiumPer100g().compareTo(BigDecimal.valueOf(400)) > 0);
    }

    private BigDecimal estimateRemainingDayCalories(BigDecimal consumed, LocalDateTime first, LocalDateTime last) {
        if (first == null || last == null) return BigDecimal.ZERO;

        LocalTime now = LocalTime.now();
        LocalTime dayEnd = LocalTime.of(22, 0); // Assume day ends at 10 PM

        if (now.isAfter(dayEnd)) return BigDecimal.ZERO;

        // Simple estimation based on current consumption rate
        double hoursElapsed = java.time.Duration.between(first.toLocalTime(), now).toMinutes() / 60.0;
        double hoursRemaining = java.time.Duration.between(now, dayEnd).toMinutes() / 60.0;

        if (hoursElapsed <= 0) return BigDecimal.ZERO;

        double currentRate = consumed.doubleValue() / hoursElapsed;
        return BigDecimal.valueOf(currentRate * hoursRemaining).setScale(0, RoundingMode.HALF_UP);
    }

    private String recommendNextMeal(BigDecimal remainingCalories, LocalDateTime now) {
        LocalTime currentTime = now.toLocalTime();

        if (remainingCalories.compareTo(BigDecimal.valueOf(500)) > 0) {
            if (currentTime.isBefore(LocalTime.of(12, 0))) {
                return "Considere um almoço balanceado com proteína e vegetais";
            } else if (currentTime.isBefore(LocalTime.of(18, 0))) {
                return "Um lanche rico em proteína seria ideal";
            } else {
                return "Jantar leve com vegetais e proteína magra";
            }
        } else if (remainingCalories.compareTo(BigDecimal.valueOf(200)) > 0) {
            return "Lanche leve como frutas ou iogurte";
        } else {
            return "Meta calórica quase atingida - hidrate-se bem";
        }
    }

    private BigDecimal getYesterdayCalories(User user, LocalDate date) {
        LocalDate yesterday = date.minusDays(1);
        List<CalorieEntry> yesterdayEntries = calorieEntryRepository.findByUserAndDateOrderByConsumedAtDesc(user, yesterday);

        return yesterdayEntries.stream()
                .map(CalorieEntry::getCalories)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getWeeklyAverageCalories(User user, LocalDate date) {
        LocalDate weekStart = date.minusDays(6);
        List<CalorieEntry> weekEntries = calorieEntryRepository.findByUserAndDateBetweenOrderByDateDescConsumedAtDesc(
                user, weekStart, date);

        Map<LocalDate, BigDecimal> dailyTotals = weekEntries.stream()
                .collect(Collectors.groupingBy(
                        CalorieEntry::getDate,
                        Collectors.reducing(BigDecimal.ZERO, CalorieEntry::getCalories, BigDecimal::add)
                ));

        if (dailyTotals.isEmpty()) return BigDecimal.ZERO;

        BigDecimal totalCalories = dailyTotals.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalCalories.divide(BigDecimal.valueOf(dailyTotals.size()), 2, RoundingMode.HALF_UP);
    }

    private String determineTrendDirection(BigDecimal today, BigDecimal yesterday, BigDecimal weeklyAverage) {
        if (yesterday.compareTo(BigDecimal.ZERO) == 0 || weeklyAverage.compareTo(BigDecimal.ZERO) == 0) {
            return "STABLE";
        }

        BigDecimal changeFromYesterday = today.subtract(yesterday);
        BigDecimal changeFromAverage = today.subtract(weeklyAverage);

        // Consider both day-to-day and weekly trend
        if (changeFromYesterday.compareTo(BigDecimal.valueOf(100)) > 0 &&
                changeFromAverage.compareTo(BigDecimal.valueOf(50)) > 0) {
            return "INCREASING";
        } else if (changeFromYesterday.compareTo(BigDecimal.valueOf(-100)) < 0 &&
                changeFromAverage.compareTo(BigDecimal.valueOf(-50)) < 0) {
            return "DECREASING";
        } else {
            return "STABLE";
        }
    }

    public List<CalorieEntryResponse> getRecentEntries(User user, int days) {
        LocalDate fromDate = LocalDate.now().minusDays(days - 1);
        Pageable pageable = PageRequest.of(0, 50); // Limit to 50 recent entries

        List<CalorieEntry> entries = calorieEntryRepository.getRecentEntries(user, fromDate, pageable);

        return entries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEntry(User user, Long entryId) {
        CalorieEntry entry = calorieEntryRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("Entrada não encontrada"));

        if (!entry.getUser().getId().equals(user.getId())) {
            throw new UnprocessableEntityException("Entrada não pertence ao usuário");
        }

        calorieEntryRepository.delete(entry);
        log.info("Calorie entry {} deleted by user {}", entryId, user.getId());
    }

    private void validateManualRequest(ManualCalorieRequest request) {
        if (request.getCalories().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UnprocessableEntityException("Calorias devem ser positivas");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new UnprocessableEntityException("Descrição é obrigatória");
        }
    }

    private void validateFoodRequest(FoodCalorieRequest request) {
        if (request.getQuantityGrams().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UnprocessableEntityException("Quantidade deve ser positiva");
        }
    }

    private void validateMealRequest(MealCalorieRequest request) {
        if (request.getConsumptionPercentage() != null) {
            if (request.getConsumptionPercentage().compareTo(BigDecimal.ZERO) <= 0 ||
                    request.getConsumptionPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new UnprocessableEntityException("Percentual deve estar entre 0.1 e 100");
            }
        }
    }

    private BigDecimal getUserDailyCalorieTarget(User user) {
        return userProfileRepository.findByUser(user)
                .map(UserProfile::getDailyCalorieTarget)
                .orElse(BigDecimal.valueOf(2000)); // Default 2000 calories
    }

    private Double calculateProgressPercentage(BigDecimal consumed, BigDecimal target) {
        if (target.compareTo(BigDecimal.ZERO) == 0) return 0.0;

        return consumed.divide(target, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private DailyCaloriesSummary createEmptyDailySummary(User user, LocalDate date) {
        BigDecimal targetCalories = getUserDailyCalorieTarget(user);

        return DailyCaloriesSummary.builder()
                .date(date)
                .totalCalories(BigDecimal.ZERO)
                .totalCarbs(BigDecimal.ZERO)
                .totalProtein(BigDecimal.ZERO)
                .totalFat(BigDecimal.ZERO)
                .targetCalories(targetCalories)
                .remainingCalories(targetCalories)
                .progressPercentage(0.0)
                .totalEntries(0)
                .manualEntries(0)
                .foodEntries(0)
                .mealEntries(0)
                .build();
    }

    private CalorieEntryResponse convertToResponse(CalorieEntry entry) {
        CalorieEntryResponse.CalorieEntryResponseBuilder builder = CalorieEntryResponse.builder()
                .id(entry.getId())
                .entryType(entry.getEntryType().name())
                .entryTypeDisplay(entry.getEntryType().getDescription())
                .calories(entry.getCalories())
                .carbs(entry.getCarbs())
                .protein(entry.getProtein())
                .fat(entry.getFat())
                .description(entry.getDescription())
                .notes(entry.getNotes())
                .date(entry.getDate())
                .consumedAt(entry.getConsumedAt())
                .quantityGrams(entry.getQuantityGrams())
                .createdAt(entry.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .updatedAt(entry.getUpdatedAt() != null ?
                        entry.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);

        // Add food info if present
        if (entry.getFood() != null) {
            builder.foodId(entry.getFood().getId())
                    .foodName(entry.getFood().getName());
        }

        // Add meal info if present
        if (entry.getMeal() != null) {
            builder.mealId(entry.getMeal().getId())
                    .mealName(entry.getMeal().getName());
        }

        return builder.build();
    }


    public List<CalorieSummaryResponse> getWeeklyCalorieSummary(User user) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // últimos 7 dias

        List<CalorieSummaryResponse> weeklySummary = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            CalorieSummaryResponse dailySummary = getDailyCalorieSummary(user, date);
            weeklySummary.add(dailySummary);
        }

        return weeklySummary;
    }

    private String determineCalorieStatus(BigDecimal consumed, BigDecimal target) {
        if (target.compareTo(BigDecimal.ZERO) == 0) {
            return "UNDEFINED";
        }

        BigDecimal difference = consumed.subtract(target);
        BigDecimal tolerance = target.multiply(BigDecimal.valueOf(0.05)); // 5% de tolerância

        if (difference.abs().compareTo(tolerance) <= 0) {
            return "ON_TARGET";
        } else if (difference.compareTo(BigDecimal.ZERO) > 0) {
            return "SURPLUS";
        } else {
            return "DEFICIT";
        }
    }

    private String getStatusDisplay(String status) {
        switch (status) {
            case "DEFICIT":
                return "Abaixo da Meta";
            case "SURPLUS":
                return "Acima da Meta";
            case "ON_TARGET":
                return "Dentro da Meta";
            default:
                return "Indefinido";
        }
    }
}