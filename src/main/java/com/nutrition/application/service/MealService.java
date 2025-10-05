package com.nutrition.application.service;

import com.nutrition.application.dto.food.FoodResponse;
import com.nutrition.application.dto.meals.ConsumedMealDTO;
import com.nutrition.application.dto.meals.DailyConsumedMealsDTO;
import com.nutrition.application.dto.meals.MealConsumptionDTO;
import com.nutrition.application.dto.meals.MealConsumptionResponseDTO;
import com.nutrition.application.dto.meals.MealCreateDTO;
import com.nutrition.application.dto.meals.MealFoodDTO;
import com.nutrition.application.dto.meals.MealFoodResponseDTO;
import com.nutrition.application.dto.meals.MealHistoryDTO;
import com.nutrition.application.dto.meals.MealResponseDTO;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.Food;
import com.nutrition.domain.entity.meal.Meal;
import com.nutrition.domain.entity.meal.MealFood;
import com.nutrition.domain.entity.tracking.CalorieEntry;
import com.nutrition.infrastructure.repository.CalorieEntryRepository;
import com.nutrition.infrastructure.repository.FoodRepository;
import com.nutrition.infrastructure.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MealService {

    private final MealRepository mealRepository;
    private final FoodRepository foodRepository;
    private final CalorieEntryRepository calorieEntryRepository;

    public MealResponseDTO createMeal(MealCreateDTO createDTO, User user) {
        log.info("Creating meal: {} for user: {}", createDTO.getName(), user.getId());

        // Validar se todos os alimentos existem
        List<Long> foodIds = createDTO.getFoods().stream()
                .map(MealFoodDTO::getFoodId)
                .collect(Collectors.toList());

        List<Food> foods = foodRepository.findAllById(foodIds);
        if (foods.size() != foodIds.size()) {
            throw new IllegalArgumentException("Um ou mais alimentos não foram encontrados");
        }

        // Criar a refeição
        Meal meal = Meal.builder()
                .name(createDTO.getName())
                .mealTime(createDTO.getMealTime())
                .user(user)
                .build();

        // Adicionar os alimentos à refeição
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

        // Salvar a refeição
        Meal savedMeal = mealRepository.save(meal);
        log.info("Meal created successfully with ID: {}", savedMeal.getId());

        return mapToResponseDTO(savedMeal);
    }

    @Transactional(readOnly = true)
    public List<MealResponseDTO> getUserMeals(User user) {
        log.info("Fetching meals for user: {}", user.getId());

        List<Meal> meals = mealRepository.findByUserOrderByMealTimeAsc(user);
        return meals.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MealResponseDTO getMealById(Long mealId, User user) {
        log.info("Fetching meal: {} for user: {}", mealId, user.getId());

        Meal meal = mealRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada"));

        return mapToResponseDTO(meal);
    }

    public void deleteMeal(Long mealId, User user) {
        log.info("Deleting meal: {} for user: {}", mealId, user.getId());

        Meal meal = mealRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada"));

        mealRepository.delete(meal);
        log.info("Meal deleted successfully: {}", mealId);
    }

    private MealResponseDTO mapToResponseDTO(Meal meal) {
        List<MealFoodResponseDTO> foodResponses = meal.getFoods().stream()
                .map(this::mapMealFoodToResponseDTO)
                .collect(Collectors.toList());

        return MealResponseDTO.builder()
                .id(meal.getId())
                .name(meal.getName())
                .mealTime(meal.getMealTime())
                .totalCalories(meal.getTotalCalories())
                .totalCarbs(meal.getTotalCarbs())
                .totalProtein(meal.getTotalProtein())
                .totalFat(meal.getTotalFat())
                .totalFiber(meal.getTotalFiber())
                .totalSodium(meal.getTotalSodium())
                .foods(foodResponses)
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

    @Transactional(readOnly = true)
    public List<MealResponseDTO> searchMealsByName(String name, User user) {
        log.info("Searching meals by name: {} for user: {}", name, user.getId());

        List<Meal> meals = mealRepository.findByUserAndNameContainingIgnoreCase(user, name);
        return meals.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MealResponseDTO> getMealsByDate(java.time.LocalDate date, User user) {
        log.info("Fetching meals for date: {} and user: {}", date, user.getId());

        List<Meal> meals = mealRepository.findByUserAndDate(user, date);
        return meals.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MealResponseDTO> getMealsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate, User user) {
        log.info("Fetching meals for date range: {} to {} for user: {}", startDate, endDate, user.getId());

        List<Meal> meals = mealRepository.findByUserAndDateRange(user, startDate, endDate);
        return meals.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
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

    @Transactional
    public MealConsumptionResponseDTO toggleMealConsumption(Long mealId, User user, MealConsumptionDTO consumptionDTO) {
        Meal meal = mealRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new RuntimeException("Refeição não encontrada"));

        if (consumptionDTO.getConsumed()) {
            return markMealAsConsumed(meal, consumptionDTO.getNotes());
        } else {
            return markMealAsNotConsumed(meal);
        }
    }

    private MealConsumptionResponseDTO markMealAsConsumed(Meal meal, String notes) {
        if (meal.getConsumed()) {
            return MealConsumptionResponseDTO.builder()
                    .mealId(meal.getId())
                    .isConsumed(true)
                    .consumedAt(meal.getConsumedAt())
                    .message("Refeição já estava marcada como consumida")
                    .build();
        }

        // Mark meal as consumed
        meal.markAsConsumed();
        mealRepository.save(meal);

        // Create calorie entry
        createCalorieEntryForMeal(meal, notes);

        return MealConsumptionResponseDTO.builder()
                .mealId(meal.getId())
                .isConsumed(true)
                .consumedAt(meal.getConsumedAt())
                .message("Refeição marcada como consumida e registrada no diário")
                .build();
    }

    private MealConsumptionResponseDTO markMealAsNotConsumed(Meal meal) {
        if (!meal.getConsumed()) {
            return MealConsumptionResponseDTO.builder()
                    .mealId(meal.getId())
                    .isConsumed(false)
                    .consumedAt(null)
                    .message("Refeição já estava marcada como não consumida")
                    .build();
        }

        // Remove calorie entry if exists
        removeCalorieEntryForMeal(meal);

        // Mark meal as not consumed
        meal.markAsNotConsumed();
        mealRepository.save(meal);

        return MealConsumptionResponseDTO.builder()
                .mealId(meal.getId())
                .isConsumed(false)
                .consumedAt(null)
                .message("Refeição desmarcada e removida do diário")
                .build();
    }

    private void createCalorieEntryForMeal(Meal meal, String notes) {
        CalorieEntry calorieEntry = CalorieEntry.builder()
                .user(meal.getUser())
                .date(LocalDate.now())
                .entryType(CalorieEntry.EntryType.MEAL)
                .calories(meal.getTotalCalories())
                .carbs(meal.getTotalCarbs())
                .protein(meal.getTotalProtein())
                .fat(meal.getTotalFat())
                .meal(meal)
                .description("Refeição: " + meal.getName())
                .notes(notes)
                .consumedAt(meal.getConsumedAt())
                .build();

        calorieEntryRepository.save(calorieEntry);
    }

    private void removeCalorieEntryForMeal(Meal meal) {
        calorieEntryRepository.findByMealId(meal.getId())
                .ifPresent(calorieEntryRepository::delete);
    }

    public DailyConsumedMealsDTO getConsumedMealsForDate(User user, LocalDate date) {
        List<Meal> consumedMeals = mealRepository.findConsumedMealsByUserAndDate(user.getId(), date);

        List<ConsumedMealDTO> mealDTOs = consumedMeals.stream()
                .map(this::mapToConsumedMealDTO)
                .collect(Collectors.toList());

        DailyConsumedMealsDTO.DailyNutritionalSummary summary = calculateDailySummary(mealDTOs);

        return DailyConsumedMealsDTO.builder()
                .date(date)
                .meals(mealDTOs)
                .nutritionalSummary(summary)
                .build();
    }

    public DailyConsumedMealsDTO getTodayConsumedMeals(User user) {
        return getConsumedMealsForDate(user, LocalDate.now());
    }

    public MealHistoryDTO getMealHistory(User user, LocalDate startDate, LocalDate endDate) {
        List<Meal> consumedMeals = mealRepository.findConsumedMealsByUserAndDateRange(
                user.getId(), startDate, endDate);

        // Group meals by date
        Map<LocalDate, List<Meal>> mealsByDate = consumedMeals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getConsumedAt().toLocalDate()));

        List<DailyConsumedMealsDTO> dailyMeals = mealsByDate.entrySet().stream()
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
                .totalMealsConsumed(consumedMeals.size())
                .build();
    }

    public MealHistoryDTO getRecentMealHistory(User user, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        return getMealHistory(user, startDate, endDate);
    }

    private ConsumedMealDTO mapToConsumedMealDTO(Meal meal) {
        // Get notes from CalorieEntry if exists
        String notes = calorieEntryRepository.findMealCalorieEntry(meal.getId())
                .map(CalorieEntry::getNotes)
                .orElse(null);

        // Manually map MealFood to MealFoodDTO
        List<MealFoodDTO> foodDTOs = meal.getFoods().stream()
                .map(mealFood -> MealFoodDTO.builder()
                        .foodId(mealFood.getFood().getId())
                        .foodName(mealFood.getFood().getName()) // Assuming Food entity has getName()
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
                .consumedAt(meal.getConsumedAt())
                .consumedDate(meal.getConsumedAt().toLocalDate())
                .totalCalories(meal.getTotalCalories())
                .totalCarbs(meal.getTotalCarbs())
                .totalProtein(meal.getTotalProtein())
                .totalFat(meal.getTotalFat())
                .totalFiber(meal.getTotalFiber())
                .totalSodium(meal.getTotalSodium())
                .foods(foodDTOs)
                .notes(notes)
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