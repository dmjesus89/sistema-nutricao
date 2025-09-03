package com.nutrition.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealPlanningService {

//    private final MealPlanRepository mealPlanRepository;
//    private final MealRepository mealRepository;
//    private final MealFoodRepository mealFoodRepository;
//    private final FoodRepository foodRepository;
//    private final UserFoodPreferenceRepository userFoodPreferenceRepository;
//    private final UserDietaryRestrictionRepository userDietaryRestrictionRepository;
//    private final ProfileService profileService;
//
//    // Macro distribution percentages (default healthy ratios)
//    private static final Map<String, BigDecimal> DEFAULT_MACRO_RATIOS = Map.of(
//            "CARBS", new BigDecimal("0.45"),    // 45% carbs
//            "PROTEIN", new BigDecimal("0.25"),  // 25% protein
//            "FAT", new BigDecimal("0.30")       // 30% fat
//    );
//
//    // Meal calorie distribution (percentage of daily calories)
//    private static final Map<Meal.MealType, BigDecimal> MEAL_CALORIE_DISTRIBUTION = Map.of(
//            Meal.MealType.BREAKFAST, new BigDecimal("0.25"),        // 25%
//            Meal.MealType.MORNING_SNACK, new BigDecimal("0.10"),    // 10%
//            Meal.MealType.LUNCH, new BigDecimal("0.30"),            // 30%
//            Meal.MealType.AFTERNOON_SNACK, new BigDecimal("0.10"),  // 10%
//            Meal.MealType.DINNER, new BigDecimal("0.25")            // 25%
//    );
//
//    // Default meal times
//    private static final Map<Meal.MealType, LocalTime> DEFAULT_MEAL_TIMES = Map.of(
//            Meal.MealType.BREAKFAST, LocalTime.of(7, 30),
//            Meal.MealType.MORNING_SNACK, LocalTime.of(10, 0),
//            Meal.MealType.LUNCH, LocalTime.of(12, 30),
//            Meal.MealType.AFTERNOON_SNACK, LocalTime.of(15, 30),
//            Meal.MealType.DINNER, LocalTime.of(19, 0)
//    );
//
//    @Transactional
//    public MealPlan generateMealPlan(User user, MealPlanGenerationRequest request) {
//        log.info("Generating meal plan for user {} on date {}", user.getId(), request.getDate());
//
//        // Check if plan already exists for this date
//        if (mealPlanRepository.hasActivePlanForDate(user.getId(), request.getDate())) {
//            throw new IllegalStateException("Já existe um plano ativo para esta data");
//        }
//
//        // Get user profile for nutritional targets
//        ProfileResponse profile = profileService.getProfile().getData();
//        if (profile == null) {
//            throw new IllegalStateException("Perfil do usuário não encontrado. Complete seu perfil primeiro.");
//        }
//
//        // Calculate macro targets
//        MacroTargets macroTargets = calculateMacroTargets(profile, request);
//
//        // Create meal plan
//        MealPlan mealPlan = MealPlan.builder()
//                .user(user)
//                .date(request.getDate())
//                .status(MealPlan.MealPlanStatus.ACTIVE)
//                .targetCalories(macroTargets.calories)
//                .targetCarbs(macroTargets.carbs)
//                .targetProtein(macroTargets.protein)
//                .targetFat(macroTargets.fat)
//                .isGenerated(true)
//                .generatedAt(LocalDateTime.now())
//                .notes(request.getNotes())
//                .build();
//
//        mealPlan = mealPlanRepository.save(mealPlan);
//
//        // Generate meals for the plan
//        List<Meal> meals = generateMeals(mealPlan, macroTargets, request);
//        mealRepository.saveAll(meals);
//
//        // Generate food selections for each meal
//        for (Meal meal : meals) {
//            List<MealFood> mealFoods = generateMealFoods(meal, user, request);
//            mealFoodRepository.saveAll(mealFoods);
//        }
//
//        log.info("Successfully generated meal plan {} with {} meals", mealPlan.getId(), meals.size());
//        return mealPlan;
//    }
//
//    @Transactional
//    public MealPlan generateDailyMealPlan(User user, LocalDate date) {
//        // Get user's daily calorie target from profile
//        UserProfile profile = profileService.getUserProfile(user.getId());
//        if (profile == null) {
//            throw new IllegalStateException("Perfil do usuário não encontrado");
//        }
//
//        MealPlanGenerationRequest request = MealPlanGenerationRequest.builder()
//                .date(date)
//                .targetCalories(profile.getDailyCalories())
//                .useUserPreferences(true)
//                .respectDietaryRestrictions(true)
//                .build();
//
//        return generateMealPlan(user, request);
//    }
//
//    private MacroTargets calculateMacroTargets(ProfileResponse profile, MealPlanGenerationRequest request) {
//        BigDecimal targetCalories = request.getTargetCalories() != null
//                ? request.getTargetCalories()
//                : profile.getDailyCalorieTarget();
//
//        BigDecimal targetCarbs = request.getTargetCarbs() != null
//                ? request.getTargetCarbs()
//                : calculateMacroGrams(targetCalories, DEFAULT_MACRO_RATIOS.get("CARBS"), 4); // 4 cal/g
//
//        BigDecimal targetProtein = request.getTargetProtein() != null
//                ? request.getTargetProtein()
//                : calculateMacroGrams(targetCalories, DEFAULT_MACRO_RATIOS.get("PROTEIN"), 4); // 4 cal/g
//
//        BigDecimal targetFat = request.getTargetFat() != null
//                ? request.getTargetFat()
//                : calculateMacroGrams(targetCalories, DEFAULT_MACRO_RATIOS.get("FAT"), 9); // 9 cal/g
//
//        return new MacroTargets(targetCalories, targetCarbs, targetProtein, targetFat);
//    }
//
//    private BigDecimal calculateMacroGrams(BigDecimal calories, BigDecimal ratio, int caloriesPerGram) {
//        return calories.multiply(ratio)
//                .divide(BigDecimal.valueOf(caloriesPerGram), 2, RoundingMode.HALF_UP);
//    }
//
//    private List<Meal> generateMeals(MealPlan mealPlan, MacroTargets macroTargets, MealPlanGenerationRequest request) {
//        List<Meal.MealType> mealTypes = request.getPreferredMealTypes() != null && !request.getPreferredMealTypes().isEmpty()
//                ? request.getPreferredMealTypes()
//                : Arrays.asList(
//                Meal.MealType.BREAKFAST,
//                Meal.MealType.MORNING_SNACK,
//                Meal.MealType.LUNCH,
//                Meal.MealType.AFTERNOON_SNACK,
//                Meal.MealType.DINNER
//        );
//
//        List<Meal> meals = new ArrayList<>();
//        int orderIndex = 0;
//
//        for (Meal.MealType mealType : mealTypes) {
//            BigDecimal mealCalories = calculateMealCalories(macroTargets.calories, mealType);
//            MacroTargets mealMacros = distributeMacrosForMeal(macroTargets, mealType);
//
//            Meal meal = Meal.builder()
//                    .mealPlan(mealPlan)
//                    .mealType(mealType)
//                    .name(generateMealName(mealType))
//                    .description(generateMealDescription(mealType))
//                    .targetCalories(mealMacros.calories)
//                    .targetCarbs(mealMacros.carbs)
//                    .targetProtein(mealMacros.protein)
//                    .targetFat(mealMacros.fat)
//                    .scheduledTime(DEFAULT_MEAL_TIMES.get(mealType))
//                    .orderIndex(orderIndex++)
//                    .build();
//
//            meals.add(meal);
//        }
//
//        return meals;
//    }
//
//    private List<MealFood> generateMealFoods(Meal meal, User user, MealPlanGenerationRequest request) {
//        log.debug("Generating foods for meal: {} ({})", meal.getName(), meal.getMealType());
//
//        // Get user preferences and restrictions
//        Set<Long> excludedFoodIds = new HashSet<>();
//        if (request.getRespectDietaryRestrictions()) {
//            excludedFoodIds.addAll(getRestrictedFoodIds(user));
//        }
//        if (request.getExcludedFoodIds() != null) {
//            excludedFoodIds.addAll(request.getExcludedFoodIds());
//        }
//
//        Set<Long> preferredFoodIds = new HashSet<>();
//        if (request.getUseUserPreferences()) {
//            preferredFoodIds.addAll(getPreferredFoodIds(user));
//        }
//        if (request.getPriorityFoodIds() != null) {
//            preferredFoodIds.addAll(request.getPriorityFoodIds());
//        }
//
//        // Get suitable foods for this meal type
//        List<Food> candidateFoods = getSuitableFoods(meal.getMealType(), excludedFoodIds, preferredFoodIds);
//
//        if (candidateFoods.isEmpty()) {
//            log.warn("No suitable foods found for meal type: {}", meal.getMealType());
//            throw new IllegalStateException("Nenhum alimento adequado encontrado para " + meal.getMealType().getDisplayName());
//        }
//
//        // Select foods to meet macro targets
//        return selectOptimalFoods(meal, candidateFoods, preferredFoodIds);
//    }
//
//    private List<Food> getSuitableFoods(Meal.MealType mealType, Set<Long> excludedIds, Set<Long> preferredIds) {
//        // Get foods appropriate for meal type
//        List<Food.FoodCategory> suitableCategories = getSuitableCategoriesForMealType(mealType);
//
//        List<Food> foods = foodRepository.findByCategoryInAndIsActiveTrueAndIsVerifiedTrue(suitableCategories);
//
//        // Filter out excluded foods
//        foods = foods.stream()
//                .filter(food -> !excludedIds.contains(food.getId()))
//                .collect(Collectors.toList());
//
//        // Sort by preference (preferred foods first)
//        foods.sort((f1, f2) -> {
//            boolean f1Preferred = preferredIds.contains(f1.getId());
//            boolean f2Preferred = preferredIds.contains(f2.getId());
//            if (f1Preferred && !f2Preferred) return -1;
//            if (!f1Preferred && f2Preferred) return 1;
//            return 0;
//        });
//
//        return foods;
//    }
//
//    private List<Food.FoodCategory> getSuitableCategoriesForMealType(Meal.MealType mealType) {
//        return switch (mealType) {
//            case BREAKFAST -> Arrays.asList(
//                    Food.FoodCategory.CEREALS_GRAINS,
//                    Food.FoodCategory.DAIRY,
//                    Food.FoodCategory.FRUITS,
//                    Food.FoodCategory.PROTEINS
//            );
//            case LUNCH, DINNER -> Arrays.asList(
//                    Food.FoodCategory.PROTEINS,
//                    Food.FoodCategory.VEGETABLES,
//                    Food.FoodCategory.CEREALS_GRAINS,
//                    Food.FoodCategory.FATS_OILS,
//                    Food.FoodCategory.PREPARED_FOODS
//            );
//            case MORNING_SNACK, AFTERNOON_SNACK -> Arrays.asList(
//                    Food.FoodCategory.FRUITS,
//                    Food.FoodCategory.SNACKS,
//                    Food.FoodCategory.DAIRY,
//                    Food.FoodCategory.NUTS_SEEDS
//            );
//            case PRE_WORKOUT -> Arrays.asList(
//                    Food.FoodCategory.FRUITS,
//                    Food.FoodCategory.CEREALS_GRAINS,
//                    Food.FoodCategory.BEVERAGES
//            );
//            case POST_WORKOUT -> Arrays.asList(
//                    Food.FoodCategory.PROTEINS,
//                    Food.FoodCategory.DAIRY,
//                    Food.FoodCategory.FRUITS
//            );
//            case EVENING_SNACK -> Arrays.asList(
//                    Food.FoodCategory.FRUITS,
//                    Food.FoodCategory.DAIRY,
//                    Food.FoodCategory.SNACKS
//            );
//        };
//    }
//
//    private List<MealFood> selectOptimalFoods(Meal meal, List<Food> candidateFoods, Set<Long> preferredIds) {
//        List<MealFood> selectedFoods = new ArrayList<>();
//
//        // Target macros for this meal
//        MacroTargets remaining = new MacroTargets(
//                meal.getTargetCalories(),
//                meal.getTargetCarbs(),
//                meal.getTargetProtein(),
//                meal.getTargetFat()
//        );
//
//        // Greedy selection algorithm
//        int maxFoodsPerMeal = 4; // Limit complexity
//        int attempts = 0;
//
//        while (!isCloseEnough(remaining) && attempts < maxFoodsPerMeal && !candidateFoods.isEmpty()) {
//            Food bestFood = null;
//            BigDecimal bestQuantity = BigDecimal.ZERO;
//            double bestScore = Double.MAX_VALUE;
//
//            // Try each candidate food
//            for (Food food : candidateFoods.subList(0, Math.min(20, candidateFoods.size()))) {
//                // Calculate optimal quantity for this food
//                BigDecimal optimalQuantity = calculateOptimalQuantity(food, remaining);
//
//                if (optimalQuantity.compareTo(BigDecimal.ZERO) <= 0) continue;
//
//                // Calculate how well this food fits our needs
//                double score = calculateFoodScore(food, optimalQuantity, remaining, preferredIds.contains(food.getId()));
//
//                if (score < bestScore) {
//                    bestScore = score;
//                    bestFood = food;
//                    bestQuantity = optimalQuantity;
//                }
//            }
//
//            if (bestFood == null) break;
//
//            // Add the best food to the meal
//            NutritionValues nutrition = calculateNutritionForQuantity(bestFood, bestQuantity);
//
//            MealFood mealFood = MealFood.builder()
//                    .meal(meal)
//                    .food(bestFood)
//                    .quantityGrams(bestQuantity)
//                    .servingDescription(generateServingDescription(bestFood, bestQuantity))
//                    .calculatedCalories(nutrition.calories)
//                    .calculatedCarbs(nutrition.carbs)
//                    .calculatedProtein(nutrition.protein)
//                    .calculatedFat(nutrition.fat)
//                    .build();
//
//            selectedFoods.add(mealFood);
//
//            // Update remaining targets
//            remaining.calories = remaining.calories.subtract(nutrition.calories);
//            remaining.carbs = remaining.carbs.subtract(nutrition.carbs);
//            remaining.protein = remaining.protein.subtract(nutrition.protein);
//            remaining.fat = remaining.fat.subtract(nutrition.fat);
//
//            // Remove selected food from candidates to avoid duplicates
//            candidateFoods.remove(bestFood);
//            attempts++;
//        }
//
//        // Ensure we have at least one food
//        if (selectedFoods.isEmpty() && !candidateFoods.isEmpty()) {
//            Food fallbackFood = candidateFoods.get(0);
//            BigDecimal fallbackQuantity = BigDecimal.valueOf(100); // 100g default
//            NutritionValues nutrition = calculateNutritionForQuantity(fallbackFood, fallbackQuantity);
//
//            MealFood mealFood = MealFood.builder()
//                    .meal(meal)
//                    .food(fallbackFood)
//                    .quantityGrams(fallbackQuantity)
//                    .servingDescription(generateServingDescription(fallbackFood, fallbackQuantity))
//                    .calculatedCalories(nutrition.calories)
//                    .calculatedCarbs(nutrition.carbs)
//                    .calculatedProtein(nutrition.protein)
//                    .calculatedFat(nutrition.fat)
//                    .build();
//
//            selectedFoods.add(mealFood);
//        }
//
//        log.debug("Selected {} foods for meal {}: total calories = {}",
//                selectedFoods.size(), meal.getName(),
//                selectedFoods.stream()
//                        .map(MealFood::getCalculatedCalories)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add));
//
//        return selectedFoods;
//    }
//
//    private BigDecimal calculateOptimalQuantity(Food food, MacroTargets remaining) {
//        // Calculate how much of this food would satisfy our remaining calorie target
//        if (food.getCaloriesPer100g().compareTo(BigDecimal.ZERO) == 0) {
//            return BigDecimal.valueOf(50); // Default 50g if no calories
//        }
//
//        BigDecimal quantityForCalories = remaining.calories
//                .multiply(BigDecimal.valueOf(100))
//                .divide(food.getCaloriesPer100g(), 2, RoundingMode.HALF_UP);
//
//        // Limit quantity to reasonable ranges
//        BigDecimal minQuantity = BigDecimal.valueOf(10);   // 10g minimum
//        BigDecimal maxQuantity = BigDecimal.valueOf(500);  // 500g maximum
//
//        if (quantityForCalories.compareTo(minQuantity) < 0) {
//            return minQuantity;
//        } else if (quantityForCalories.compareTo(maxQuantity) > 0) {
//            return maxQuantity;
//        }
//
//        return quantityForCalories;
//    }
//
//    private double calculateFoodScore(Food food, BigDecimal quantity, MacroTargets remaining, boolean isPreferred) {
//        NutritionValues nutrition = calculateNutritionForQuantity(food, quantity);
//
//        // Calculate how far off we'd be from our targets
//        double calorieDiff = Math.abs(remaining.calories.subtract(nutrition.calories).doubleValue());
//        double carbsDiff = Math.abs(remaining.carbs.subtract(nutrition.carbs).doubleValue());
//        double proteinDiff = Math.abs(remaining.protein.subtract(nutrition.protein).doubleValue());
//        double fatDiff = Math.abs(remaining.fat.subtract(nutrition.fat).doubleValue());
//
//        // Weighted score (calories are most important)
//        double score = (calorieDiff * 1.0) + (carbsDiff * 0.3) + (proteinDiff * 0.4) + (fatDiff * 0.3);
//
//        // Bonus for preferred foods
//        if (isPreferred) {
//            score *= 0.8; // 20% bonus
//        }
//
//        // Penalty for going significantly over targets
//        if (nutrition.calories.compareTo(remaining.calories.multiply(BigDecimal.valueOf(1.2))) > 0) {
//            score *= 2.0; // Double penalty for exceeding by >20%
//        }
//
//        return score;
//    }
//
//    private boolean isCloseEnough(MacroTargets remaining) {
//        // Consider "close enough" if we're within 10% of calorie target
//        double threshold = 0.1;
//        return Math.abs(remaining.calories.doubleValue()) <= remaining.calories.abs().multiply(BigDecimal.valueOf(threshold)).doubleValue();
//    }
//
//    private NutritionValues calculateNutritionForQuantity(Food food, BigDecimal quantity) {
//        BigDecimal multiplier = quantity.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
//
//        return new NutritionValues(
//                food.getCaloriesPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP),
//                food.getCarbsPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP),
//                food.getProteinPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP),
//                food.getFatPer100g().multiply(multiplier).setScale(2, RoundingMode.HALF_UP)
//        );
//    }
//
//    private String generateServingDescription(Food food, BigDecimal quantity) {
//        if (food.getServingSize() != null && food.getServingUnit() != null) {
//            BigDecimal servings = quantity.divide(food.getServingSize(), 1, RoundingMode.HALF_UP);
//            return String.format("%.1f %s", servings.doubleValue(), food.getServingUnit());
//        }
//        return String.format("%.0fg", quantity.doubleValue());
//    }
//
//    private Set<Long> getRestrictedFoodIds(User user) {
//        return userDietaryRestrictionRepository.findByUser(user)
//                .stream()
//                .flatMap(restriction -> {
//                    // Get foods that match this restriction
//                    return foodRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
//                            restriction.getRestrictionType().name().toLowerCase(),
//                            restriction.getRestrictionType().name().toLowerCase()
//                    ).stream();
//                })
//                .map(Food::getId)
//                .collect(Collectors.toSet());
//    }
//
//    private Set<Long> getPreferredFoodIds(User user) {
//        return userFoodPreferenceRepository.findByUserAndIsFavoriteTrue(user)
//                .stream()
//                .map(pref -> pref.getFood().getId())
//                .collect(Collectors.toSet());
//    }
//
//    private BigDecimal calculateMealCalories(BigDecimal totalCalories, Meal.MealType mealType) {
//        BigDecimal percentage = MEAL_CALORIE_DISTRIBUTION.getOrDefault(mealType, new BigDecimal("0.20"));
//        return totalCalories.multiply(percentage).setScale(2, RoundingMode.HALF_UP);
//    }
//
//    private MacroTargets distributeMacrosForMeal(MacroTargets totalMacros, Meal.MealType mealType) {
//        BigDecimal percentage = MEAL_CALORIE_DISTRIBUTION.getOrDefault(mealType, new BigDecimal("0.20"));
//
//        return new MacroTargets(
//                totalMacros.calories.multiply(percentage).setScale(2, RoundingMode.HALF_UP),
//                totalMacros.carbs.multiply(percentage).setScale(2, RoundingMode.HALF_UP),
//                totalMacros.protein.multiply(percentage).setScale(2, RoundingMode.HALF_UP),
//                totalMacros.fat.multiply(percentage).setScale(2, RoundingMode.HALF_UP)
//        );
//    }
//
//    private String generateMealName(Meal.MealType mealType) {
//        return switch (mealType) {
//            case BREAKFAST -> "Café da Manhã Balanceado";
//            case MORNING_SNACK -> "Lanche da Manhã";
//            case LUNCH -> "Almoço Nutritivo";
//            case AFTERNOON_SNACK -> "Lanche da Tarde";
//            case DINNER -> "Jantar Saudável";
//            case EVENING_SNACK -> "Lanche da Noite";
//            case PRE_WORKOUT -> "Pré-Treino";
//            case POST_WORKOUT -> "Pós-Treino";
//        };
//    }
//
//    private String generateMealDescription(Meal.MealType mealType) {
//        return switch (mealType) {
//            case BREAKFAST -> "Refeição matinal rica em nutrientes para começar bem o dia";
//            case MORNING_SNACK -> "Lanche leve para manter a energia até o almoço";
//            case LUNCH -> "Refeição principal com equilíbrio de macronutrientes";
//            case AFTERNOON_SNACK -> "Lanche nutritivo para sustentar até o jantar";
//            case DINNER -> "Jantar balanceado para recuperação e saciedade";
//            case EVENING_SNACK -> "Lanche noturno leve e nutritivo";
//            case PRE_WORKOUT -> "Energia rápida para otimizar o treino";
//            case POST_WORKOUT -> "Recuperação muscular com proteínas e carboidratos";
//        };
//    }
//
//    @Transactional
//    public void deleteMealPlan(Long mealPlanId, Long userId) {
//        MealPlan mealPlan = mealPlanRepository.findById(mealPlanId)
//                .orElseThrow(() -> new IllegalArgumentException("Plano alimentar não encontrado"));
//
//        if (!mealPlan.getUser().getId().equals(userId)) {
//            throw new IllegalArgumentException("Você não tem permissão para deletar este plano");
//        }
//
//        mealPlanRepository.delete(mealPlan);
//        log.info("Deleted meal plan {} for user {}", mealPlanId, userId);
//    }
//
//    @Transactional
//    public MealPlan regenerateMealPlan(Long mealPlanId, Long userId) {
//        MealPlan existingPlan = mealPlanRepository.findById(mealPlanId)
//                .orElseThrow(() -> new IllegalArgumentException("Plano alimentar não encontrado"));
//
//        if (!existingPlan.getUser().getId().equals(userId)) {
//            throw new IllegalArgumentException("Você não tem permissão para regenerar este plano");
//        }
//
//        // Archive the old plan
//        existingPlan.setStatus(MealPlan.MealPlanStatus.ARCHIVED);
//        mealPlanRepository.save(existingPlan);
//
//        // Generate new plan for the same date
//        MealPlanGenerationRequest request = MealPlanGenerationRequest.builder()
//                .date(existingPlan.getDate())
//                .targetCalories(existingPlan.getTargetCalories())
//                .targetCarbs(existingPlan.getTargetCarbs())
//                .targetProtein(existingPlan.getTargetProtein())
//                .targetFat(existingPlan.getTargetFat())
//                .notes("Regenerado automaticamente")
//                .useUserPreferences(true)
//                .respectDietaryRestrictions(true)
//                .build();
//
//        return generateMealPlan(existingPlan.getUser(), request);
//    }
//
//    // Helper classes
//    private static class MacroTargets {
//        BigDecimal calories;
//        BigDecimal carbs;
//        BigDecimal protein;
//        BigDecimal fat;
//
//        MacroTargets(BigDecimal calories, BigDecimal carbs, BigDecimal protein, BigDecimal fat) {
//            this.calories = calories;
//            this.carbs = carbs;
//            this.protein = protein;
//            this.fat = fat;
//        }
//    }
//
//    private static class NutritionValues {
//        BigDecimal calories;
//        BigDecimal carbs;
//        BigDecimal protein;
//        BigDecimal fat;
//
//        NutritionValues(BigDecimal calories, BigDecimal carbs, BigDecimal protein, BigDecimal fat) {
//            this.calories = calories;
//            this.carbs = carbs;
//            this.protein = protein;
//            this.fat = fat;
//        }
//    }
}