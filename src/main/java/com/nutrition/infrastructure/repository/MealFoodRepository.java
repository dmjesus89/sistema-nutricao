// ===== Updated MealFoodRepository.java =====
package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.food.Food;
import com.nutrition.domain.entity.Meal;
import com.nutrition.domain.entity.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealFoodRepository extends JpaRepository<MealFood, Long> {

    // Find foods in a meal
    List<MealFood> findByMealOrderByCreatedAt(Meal meal);

    List<MealFood> findByMealIdOrderByCreatedAt(Long mealId);

    // Find consumed/unconsumed foods
    List<MealFood> findByMealAndIsConsumed(Meal meal, Boolean isConsumed);

    List<MealFood> findByMealIdAndIsConsumed(Long mealId, Boolean isConsumed);

    // MISSING METHOD - Find all foods in a meal
    List<MealFood> findByMeal(Meal meal);

    // Find foods by food item
    List<MealFood> findByFood(Food food);

    List<MealFood> findByFoodId(Long foodId);

    // Find user's consumption of specific food within date range
    @Query("SELECT mf FROM MealFood mf JOIN mf.meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mf.food.id = :foodId " +
            "AND mp.date BETWEEN :startDate AND :endDate " +
            "AND mf.isConsumed = true " +
            "ORDER BY mp.date DESC, m.createdAt DESC")
    List<MealFood> findUserFoodConsumption(@Param("userId") Long userId,
                                           @Param("foodId") Long foodId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    // Most consumed foods by user
    @Query("SELECT mf.food, COUNT(mf), SUM(mf.consumedQuantity) " +
            "FROM MealFood mf JOIN mf.meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mf.isConsumed = true " +
            "AND mp.date BETWEEN :startDate AND :endDate " +
            "GROUP BY mf.food " +
            "ORDER BY COUNT(mf) DESC")
    List<Object[]> findMostConsumedFoods(@Param("userId") Long userId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    // Total nutrition consumed by user in date range
    @Query("SELECT " +
            "SUM(CASE WHEN mf.consumedQuantity IS NOT NULL " +
            "     THEN mf.calculatedCalories * mf.consumedQuantity / mf.quantityGrams " +
            "     ELSE mf.calculatedCalories END), " +
            "SUM(CASE WHEN mf.consumedQuantity IS NOT NULL " +
            "     THEN mf.calculatedCarbs * mf.consumedQuantity / mf.quantityGrams " +
            "     ELSE mf.calculatedCarbs END), " +
            "SUM(CASE WHEN mf.consumedQuantity IS NOT NULL " +
            "     THEN mf.calculatedProtein * mf.consumedQuantity / mf.quantityGrams " +
            "     ELSE mf.calculatedProtein END), " +
            "SUM(CASE WHEN mf.consumedQuantity IS NOT NULL " +
            "     THEN mf.calculatedFat * mf.consumedQuantity / mf.quantityGrams " +
            "     ELSE mf.calculatedFat END) " +
            "FROM MealFood mf JOIN mf.meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mf.isConsumed = true " +
            "AND mp.date BETWEEN :startDate AND :endDate")
    List<Object[]> getTotalNutritionConsumed(@Param("userId") Long userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}