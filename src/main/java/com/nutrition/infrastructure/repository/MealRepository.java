package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.Meal;
import com.nutrition.domain.entity.Meal.MealType;
import com.nutrition.domain.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    // Find meals by meal plan
    List<Meal> findByMealPlanOrderByOrderIndex(MealPlan mealPlan);

    List<Meal> findByMealPlanIdOrderByOrderIndex(Long mealPlanId);

    // MISSING METHOD - Find all meals by meal plan
    List<Meal> findByMealPlan(MealPlan mealPlan);

    // Find specific meal by plan and type
    Optional<Meal> findByMealPlanAndMealType(MealPlan mealPlan, MealType mealType);

    Optional<Meal> findByMealPlanIdAndMealType(Long mealPlanId, MealType mealType);

    // Find completed/incomplete meals
    List<Meal> findByMealPlanAndIsCompletedOrderByOrderIndex(MealPlan mealPlan, Boolean isCompleted);

    List<Meal> findByMealPlanIdAndIsCompletedOrderByOrderIndex(Long mealPlanId, Boolean isCompleted);

    // Find meals for user on specific date
    @Query("SELECT m FROM Meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mp.date = :date " +
            "ORDER BY m.orderIndex")
    List<Meal> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // Find upcoming meals for user
    @Query("SELECT m FROM Meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mp.date = :date " +
            "AND m.isCompleted = false AND m.scheduledTime IS NOT NULL " +
            "ORDER BY m.scheduledTime")
    List<Meal> findUpcomingMealsForDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // Statistics
    @Query("SELECT COUNT(m) FROM Meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND m.isCompleted = true " +
            "AND mp.date BETWEEN :startDate AND :endDate")
    Long countCompletedMeals(@Param("userId") Long userId,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);

    @Query("SELECT m.mealType, COUNT(m) FROM Meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND m.isCompleted = true " +
            "AND mp.date BETWEEN :startDate AND :endDate " +
            "GROUP BY m.mealType")
    List<Object[]> getMealTypeCompletionStats(@Param("userId") Long userId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}