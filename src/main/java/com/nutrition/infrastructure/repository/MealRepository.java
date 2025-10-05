package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.meal.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    @Query("SELECT DISTINCT m FROM Meal m LEFT JOIN FETCH m.foods mf LEFT JOIN FETCH mf.food WHERE m.user = :user ORDER BY m.mealTime ASC")
    List<Meal> findByUserOrderByMealTimeAsc(@Param("user") User user);

    @Query("SELECT DISTINCT m FROM Meal m LEFT JOIN FETCH m.foods mf LEFT JOIN FETCH mf.food WHERE m.id = :id AND m.user = :user")
    Optional<Meal> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

    @Query("SELECT DISTINCT m FROM Meal m LEFT JOIN FETCH m.foods mf LEFT JOIN FETCH mf.food WHERE m.user = :user AND DATE(m.createdAt) = :date ORDER BY m.mealTime ASC")
    List<Meal> findByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT DISTINCT m FROM Meal m LEFT JOIN FETCH m.foods mf LEFT JOIN FETCH mf.food WHERE m.user = :user AND DATE(m.createdAt) BETWEEN :startDate AND :endDate ORDER BY m.createdAt ASC")
    List<Meal> findByUserAndDateRange(@Param("user") User user,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(m) FROM Meal m WHERE m.user = :user")
    Long countByUser(@Param("user") User user);

    @Query("SELECT DISTINCT m FROM Meal m LEFT JOIN FETCH m.foods mf LEFT JOIN FETCH mf.food WHERE m.user = :user AND m.name LIKE %:name% ORDER BY m.mealTime ASC")
    List<Meal> findByUserAndNameContainingIgnoreCase(@Param("user") User user, @Param("name") String name);

      // Find consumed meals for a specific date
    @Query("SELECT DISTINCT m FROM Meal m LEFT JOIN FETCH m.foods mf LEFT JOIN FETCH mf.food " +
            "WHERE m.user.id = :userId " +
            "AND m.consumed = true " +
            "AND DATE(m.consumedAt) = :date " +
            "ORDER BY m.consumedAt ASC")
    List<Meal> findConsumedMealsByUserAndDate(@Param("userId") Long userId,
                                              @Param("date") LocalDate date);

    // Find consumed meals between dates
    @Query("SELECT DISTINCT m FROM Meal m LEFT JOIN FETCH m.foods mf LEFT JOIN FETCH mf.food " +
            "WHERE m.user.id = :userId " +
            "AND m.consumed = true " +
            "AND DATE(m.consumedAt) BETWEEN :startDate AND :endDate " +
            "ORDER BY m.consumedAt DESC")
    List<Meal> findConsumedMealsByUserAndDateRange(@Param("userId") Long userId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);



}