package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.meal.MealConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealConsumptionRepository extends JpaRepository<MealConsumption, Long> {

    // Find consumption for a specific meal and date
    Optional<MealConsumption> findByMealIdAndUserIdAndConsumptionDate(Long mealId, Long userId, LocalDate consumptionDate);

    // Find all consumptions for a user on a specific date
    @Query("SELECT mc FROM MealConsumption mc " +
           "JOIN FETCH mc.meal m " +
           "JOIN FETCH m.foods mf " +
           "JOIN FETCH mf.food f " +
           "WHERE mc.user.id = :userId AND mc.consumptionDate = :date " +
           "ORDER BY m.mealTime ASC")
    List<MealConsumption> findByUserIdAndConsumptionDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // Find all consumptions for a user in a date range
    @Query("SELECT mc FROM MealConsumption mc " +
           "JOIN FETCH mc.meal m " +
           "JOIN FETCH m.foods mf " +
           "JOIN FETCH mf.food f " +
           "WHERE mc.user.id = :userId " +
           "AND mc.consumptionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY mc.consumptionDate DESC, m.mealTime ASC")
    List<MealConsumption> findByUserIdAndConsumptionDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Check if a meal was consumed on a specific date
    boolean existsByMealIdAndUserIdAndConsumptionDate(Long mealId, Long userId, LocalDate consumptionDate);

    // Delete consumption for a specific meal and date
    void deleteByMealIdAndUserIdAndConsumptionDate(Long mealId, Long userId, LocalDate consumptionDate);

    // Count consumptions for a meal
    long countByMealId(Long mealId);
}
