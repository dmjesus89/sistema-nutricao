package com.nutrition.application.service;

import com.nutrition.application.dto.meals.ExtraFoodRequest;
import com.nutrition.application.dto.meals.MealCheckInRequest;
import com.nutrition.application.dto.meals.MealFoodUpdateRequest;
import com.nutrition.domain.entity.ExtraFood;
import com.nutrition.domain.entity.food.Food;
import com.nutrition.domain.entity.Meal;
import com.nutrition.domain.entity.MealCheckIn;
import com.nutrition.domain.entity.MealFood;
import com.nutrition.domain.entity.MealPlan;
import com.nutrition.infrastructure.repository.ExtraFoodRepository;
import com.nutrition.infrastructure.repository.FoodRepository;
import com.nutrition.infrastructure.repository.MealCheckInRepository;
import com.nutrition.infrastructure.repository.MealFoodRepository;
import com.nutrition.infrastructure.repository.MealPlanRepository;
import com.nutrition.infrastructure.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealTrackingService {

    private final MealRepository mealRepository;
    private final MealFoodRepository mealFoodRepository;
    private final MealCheckInRepository mealCheckInRepository;
    private final ExtraFoodRepository extraFoodRepository;
    private final MealPlanRepository mealPlanRepository;
    private final FoodRepository foodRepository;

    @Transactional
    public MealCheckIn checkInMeal(Long userId, MealCheckInRequest request) {
        log.info("Processing meal check-in for user {} and meal {}", userId, request.getMealId());

        // Find the meal and validate ownership
        Meal meal = mealRepository.findById(request.getMealId())
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada"));

        if (!meal.getMealPlan().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Você não tem permissão para fazer check-in desta refeição");
        }

        // Check if already checked in
        Optional<MealCheckIn> existingCheckIn = mealCheckInRepository.findByMeal(meal);
        if (existingCheckIn.isPresent()) {
            throw new IllegalStateException("Esta refeição já foi registrada");
        }

        // Create check-in record
        MealCheckIn checkIn = MealCheckIn.builder()
                .meal(meal)
                .user(meal.getMealPlan().getUser())
                .completionPercentage(request.getCompletionPercentage())
                .actualCalories(request.getActualCalories())
                .actualCarbs(request.getActualCarbs())
                .actualProtein(request.getActualProtein())
                .actualFat(request.getActualFat())
                .satisfactionRating(request.getSatisfactionRating())
                .notes(request.getNotes())
                .build();

        checkIn = mealCheckInRepository.save(checkIn);

        // Update meal status
        meal.complete();
        meal.setConsumedCalories(checkIn.getEffectiveCalories());
        meal.setConsumedCarbs(checkIn.getEffectiveCarbs());
        meal.setConsumedProtein(checkIn.getEffectiveProtein());
        meal.setConsumedFat(checkIn.getEffectiveFat());
        mealRepository.save(meal);

        // Mark individual meal foods as consumed based on completion percentage
        if (request.getCompletionPercentage() >= 100) {
            markAllMealFoodsAsConsumed(meal, BigDecimal.valueOf(request.getCompletionPercentage() / 100.0));
        } else if (request.getCompletionPercentage() > 0) {
            markMealFoodsAsPartiallyConsumed(meal, BigDecimal.valueOf(request.getCompletionPercentage() / 100.0));
        }

        // Update meal plan totals
        updateMealPlanTotals(meal.getMealPlan());

        log.info("Successfully created check-in {} for meal {} with {}% completion",
                checkIn.getId(), meal.getId(), request.getCompletionPercentage());

        return checkIn;
    }

    @Transactional
    public MealCheckIn updateCheckIn(Long checkInId, Long userId, MealCheckInRequest request) {
        MealCheckIn checkIn = mealCheckInRepository.findById(checkInId)
                .orElseThrow(() -> new IllegalArgumentException("Check-in não encontrado"));

        if (!checkIn.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Você não tem permissão para atualizar este check-in");
        }

        // Update check-in data
        checkIn.setCompletionPercentage(request.getCompletionPercentage());
        checkIn.setActualCalories(request.getActualCalories());
        checkIn.setActualCarbs(request.getActualCarbs());
        checkIn.setActualProtein(request.getActualProtein());
        checkIn.setActualFat(request.getActualFat());
        checkIn.setSatisfactionRating(request.getSatisfactionRating());
        checkIn.setNotes(request.getNotes());

        checkIn = mealCheckInRepository.save(checkIn);

        // Update corresponding meal
        Meal meal = checkIn.getMeal();
        meal.setConsumedCalories(checkIn.getEffectiveCalories());
        meal.setConsumedCarbs(checkIn.getEffectiveCarbs());
        meal.setConsumedProtein(checkIn.getEffectiveProtein());
        meal.setConsumedFat(checkIn.getEffectiveFat());
        mealRepository.save(meal);

        // Update meal plan totals
        updateMealPlanTotals(meal.getMealPlan());

        log.info("Updated check-in {} for meal {}", checkInId, meal.getId());
        return checkIn;
    }

    @Transactional
    public void deleteCheckIn(Long checkInId, Long userId) {
        MealCheckIn checkIn = mealCheckInRepository.findById(checkInId)
                .orElseThrow(() -> new IllegalArgumentException("Check-in não encontrado"));

        if (!checkIn.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Você não tem permissão para deletar este check-in");
        }

        Meal meal = checkIn.getMeal();

        // Reset meal status
        meal.setIsCompleted(false);
        meal.setCompletedAt(null);
        meal.setConsumedCalories(BigDecimal.ZERO);
        meal.setConsumedCarbs(BigDecimal.ZERO);
        meal.setConsumedProtein(BigDecimal.ZERO);
        meal.setConsumedFat(BigDecimal.ZERO);
        mealRepository.save(meal);

        // Reset meal foods consumption status
        List<MealFood> mealFoods = mealFoodRepository.findByMeal(meal);
        for (MealFood mealFood : mealFoods) {
            mealFood.setIsConsumed(false);
            mealFood.setConsumedQuantity(null);
            mealFood.setConsumedAt(null);
        }
        mealFoodRepository.saveAll(mealFoods);

        // Delete the check-in
        mealCheckInRepository.delete(checkIn);

        // Update meal plan totals
        updateMealPlanTotals(meal.getMealPlan());

        log.info("Deleted check-in {} for meal {}", checkInId, meal.getId());
    }

    @Transactional
    public ExtraFood addExtraFood(Long userId, LocalDate date, ExtraFoodRequest request) {
        log.info("Adding extra food for user {} on date {}", userId, date);

        // Find or create meal plan for the date
        MealPlan mealPlan = mealPlanRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plano alimentar não encontrado para esta data. Gere um plano primeiro."));

        // Find the food
        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new IllegalArgumentException("Alimento não encontrado"));

        // Calculate nutrition for the specified quantity
        NutritionValues nutrition = calculateNutritionForQuantity(food, request.getQuantityGrams());

        // Create extra food entry
        ExtraFood extraFood = ExtraFood.builder()
                .mealPlan(mealPlan)
                .food(food)
                .quantityGrams(request.getQuantityGrams())
                .servingDescription(request.getServingDescription())
                .calculatedCalories(nutrition.calories)
                .calculatedCarbs(nutrition.carbs)
                .calculatedProtein(nutrition.protein)
                .calculatedFat(nutrition.fat)
                .consumedAt(request.getConsumedAt() != null ? request.getConsumedAt() : LocalDateTime.now())
                .mealTypeHint(request.getMealTypeHint())
                .notes(request.getNotes())
                .build();

        extraFood = extraFoodRepository.save(extraFood);

        // Update meal plan totals
        updateMealPlanTotals(mealPlan);

        log.info("Successfully added extra food {} ({}g) with {} calories",
                food.getName(), request.getQuantityGrams(), nutrition.calories);

        return extraFood;
    }

    @Transactional
    public void removeExtraFood(Long extraFoodId, Long userId) {
        ExtraFood extraFood = extraFoodRepository.findById(extraFoodId)
                .orElseThrow(() -> new IllegalArgumentException("Alimento extra não encontrado"));

        if (!extraFood.getMealPlan().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Você não tem permissão para remover este alimento");
        }

        MealPlan mealPlan = extraFood.getMealPlan();
        extraFoodRepository.delete(extraFood);

        // Update meal plan totals
        updateMealPlanTotals(mealPlan);

        log.info("Removed extra food {} from meal plan {}", extraFoodId, mealPlan.getId());
    }

    @Transactional
    public MealFood updateMealFoodConsumption(Long userId, MealFoodUpdateRequest request) {
        MealFood mealFood = mealFoodRepository.findById(request.getMealFoodId())
                .orElseThrow(() -> new IllegalArgumentException("Alimento da refeição não encontrado"));

        // Validate ownership
        if (!mealFood.getMeal().getMealPlan().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Você não tem permissão para atualizar este alimento");
        }

        // Update consumption data
        if (request.getIsConsumed()) {
            BigDecimal consumedQuantity = request.getConsumedQuantity() != null
                    ? request.getConsumedQuantity()
                    : mealFood.getQuantityGrams();
            mealFood.consume(consumedQuantity);
        } else {
            mealFood.setIsConsumed(false);
            mealFood.setConsumedQuantity(null);
            mealFood.setConsumedAt(null);
        }

        mealFood.setNotes(request.getNotes());
        mealFood = mealFoodRepository.save(mealFood);

        // Update meal totals
        updateMealTotals(mealFood.getMeal());

        // Update meal plan totals
        updateMealPlanTotals(mealFood.getMeal().getMealPlan());

        log.info("Updated consumption for meal food {}: consumed = {}",
                mealFood.getId(), mealFood.getIsConsumed());

        return mealFood;
    }

    public List<MealCheckIn> getUserCheckIns(Long userId, LocalDate startDate, LocalDate endDate) {
        return mealCheckInRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    public List<ExtraFood> getUserExtraFoods(Long userId, LocalDate startDate, LocalDate endDate) {
        return extraFoodRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    public List<MealCheckIn> getTodayCheckIns(Long userId) {
        return mealCheckInRepository.findByUserIdAndDate(userId, LocalDate.now());
    }

    public List<ExtraFood> getTodayExtraFoods(Long userId) {
        return extraFoodRepository.findByUserIdAndDate(userId, LocalDate.now());
    }

    private void markAllMealFoodsAsConsumed(Meal meal, BigDecimal completionRatio) {
        List<MealFood> mealFoods = mealFoodRepository.findByMeal(meal);

        for (MealFood mealFood : mealFoods) {
            BigDecimal consumedQuantity = mealFood.getQuantityGrams()
                    .multiply(completionRatio)
                    .setScale(2, RoundingMode.HALF_UP);
            mealFood.consume(consumedQuantity);
        }

        mealFoodRepository.saveAll(mealFoods);
    }

    private void markMealFoodsAsPartiallyConsumed(Meal meal, BigDecimal completionRatio) {
        List<MealFood> mealFoods = mealFoodRepository.findByMeal(meal);

        for (MealFood mealFood : mealFoods) {
            BigDecimal consumedQuantity = mealFood.getQuantityGrams()
                    .multiply(completionRatio)
                    .setScale(2, RoundingMode.HALF_UP);
            mealFood.consume(consumedQuantity);
        }

        mealFoodRepository.saveAll(mealFoods);
    }

    private void updateMealTotals(Meal meal) {
        List<MealFood> mealFoods = mealFoodRepository.findByMealAndIsConsumed(meal, true);

        BigDecimal totalCalories = mealFoods.stream()
                .map(MealFood::getActualCalories)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCarbs = mealFoods.stream()
                .map(MealFood::getActualCarbs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProtein = mealFoods.stream()
                .map(MealFood::getActualProtein)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFat = mealFoods.stream()
                .map(MealFood::getActualFat)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        meal.setConsumedCalories(totalCalories);
        meal.setConsumedCarbs(totalCarbs);
        meal.setConsumedProtein(totalProtein);
        meal.setConsumedFat(totalFat);

        mealRepository.save(meal);
    }

    @Transactional
    public void updateMealPlanTotals(MealPlan mealPlan) {
        // Calculate totals from completed meals
        List<Meal> meals = mealRepository.findByMealPlanAndIsCompletedOrderByOrderIndex(mealPlan, true);

        BigDecimal totalCalories = meals.stream()
                .map(Meal::getConsumedCalories)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCarbs = meals.stream()
                .map(Meal::getConsumedCarbs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProtein = meals.stream()
                .map(Meal::getConsumedProtein)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFat = meals.stream()
                .map(Meal::getConsumedFat)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add extra foods
        List<ExtraFood> extraFoods = extraFoodRepository.findByMealPlan(mealPlan);

        BigDecimal extraCalories = extraFoods.stream()
                .map(ExtraFood::getCalculatedCalories)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal extraCarbs = extraFoods.stream()
                .map(ExtraFood::getCalculatedCarbs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal extraProtein = extraFoods.stream()
                .map(ExtraFood::getCalculatedProtein)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal extraFat = extraFoods.stream()
                .map(ExtraFood::getCalculatedFat)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Update meal plan totals
        mealPlan.setConsumedCalories(totalCalories.add(extraCalories));
        mealPlan.setConsumedCarbs(totalCarbs.add(extraCarbs));
        mealPlan.setConsumedProtein(totalProtein.add(extraProtein));
        mealPlan.setConsumedFat(totalFat.add(extraFat));

        // Update status if all meals are completed
        List<Meal> allMeals = mealRepository.findByMealPlan(mealPlan);
        boolean allMealsCompleted = allMeals.stream().allMatch(Meal::getIsCompleted);

        if (allMealsCompleted && mealPlan.getStatus() == MealPlan.MealPlanStatus.ACTIVE) {
            mealPlan.setStatus(MealPlan.MealPlanStatus.COMPLETED);
        } else if (!allMealsCompleted && mealPlan.getStatus() == MealPlan.MealPlanStatus.COMPLETED) {
            mealPlan.setStatus(MealPlan.MealPlanStatus.ACTIVE);
        }

        mealPlanRepository.save(mealPlan);

        log.debug("Updated meal plan {} totals: {} calories, {} carbs, {} protein, {} fat",
                mealPlan.getId(), mealPlan.getConsumedCalories(), mealPlan.getConsumedCarbs(),
                mealPlan.getConsumedProtein(), mealPlan.getConsumedFat());
    }

    private NutritionValues calculateNutritionForQuantity(Food food, BigDecimal quantity) {
        BigDecimal multiplier = quantity.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        return new NutritionValues(
                food.getCaloriesPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP),
                food.getCarbsPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP),
                food.getProteinPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP),
                food.getFatPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP)
        );
    }

    // Quick check-in with default values
    @Transactional
    public MealCheckIn quickCheckIn(Long userId, Long mealId) {
        MealCheckInRequest request = MealCheckInRequest.builder()
                .mealId(mealId)
                .completionPercentage(100)
                .satisfactionRating(4) // Default to "satisfied"
                .notes("Check-in rápido")
                .build();

        return checkInMeal(userId, request);
    }

    // Partial check-in for when user didn't finish the meal
    @Transactional
    public MealCheckIn partialCheckIn(Long userId, Long mealId, Integer completionPercentage) {
        MealCheckInRequest request = MealCheckInRequest.builder()
                .mealId(mealId)
                .completionPercentage(completionPercentage)
                .satisfactionRating(3) // Neutral satisfaction for partial
                .notes(String.format("Consumido %d%% da refeição", completionPercentage))
                .build();

        return checkInMeal(userId, request);
    }

    // Get meal completion stats for user
    public MealCompletionStats getMealCompletionStats(Long userId, LocalDate startDate, LocalDate endDate) {
        Long totalMeals = mealRepository.countCompletedMeals(userId, startDate, endDate);
        List<Object[]> mealTypeStats = mealRepository.getMealTypeCompletionStats(userId, startDate, endDate);
        Double averageSatisfaction = mealCheckInRepository.getAverageSatisfactionRating(userId, startDate, endDate);
        Double averageCompletion = mealCheckInRepository.getAverageCompletionPercentage(userId, startDate, endDate);

        return MealCompletionStats.builder()
                .totalCompletedMeals(totalMeals)
                .mealTypeStats(mealTypeStats)
                .averageSatisfactionRating(averageSatisfaction)
                .averageCompletionPercentage(averageCompletion)
                .build();
    }

    // Get user's most consumed foods for recommendations
    public List<Object[]> getMostConsumedFoods(Long userId, LocalDate startDate, LocalDate endDate) {
        return mealFoodRepository.findMostConsumedFoods(userId, startDate, endDate);
    }

    // Get total nutrition consumed by user
    public List<Object[]> getTotalNutritionConsumed(Long userId, LocalDate startDate, LocalDate endDate) {
        return mealFoodRepository.getTotalNutritionConsumed(userId, startDate, endDate);
    }

    // Get recent extra foods for quick suggestions
    public List<Food> getRecentExtraFoods(Long userId) {
        return extraFoodRepository.findRecentExtraFoods(userId);
    }

    // Batch check-in all meals for a day
    @Transactional
    public List<MealCheckIn> batchCheckInDay(Long userId, LocalDate date, Integer defaultCompletionPercentage) {
        List<Meal> dayMeals = mealRepository.findByUserIdAndDate(userId, date);
        List<MealCheckIn> checkIns = new ArrayList<>();

        for (Meal meal : dayMeals) {
            // Skip already checked-in meals
            if (mealCheckInRepository.findByMeal(meal).isPresent()) {
                continue;
            }

            MealCheckInRequest request = MealCheckInRequest.builder()
                    .mealId(meal.getId())
                    .completionPercentage(defaultCompletionPercentage)
                    .satisfactionRating(4)
                    .notes("Check-in em lote")
                    .build();

            try {
                MealCheckIn checkIn = checkInMeal(userId, request);
                checkIns.add(checkIn);
            } catch (Exception e) {
                log.warn("Failed to check-in meal {} during batch operation: {}", meal.getId(), e.getMessage());
            }
        }

        log.info("Batch checked-in {} meals for user {} on date {}", checkIns.size(), userId, date);
        return checkIns;
    }

    // Helper classes
    private static class NutritionValues {
        final BigDecimal calories;
        final BigDecimal carbs;
        final BigDecimal protein;
        final BigDecimal fat;

        NutritionValues(BigDecimal calories, BigDecimal carbs, BigDecimal protein, BigDecimal fat) {
            this.calories = calories;
            this.carbs = carbs;
            this.protein = protein;
            this.fat = fat;
        }
    }

    public static class MealCompletionStats {
        private Long totalCompletedMeals;
        private List<Object[]> mealTypeStats;
        private Double averageSatisfactionRating;
        private Double averageCompletionPercentage;

        public static MealCompletionStatsBuilder builder() {
            return new MealCompletionStatsBuilder();
        }

        // Getters and builder class would be here
        public static class MealCompletionStatsBuilder {
            private Long totalCompletedMeals;
            private List<Object[]> mealTypeStats;
            private Double averageSatisfactionRating;
            private Double averageCompletionPercentage;

            public MealCompletionStatsBuilder totalCompletedMeals(Long totalCompletedMeals) {
                this.totalCompletedMeals = totalCompletedMeals;
                return this;
            }

            public MealCompletionStatsBuilder mealTypeStats(List<Object[]> mealTypeStats) {
                this.mealTypeStats = mealTypeStats;
                return this;
            }

            public MealCompletionStatsBuilder averageSatisfactionRating(Double averageSatisfactionRating) {
                this.averageSatisfactionRating = averageSatisfactionRating;
                return this;
            }

            public MealCompletionStatsBuilder averageCompletionPercentage(Double averageCompletionPercentage) {
                this.averageCompletionPercentage = averageCompletionPercentage;
                return this;
            }

            public MealCompletionStats build() {
                MealCompletionStats stats = new MealCompletionStats();
                stats.totalCompletedMeals = this.totalCompletedMeals;
                stats.mealTypeStats = this.mealTypeStats;
                stats.averageSatisfactionRating = this.averageSatisfactionRating;
                stats.averageCompletionPercentage = this.averageCompletionPercentage;
                return stats;
            }
        }

        // Getters
        public Long getTotalCompletedMeals() {
            return totalCompletedMeals;
        }

        public List<Object[]> getMealTypeStats() {
            return mealTypeStats;
        }

        public Double getAverageSatisfactionRating() {
            return averageSatisfactionRating;
        }

        public Double getAverageCompletionPercentage() {
            return averageCompletionPercentage;
        }
    }
}