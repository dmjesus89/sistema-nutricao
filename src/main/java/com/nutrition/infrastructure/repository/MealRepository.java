package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.meal.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    @Query("SELECT DISTINCT m FROM Meal m LEFT JOIN FETCH m.foods mf LEFT JOIN FETCH mf.food WHERE m.user = :user ORDER BY m.mealTime ASC")
    List<Meal> findByUserOrderByMealTimeAsc(@Param("user") User user);

    @Query("SELECT DISTINCT m FROM Meal m LEFT JOIN FETCH m.foods mf LEFT JOIN FETCH mf.food WHERE m.id = :id AND m.user = :user")
    Optional<Meal> findByIdAndUser(@Param("id") Long id, @Param("user") User user);
}