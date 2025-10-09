package com.nutrition.application.service;

import com.nutrition.application.dto.food.FoodResponse;
import com.nutrition.application.dto.meals.*;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.Food;
import com.nutrition.domain.entity.meal.Meal;
import com.nutrition.domain.entity.meal.MealConsumption;
import com.nutrition.domain.entity.meal.MealFood;
import com.nutrition.domain.entity.tracking.CalorieEntry;
import com.nutrition.infrastructure.repository.CalorieEntryRepository;
import com.nutrition.infrastructure.repository.FoodRepository;
import com.nutrition.infrastructure.repository.MealConsumptionRepository;
import com.nutrition.infrastructure.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MealService {

    private final MealRepository mealRepository;
    private final FoodRepository foodRepository;
    private final CalorieEntryRepository calorieEntryRepository;
    private final MealConsumptionRepository mealConsumptionRepository;

    public MealTemplateResponseDTO createMeal(MealCreateDTO createDTO, User user) {
        log.info("Creating meal: {} for user: {}", createDTO.getName(), user.getId());

        // Validate all foods exist
        List<Long> foodIds = createDTO.getFoods().stream()
                .map(MealFoodDTO::getFoodId)
                .collect(Collectors.toList());

        List<Food> foods = foodRepository.findAllById(foodIds);
        if (foods.size() != foodIds.size()) {
            throw new IllegalArgumentException("Um ou mais alimentos não foram encontrados");
        }

        // Create the meal
        Meal meal = Meal.builder()
                .name(createDTO.getName())
                .mealTime(createDTO.getMealTime())
                .user(user)
                .isTemplate(!createDTO.getIsOneTime())
                .isOneTime(createDTO.getIsOneTime())
                .build();

        // Add foods to meal
        for (MealFoodDTO mealFoodDTO : createDTO.getFoods()) {
            Food food = foods.stream()
                    .filter(f -> f.getId().equals(mealFoodDTO.getFoodId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Alimento não encontrado: " + mealFoodDTO.getFoodId()));

            MealFood mealFood = MealFood.builder()
                    .food(food)
                    .quantity(mealFoodDTO.getQuantity())
                    .unit(mealFoodDTO.getUnit() != null ? mealFoodDTO.getUnit() : "g")
                    .build();

            meal.addFood(mealFood);
        }

        // Save the meal
        Meal savedMeal = mealRepository.save(meal);
        log.info("Meal created successfully with ID: {}", savedMeal.getId());

        return mapToTemplateResponseDTO(savedMeal, false);
    }

    public MealTemplateResponseDTO updateMeal(Long mealId, MealUpdateDTO updateDTO, User user) {
        log.info("Updating meal: {} for user: {}", mealId, user.getId());

        Meal meal = mealRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada"));

        // Validate all foods exist
        List<Long> foodIds = updateDTO.getFoods().stream()
                .map(MealFoodDTO::getFoodId)
                .collect(Collectors.toList());

        List<Food> foods = foodRepository.findAllById(foodIds);
        if (foods.size() != foodIds.size()) {
            throw new IllegalArgumentException("Um ou mais alimentos não foram encontrados");
        }

        // Update meal properties
        meal.setName(updateDTO.getName());
        meal.setMealTime(updateDTO.getMealTime());

        // Clear existing foods and add new ones
        meal.getFoods().clear();

        for (MealFoodDTO mealFoodDTO : updateDTO.getFoods()) {
            Food food = foods.stream()
                    .filter(f -> f.getId().equals(mealFoodDTO.getFoodId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Alimento não encontrado: " + mealFoodDTO.getFoodId()));

            MealFood mealFood = MealFood.builder()
                    .food(food)
                    .quantity(mealFoodDTO.getQuantity())
                    .unit(mealFoodDTO.getUnit() != null ? mealFoodDTO.getUnit() : "g")
                    .build();

            meal.addFood(mealFood);
        }

        Meal updatedMeal = mealRepository.save(meal);
        log.info("Meal updated successfully: {}", mealId);

        // Check if consumed today
        boolean isConsumedToday = mealConsumptionRepository.existsByMealIdAndUserIdAndConsumptionDate(
                mealId, user.getId(), LocalDate.now());

        return mapToTemplateResponseDTO(updatedMeal, isConsumedToday);
    }

    @Transactional(readOnly = true)
    public List<MealTemplateResponseDTO> getUserMealTemplates(User user) {
        log.info("Fetching meal templates for user: {}", user.getId());

        List<Meal> meals = mealRepository.findByUserOrderByMealTimeAsc(user);

        // Get today's consumed meal IDs
        LocalDate today = LocalDate.now();
        Set<Long> consumedMealIds = mealConsumptionRepository
                .findByUserIdAndConsumptionDate(user.getId(), today)
                .stream()
                .map(mc -> mc.getMeal().getId())
                .collect(Collectors.toSet());

        return meals.stream()
                .map(meal -> mapToTemplateResponseDTO(meal, consumedMealIds.contains(meal.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MealTemplateResponseDTO getMealById(Long mealId, User user) {
        log.info("Fetching meal: {} for user: {}", mealId, user.getId());

        Meal meal = mealRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada"));

        boolean isConsumedToday = mealConsumptionRepository.existsByMealIdAndUserIdAndConsumptionDate(
                mealId, user.getId(), LocalDate.now());

        return mapToTemplateResponseDTO(meal, isConsumedToday);
    }

    public void deleteMeal(Long mealId, User user) {
        log.info("Deleting meal: {} for user: {}", mealId, user.getId());

        Meal meal = mealRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada"));

        mealRepository.delete(meal);
        log.info("Meal deleted successfully: {}", mealId);
    }

    public MealConsumptionResponseDTO consumeMeal(Long mealId, User user, ConsumeMealDTO consumeDTO) {
        log.info("Marking meal {} as consumed for user: {}", mealId, user.getId());

        Meal meal = mealRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada"));

        LocalDate consumptionDate = consumeDTO.getConsumptionDate() != null
                ? consumeDTO.getConsumptionDate()
                : LocalDate.now();

        // Check if already consumed on this date
        if (mealConsumptionRepository.existsByMealIdAndUserIdAndConsumptionDate(
                mealId, user.getId(), consumptionDate)) {
            throw new IllegalArgumentException("Esta refeição já foi marcada como consumida nesta data");
        }

        // Create meal consumption
        MealConsumption consumption = MealConsumption.builder()
                .meal(meal)
                .user(user)
                .consumptionDate(consumptionDate)
                .notes(consumeDTO.getNotes())
                .build();

        mealConsumptionRepository.save(consumption);

        // Create calorie entry
        createCalorieEntryForConsumption(consumption, meal);

        return MealConsumptionResponseDTO.builder()
                .mealId(mealId)
                .isConsumed(true)
                .consumedAt(consumption.getConsumedAt())
                .message("Refeição marcada como consumida")
                .build();
    }

    public MealConsumptionResponseDTO unconsumeMeal(Long mealId, User user, LocalDate consumptionDate) {
        log.info("Unmarking meal {} as consumed for user: {} on date: {}",
                mealId, user.getId(), consumptionDate);

        // Validate meal exists and belongs to user
        mealRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada"));

        LocalDate dateToUnconsume = consumptionDate != null ? consumptionDate : LocalDate.now();

        // Find and delete the consumption
        MealConsumption consumption = mealConsumptionRepository
                .findByMealIdAndUserIdAndConsumptionDate(mealId, user.getId(), dateToUnconsume)
                .orElseThrow(() -> new IllegalArgumentException("Consumo não encontrado para esta data"));

        // Delete associated calorie entry
        removeCalorieEntryForConsumption(consumption);

        // Delete consumption
        mealConsumptionRepository.delete(consumption);

        return MealConsumptionResponseDTO.builder()
                .mealId(mealId)
                .isConsumed(false)
                .consumedAt(null)
                .message("Refeição desmarcada")
                .build();
    }

    @Transactional(readOnly = true)
    public DailyConsumedMealsDTO getConsumedMealsForDate(User user, LocalDate date) {
        log.info("Fetching consumed meals for user: {} on date: {}", user.getId(), date);

        List<MealConsumption> consumptions = mealConsumptionRepository
                .findByUserIdAndConsumptionDate(user.getId(), date);

        List<ConsumedMealDTO> mealDTOs = consumptions.stream()
                .map(this::mapToConsumedMealDTO)
                .collect(Collectors.toList());

        DailyConsumedMealsDTO.DailyNutritionalSummary summary = calculateDailySummary(mealDTOs);

        return DailyConsumedMealsDTO.builder()
                .date(date)
                .meals(mealDTOs)
                .nutritionalSummary(summary)
                .build();
    }

    @Transactional(readOnly = true)
    public DailyConsumedMealsDTO getTodayConsumedMeals(User user) {
        return getConsumedMealsForDate(user, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public MealHistoryDTO getMealHistory(User user, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching meal history for user: {} from {} to {}",
                user.getId(), startDate, endDate);

        List<MealConsumption> consumptions = mealConsumptionRepository
                .findByUserIdAndConsumptionDateBetween(user.getId(), startDate, endDate);

        // Group consumptions by date
        Map<LocalDate, List<MealConsumption>> consumptionsByDate = consumptions.stream()
                .collect(Collectors.groupingBy(MealConsumption::getConsumptionDate));

        List<DailyConsumedMealsDTO> dailyMeals = consumptionsByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<ConsumedMealDTO> dayMeals = entry.getValue().stream()
                            .map(this::mapToConsumedMealDTO)
                            .collect(Collectors.toList());

                    DailyConsumedMealsDTO.DailyNutritionalSummary summary = calculateDailySummary(dayMeals);

                    return DailyConsumedMealsDTO.builder()
                            .date(date)
                            .meals(dayMeals)
                            .nutritionalSummary(summary)
                            .build();
                })
                .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // Most recent first
                .collect(Collectors.toList());

        return MealHistoryDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .dailyMeals(dailyMeals)
                .totalDays(dailyMeals.size())
                .totalMealsConsumed(consumptions.size())
                .build();
    }

    @Transactional(readOnly = true)
    public MealHistoryDTO getRecentMealHistory(User user, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        return getMealHistory(user, startDate, endDate);
    }

    // Private helper methods

    private void createCalorieEntryForConsumption(MealConsumption consumption, Meal meal) {
        CalorieEntry calorieEntry = CalorieEntry.builder()
                .user(consumption.getUser())
                .date(consumption.getConsumptionDate())
                .entryType(CalorieEntry.EntryType.MEAL)
                .calories(meal.getTotalCalories())
                .carbs(meal.getTotalCarbs())
                .protein(meal.getTotalProtein())
                .fat(meal.getTotalFat())
                .meal(meal)
                .description("Refeição: " + meal.getName())
                .notes(consumption.getNotes())
                .consumedAt(consumption.getConsumedAt())
                .build();

        calorieEntryRepository.save(calorieEntry);
    }

    private void removeCalorieEntryForConsumption(MealConsumption consumption) {
        calorieEntryRepository.findByMealId(consumption.getMeal().getId())
                .ifPresent(calorieEntryRepository::delete);
    }

    private MealTemplateResponseDTO mapToTemplateResponseDTO(Meal meal, boolean isConsumedToday) {
        List<MealFoodResponseDTO> foodResponses = meal.getFoods().stream()
                .map(this::mapMealFoodToResponseDTO)
                .collect(Collectors.toList());

        return MealTemplateResponseDTO.builder()
                .id(meal.getId())
                .name(meal.getName())
                .mealTime(meal.getMealTime())
                .isTemplate(meal.getIsTemplate())
                .isOneTime(meal.getIsOneTime())
                .totalCalories(meal.getTotalCalories())
                .totalCarbs(meal.getTotalCarbs())
                .totalProtein(meal.getTotalProtein())
                .totalFat(meal.getTotalFat())
                .totalFiber(meal.getTotalFiber())
                .totalSodium(meal.getTotalSodium())
                .foods(foodResponses)
                .isConsumedToday(isConsumedToday)
                .createdAt(meal.getCreatedAt())
                .updatedAt(meal.getUpdatedAt())
                .build();
    }

    private MealFoodResponseDTO mapMealFoodToResponseDTO(MealFood mealFood) {
        FoodResponse foodResponse = mapFoodToResponseDTO(mealFood.getFood());

        return MealFoodResponseDTO.builder()
                .id(mealFood.getId())
                .food(foodResponse)
                .quantity(mealFood.getQuantityAsDouble())
                .unit(mealFood.getUnit())
                .totalCalories(mealFood.getTotalCalories())
                .totalCarbs(mealFood.getTotalCarbs())
                .totalProtein(mealFood.getTotalProtein())
                .totalFat(mealFood.getTotalFat())
                .totalFiber(mealFood.getTotalFiber())
                .totalSodium(mealFood.getTotalSodium())
                .build();
    }

    private FoodResponse mapFoodToResponseDTO(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .brand(food.getBrand())
                .category(food.getCategory().name())
                .categoryDisplay(food.getCategory().getDisplayName())
                .barcode(food.getBarcode())
                .caloriesPer100g(food.getCaloriesPer100g())
                .carbsPer100g(food.getCarbsPer100g())
                .proteinPer100g(food.getProteinPer100g())
                .fatPer100g(food.getFatPer100g())
                .fiberPer100g(food.getFiberPer100g())
                .sugarPer100g(food.getSugarPer100g())
                .sodiumPer100g(food.getSodiumPer100g())
                .saturatedFatPer100g(food.getSaturatedFatPer100g())
                .quantityEquivalence(food.getQuantityEquivalence())
                .servingSize(food.getServingSize())
                .servingDescription(food.getServingDescription())
                .caloriesPerServing(food.getCaloriesPerServing())
                .carbsPerServing(food.getCarbsPerServing())
                .proteinPerServing(food.getProteinPerServing())
                .fatPerServing(food.getFatPerServing())
                .source(food.getSource())
                .verified(food.getIsVerified())
                .displayName(food.getDisplayName())
                .isHighProtein(food.isHighProtein())
                .isHighFiber(food.isHighFiber())
                .isLowSodium(food.isLowSodium())
                .build();
    }

    private ConsumedMealDTO mapToConsumedMealDTO(MealConsumption consumption) {
        Meal meal = consumption.getMeal();

        List<MealFoodDTO> foodDTOs = meal.getFoods().stream()
                .map(mealFood -> MealFoodDTO.builder()
                        .foodId(mealFood.getFood().getId())
                        .foodName(mealFood.getFood().getName())
                        .quantity(mealFood.getQuantity())
                        .unit(mealFood.getUnit())
                        .calories(mealFood.getTotalCalories())
                        .carbs(mealFood.getTotalCarbs())
                        .protein(mealFood.getTotalProtein())
                        .fat(mealFood.getTotalFat())
                        .fiber(mealFood.getTotalFiber())
                        .sodium(mealFood.getTotalSodium())
                        .build())
                .collect(Collectors.toList());

        return ConsumedMealDTO.builder()
                .id(meal.getId())
                .name(meal.getName())
                .mealTime(meal.getMealTime())
                .consumedAt(consumption.getConsumedAt())
                .consumedDate(consumption.getConsumptionDate())
                .totalCalories(meal.getTotalCalories())
                .totalCarbs(meal.getTotalCarbs())
                .totalProtein(meal.getTotalProtein())
                .totalFat(meal.getTotalFat())
                .totalFiber(meal.getTotalFiber())
                .totalSodium(meal.getTotalSodium())
                .foods(foodDTOs)
                .notes(consumption.getNotes())
                .build();
    }

    private DailyConsumedMealsDTO.DailyNutritionalSummary calculateDailySummary(List<ConsumedMealDTO> meals) {
        return DailyConsumedMealsDTO.DailyNutritionalSummary.builder()
                .totalCalories(meals.stream()
                        .map(ConsumedMealDTO::getTotalCalories)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .totalCarbs(meals.stream()
                        .map(ConsumedMealDTO::getTotalCarbs)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .totalProtein(meals.stream()
                        .map(ConsumedMealDTO::getTotalProtein)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .totalFat(meals.stream()
                        .map(ConsumedMealDTO::getTotalFat)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .totalFiber(meals.stream()
                        .map(ConsumedMealDTO::getTotalFiber)
                        .filter(fiber -> fiber != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .totalSodium(meals.stream()
                        .map(ConsumedMealDTO::getTotalSodium)
                        .filter(sodium -> sodium != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .mealsCount(meals.size())
                .build();
    }
}
