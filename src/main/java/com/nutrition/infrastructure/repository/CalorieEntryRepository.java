package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.tracking.CalorieEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalorieEntryRepository extends JpaRepository<CalorieEntry, Long> {

    List<com.nutrition.domain.entity.tracking.CalorieEntry> findByUserAndDateOrderByConsumedAtDesc(User user, LocalDate date);

    List<CalorieEntry> findByUserAndDateBetweenOrderByDateDescConsumedAtDesc(
            User user, LocalDate startDate, LocalDate endDate);


    @Query("SELECT ce FROM CalorieEntry ce " +
            "WHERE ce.user = :user AND ce.date >= :fromDate " +
            "ORDER BY ce.date DESC, ce.consumedAt DESC")
    List<com.nutrition.domain.entity.tracking.CalorieEntry> getRecentEntries(@Param("user") User user,
                                                                             @Param("fromDate") LocalDate fromDate,
                                                                             Pageable pageable);

    Optional<CalorieEntry> findByMealId(Long mealId);

    @Query("SELECT ce FROM CalorieEntry ce WHERE ce.meal.id = :mealId AND ce.entryType = 'MEAL'")
    Optional<CalorieEntry> findMealCalorieEntry(@Param("mealId") Long mealId);

}