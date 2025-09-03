package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.MealPlan;
import com.nutrition.domain.entity.MealPlan.MealPlanStatus;
import com.nutrition.domain.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    // Find meal plan for specific user and date
    Optional<MealPlan> findByUserAndDate(User user, LocalDate date);

    Optional<MealPlan> findByUserIdAndDate(Long userId, LocalDate date);

    // Find user's meal plans within date range
    List<MealPlan> findByUserAndDateBetweenOrderByDateDesc(
            User user, LocalDate startDate, LocalDate endDate);

    List<MealPlan> findByUserIdAndDateBetweenOrderByDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);

    // Find by status
    List<MealPlan> findByUserAndStatusOrderByDateDesc(User user, MealPlanStatus status);

    List<MealPlan> findByUserIdAndStatusOrderByDateDesc(Long userId, MealPlanStatus status);

    // Recent meal plans for user
    List<MealPlan> findTop7ByUserOrderByDateDesc(User user);

    List<MealPlan> findTop7ByUserIdOrderByDateDesc(Long userId);

    // Statistics queries
    @Query("SELECT COUNT(mp) FROM MealPlan mp WHERE mp.user.id = :userId AND mp.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") MealPlanStatus status);

    @Query("SELECT AVG(mp.consumedCalories) FROM MealPlan mp " +
            "WHERE mp.user.id = :userId AND mp.date BETWEEN :startDate AND :endDate")
    Double getAverageConsumedCalories(@Param("userId") Long userId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT mp FROM MealPlan mp " +
            "WHERE mp.user.id = :userId AND mp.date >= :fromDate " +
            "ORDER BY mp.date DESC")
    List<MealPlan> findRecentPlans(@Param("userId") Long userId, @Param("fromDate") LocalDate fromDate);

    // Check if user has active plan for date
    @Query("SELECT CASE WHEN COUNT(mp) > 0 THEN true ELSE false END " +
            "FROM MealPlan mp WHERE mp.user.id = :userId AND mp.date = :date AND mp.status = 'ACTIVE'")
    boolean hasActivePlanForDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // Delete old plans (for cleanup)
    void deleteByUserIdAndDateBefore(Long userId, LocalDate cutoffDate);
}
