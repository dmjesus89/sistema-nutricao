
package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.ExtraFood;
import com.nutrition.domain.entity.food.Food;
import com.nutrition.domain.entity.Meal.MealType;
import com.nutrition.domain.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExtraFoodRepository extends JpaRepository<ExtraFood, Long> {

    // Find extra foods by meal plan
    List<ExtraFood> findByMealPlanOrderByConsumedAtDesc(MealPlan mealPlan);

    List<ExtraFood> findByMealPlanIdOrderByConsumedAtDesc(Long mealPlanId);

    // MISSING METHOD - Find all extra foods by meal plan
    List<ExtraFood> findByMealPlan(MealPlan mealPlan);

    // Find extra foods by food
    List<ExtraFood> findByFood(Food food);

    List<ExtraFood> findByFoodId(Long foodId);

    // Find extra foods by meal type hint
    List<ExtraFood> findByMealPlanAndMealTypeHint(MealPlan mealPlan, MealType mealTypeHint);

    List<ExtraFood> findByMealPlanIdAndMealTypeHint(Long mealPlanId, MealType mealTypeHint);

    // Find user's extra foods within date range
    @Query("SELECT ef FROM ExtraFood ef JOIN ef.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mp.date BETWEEN :startDate AND :endDate " +
            "ORDER BY ef.consumedAt DESC")
    List<ExtraFood> findByUserIdAndDateRange(@Param("userId") Long userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    // Find extra foods consumed today
    @Query("SELECT ef FROM ExtraFood ef JOIN ef.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mp.date = :date " +
            "ORDER BY ef.consumedAt DESC")
    List<ExtraFood> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // Statistics - most added extra foods
    @Query("SELECT ef.food, COUNT(ef), SUM(ef.calculatedCalories) " +
            "FROM ExtraFood ef JOIN ef.mealPlan mp " +
            "WHERE mp.user.id = :userId " +
            "AND mp.date BETWEEN :startDate AND :endDate " +
            "GROUP BY ef.food " +
            "ORDER BY COUNT(ef) DESC")
    List<Object[]> getMostAddedExtraFoods(@Param("userId") Long userId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // Total nutrition from extra foods
    @Query("SELECT SUM(ef.calculatedCalories), SUM(ef.calculatedCarbs), " +
            "SUM(ef.calculatedProtein), SUM(ef.calculatedFat) " +
            "FROM ExtraFood ef JOIN ef.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mp.date BETWEEN :startDate AND :endDate")
    List<Object[]> getTotalExtraNutrition(@Param("userId") Long userId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // Recent extra foods (for suggestions)
    @Query("SELECT DISTINCT ef.food FROM ExtraFood ef JOIN ef.mealPlan mp " +
            "WHERE mp.user.id = :userId " +
            "ORDER BY ef.consumedAt DESC")
    List<Food> findRecentExtraFoods(@Param("userId") Long userId);
}
