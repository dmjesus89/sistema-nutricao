package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.food.Supplement;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.UserSupplementPreference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSupplementPreferenceRepository extends JpaRepository<UserSupplementPreference, Long> {

    /**
     * Find preference by user and supplement
     */
    Optional<UserSupplementPreference> findByUserAndSupplement(User user, Supplement supplement);

    /**
     * Find all preferences for a user
     */
    List<UserSupplementPreference> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Find preferences by user with pagination
     */
    Page<UserSupplementPreference> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * Find preferences by user and specific type
     */
    List<UserSupplementPreference> findByUserAndPreferenceType(User user, UserSupplementPreference.PreferenceType preferenceType);

    /**
     * Find preferences by user and multiple types
     */
    @Query("SELECT usp FROM UserSupplementPreference usp WHERE usp.user = :user AND " +
            "usp.preferenceType IN :preferenceTypes ORDER BY usp.createdAt DESC")
    List<UserSupplementPreference> findByUserAndPreferenceTypes(@Param("user") User user,
                                                                @Param("preferenceTypes") List<UserSupplementPreference.PreferenceType> preferenceTypes);

    /**
     * Check if preference exists
     */
    boolean existsByUserAndSupplement(User user, Supplement supplement);

    /**
     * Delete preference
     */
    @Modifying
    void deleteByUserAndSupplement(User user, Supplement supplement);

    /**
     * Count preferences by user and type
     */
    @Query("SELECT COUNT(usp) FROM UserSupplementPreference usp WHERE usp.user = :user AND usp.preferenceType = :type")
    long countByUserAndPreferenceType(@Param("user") User user, @Param("type") UserSupplementPreference.PreferenceType type);

    /**
     * Find user's favorite supplements with supplement details
     */
    @Query("SELECT usp FROM UserSupplementPreference usp JOIN FETCH usp.supplement s WHERE usp.user = :user AND " +
            "usp.preferenceType = 'FAVORITE' AND s.active = true ORDER BY usp.createdAt DESC")
    List<UserSupplementPreference> findUserFavoritesWithSupplement(@Param("user") User user);

    /**
     * Find user's current supplements with supplement details
     */
    @Query("SELECT usp FROM UserSupplementPreference usp JOIN FETCH usp.supplement s WHERE usp.user = :user AND " +
            "usp.preferenceType = 'CURRENTLY_USING' AND s.active = true ORDER BY usp.createdAt DESC")
    List<UserSupplementPreference> findUserCurrentSupplementsWithDetails(@Param("user") User user);

    /**
     * Find user's supplement wishlist
     */
    @Query("SELECT usp FROM UserSupplementPreference usp JOIN FETCH usp.supplement s WHERE usp.user = :user AND " +
            "usp.preferenceType = 'WANT_TO_TRY' AND s.active = true ORDER BY usp.createdAt DESC")
    List<UserSupplementPreference> findUserWishlistWithDetails(@Param("user") User user);

    /**
     * Find user's restricted supplements with supplement details
     */
    @Query("SELECT usp FROM UserSupplementPreference usp JOIN FETCH usp.supplement s WHERE usp.user = :user AND " +
            "usp.preferenceType IN ('RESTRICTION', 'NOT_SUITABLE') AND s.active = true ORDER BY s.name ASC")
    List<UserSupplementPreference> findUserRestrictionsWithSupplement(@Param("user") User user);

    /**
     * Find preferences by supplement category
     */
    @Query("SELECT usp FROM UserSupplementPreference usp JOIN usp.supplement s WHERE usp.user = :user AND " +
            "s.category = :category AND s.active = true ORDER BY usp.createdAt DESC")
    List<UserSupplementPreference> findByUserAndSupplementCategory(@Param("user") User user,
                                                                   @Param("category") Supplement.SupplementCategory category);

    /**
     * Get preference statistics for user
     */
    @Query("SELECT usp.preferenceType, COUNT(usp) FROM UserSupplementPreference usp WHERE usp.user = :user " +
            "GROUP BY usp.preferenceType ORDER BY COUNT(usp) DESC")
    List<Object[]> getUserPreferenceStatistics(@Param("user") User user);

    /**
     * Find recently added preferences
     */
    @Query("SELECT usp FROM UserSupplementPreference usp WHERE usp.user = :user AND usp.createdAt >= :since " +
            "ORDER BY usp.createdAt DESC")
    List<UserSupplementPreference> findRecentPreferences(@Param("user") User user, @Param("since") LocalDateTime since);

    /**
     * Find supplements user tried (currently using or used before)
     */
    @Query("SELECT usp FROM UserSupplementPreference usp JOIN FETCH usp.supplement s WHERE usp.user = :user AND " +
            "usp.preferenceType IN ('CURRENTLY_USING', 'USED_BEFORE') AND s.active = true ORDER BY usp.createdAt DESC")
    List<UserSupplementPreference> findSupplementsUserTried(@Param("user") User user);

    /**
     * Find preferences with notes
     */
    @Query("SELECT usp FROM UserSupplementPreference usp WHERE usp.user = :user AND usp.notes IS NOT NULL AND usp.notes != '' " +
            "ORDER BY usp.createdAt DESC")
    List<UserSupplementPreference> findPreferencesWithNotes(@Param("user") User user);

    /**
     * Bulk delete preferences by supplement IDs
     */
    @Modifying
    @Query("DELETE FROM UserSupplementPreference usp WHERE usp.user = :user AND usp.supplement.id IN :supplementIds")
    int bulkDeleteByUserAndSupplementIds(@Param("user") User user, @Param("supplementIds") List<Long> supplementIds);

    /**
     * Update preference type
     */
    @Modifying
    @Query("UPDATE UserSupplementPreference usp SET usp.preferenceType = :newType WHERE usp.user = :user AND usp.supplement = :supplement")
    int updatePreferenceType(@Param("user") User user, @Param("supplement") Supplement supplement, @Param("newType") UserSupplementPreference.PreferenceType newType);

    /**
     * Find all preferences with email reminders enabled for a specific preference type
     */
    @Query("SELECT usp FROM UserSupplementPreference usp JOIN FETCH usp.supplement s JOIN FETCH usp.user u " +
            "WHERE usp.emailReminderEnabled = true AND usp.preferenceType = :preferenceType AND s.active = true")
    List<UserSupplementPreference> findByEmailReminderEnabledTrueAndPreferenceType(
            @Param("preferenceType") UserSupplementPreference.PreferenceType preferenceType);
}


