package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.food.Supplement;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.UserSupplement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSupplementRepository extends JpaRepository<UserSupplement, Long> {

    /**
     * Find user supplement by user and supplement
     */
    Optional<UserSupplement> findByUserAndSupplement(User user, Supplement supplement);

    /**
     * Find all supplements for a user
     */
    List<UserSupplement> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Find user supplements with pagination
     */
    Page<UserSupplement> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * Find user supplements by frequency
     */
    List<UserSupplement> findByUserAndFrequency(User user, UserSupplement.Frequency frequency);

    /**
     * Check if user supplement exists
     */
    boolean existsByUserAndSupplement(User user, Supplement supplement);

    /**
     * Delete user supplement
     */
    @Modifying
    void deleteByUserAndSupplement(User user, Supplement supplement);

    /**
     * Count supplements by user
     */
    long countByUser(User user);

    /**
     * Count supplements by user and frequency
     */
    long countByUserAndFrequency(User user, UserSupplement.Frequency frequency);

    /**
     * Find user's supplements with supplement details eagerly loaded
     */
    @Query("SELECT us FROM UserSupplement us JOIN FETCH us.supplement s WHERE us.user = :user AND " +
            "s.active = true ORDER BY us.createdAt DESC")
    List<UserSupplement> findByUserWithSupplementDetails(@Param("user") User user);

    /**
     * Find user's supplements by frequency with supplement details
     */
    @Query("SELECT us FROM UserSupplement us JOIN FETCH us.supplement s WHERE us.user = :user AND " +
            "us.frequency = :frequency AND s.active = true ORDER BY us.createdAt DESC")
    List<UserSupplement> findByUserAndFrequencyWithDetails(@Param("user") User user,
                                                            @Param("frequency") UserSupplement.Frequency frequency);

    /**
     * Find user's supplements by category
     */
    @Query("SELECT us FROM UserSupplement us JOIN us.supplement s WHERE us.user = :user AND " +
            "s.category = :category AND s.active = true ORDER BY us.createdAt DESC")
    List<UserSupplement> findByUserAndSupplementCategory(@Param("user") User user,
                                                          @Param("category") Supplement.SupplementCategory category);

    /**
     * Find supplements with email reminders enabled
     */
    @Query("SELECT us FROM UserSupplement us JOIN FETCH us.supplement s JOIN FETCH us.user u " +
            "WHERE us.emailReminderEnabled = true AND s.active = true")
    List<UserSupplement> findByEmailReminderEnabledTrue();

    /**
     * Find supplements with email reminders enabled for a specific time window
     */
    @Query("SELECT us FROM UserSupplement us JOIN FETCH us.supplement s JOIN FETCH us.user u " +
            "WHERE us.emailReminderEnabled = true AND us.dosageTime BETWEEN :startTime AND :endTime AND s.active = true")
    List<UserSupplement> findByEmailReminderEnabledAndDosageTimeBetween(
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    /**
     * Find supplements with email reminders enabled by frequency
     */
    @Query("SELECT us FROM UserSupplement us JOIN FETCH us.supplement s JOIN FETCH us.user u " +
            "WHERE us.emailReminderEnabled = true AND us.frequency = :frequency AND s.active = true")
    List<UserSupplement> findByEmailReminderEnabledAndFrequency(@Param("frequency") UserSupplement.Frequency frequency);

    /**
     * Find supplements with notes
     */
    @Query("SELECT us FROM UserSupplement us WHERE us.user = :user AND us.notes IS NOT NULL AND us.notes != '' " +
            "ORDER BY us.createdAt DESC")
    List<UserSupplement> findByUserWithNotes(@Param("user") User user);

    /**
     * Find recently added user supplements
     */
    @Query("SELECT us FROM UserSupplement us WHERE us.user = :user AND us.createdAt >= :since " +
            "ORDER BY us.createdAt DESC")
    List<UserSupplement> findRecentlyAdded(@Param("user") User user, @Param("since") LocalDateTime since);

    /**
     * Find supplements that haven't been taken in a while
     */
    @Query("SELECT us FROM UserSupplement us WHERE us.user = :user AND " +
            "(us.lastTakenAt IS NULL OR us.lastTakenAt < :since) " +
            "ORDER BY us.lastTakenAt ASC NULLS FIRST")
    List<UserSupplement> findNotTakenSince(@Param("user") User user, @Param("since") LocalDateTime since);

    /**
     * Get frequency statistics for user
     */
    @Query("SELECT us.frequency, COUNT(us) FROM UserSupplement us WHERE us.user = :user " +
            "GROUP BY us.frequency ORDER BY COUNT(us) DESC")
    List<Object[]> getUserFrequencyStatistics(@Param("user") User user);

    /**
     * Bulk delete by supplement IDs
     */
    @Modifying
    @Query("DELETE FROM UserSupplement us WHERE us.user = :user AND us.supplement.id IN :supplementIds")
    int bulkDeleteByUserAndSupplementIds(@Param("user") User user, @Param("supplementIds") List<Long> supplementIds);

    /**
     * Update last taken timestamp
     */
    @Modifying
    @Query("UPDATE UserSupplement us SET us.lastTakenAt = :takenAt WHERE us.user = :user AND us.supplement = :supplement")
    int updateLastTakenAt(@Param("user") User user, @Param("supplement") Supplement supplement, @Param("takenAt") LocalDateTime takenAt);
}
