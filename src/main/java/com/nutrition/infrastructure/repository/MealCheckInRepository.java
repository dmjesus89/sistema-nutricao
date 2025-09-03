
// ===== MealCheckInRepository.java =====
package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.Meal;
import com.nutrition.domain.entity.MealCheckIn;
import com.nutrition.domain.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealCheckInRepository extends JpaRepository<MealCheckIn, Long> {

    // Find check-in by meal (should be unique)
    Optional<MealCheckIn> findByMeal(Meal meal);

    Optional<MealCheckIn> findByMealId(Long mealId);

    // Find user's check-ins
    List<MealCheckIn> findByUserOrderByCheckedInAtDesc(User user);

    List<MealCheckIn> findByUserIdOrderByCheckedInAtDesc(Long userId);

    // Find check-ins within date range
    @Query("SELECT mci FROM MealCheckIn mci JOIN mci.meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mp.date BETWEEN :startDate AND :endDate " +
            "ORDER BY mci.checkedInAt DESC")
    List<MealCheckIn> findByUserIdAndDateRange(@Param("userId") Long userId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    // Find today's check-ins
    @Query("SELECT mci FROM MealCheckIn mci JOIN mci.meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mp.date = :date " +
            "ORDER BY mci.checkedInAt DESC")
    List<MealCheckIn> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // Find recent check-ins
    List<MealCheckIn> findTop10ByUserOrderByCheckedInAtDesc(User user);

    List<MealCheckIn> findTop10ByUserIdOrderByCheckedInAtDesc(Long userId);

    // Satisfaction statistics
    @Query("SELECT AVG(mci.satisfactionRating) FROM MealCheckIn mci JOIN mci.meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mci.satisfactionRating IS NOT NULL " +
            "AND mp.date BETWEEN :startDate AND :endDate")
    Double getAverageSatisfactionRating(@Param("userId") Long userId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    // Completion statistics
    @Query("SELECT AVG(mci.completionPercentage) FROM MealCheckIn mci JOIN mci.meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mp.date BETWEEN :startDate AND :endDate")
    Double getAverageCompletionPercentage(@Param("userId") Long userId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // Check-ins by satisfaction rating
    @Query("SELECT mci.satisfactionRating, COUNT(mci) FROM MealCheckIn mci JOIN mci.meal m JOIN m.mealPlan mp " +
            "WHERE mp.user.id = :userId AND mci.satisfactionRating IS NOT NULL " +
            "AND mp.date BETWEEN :startDate AND :endDate " +
            "GROUP BY mci.satisfactionRating " +
            "ORDER BY mci.satisfactionRating")
    List<Object[]> getSatisfactionRatingDistribution(@Param("userId") Long userId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
}
