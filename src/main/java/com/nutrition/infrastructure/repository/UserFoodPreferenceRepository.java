package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.food.Food;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.UserFoodPreference;
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

// ========== USER FOOD PREFERENCE REPOSITORY ==========

@Repository
public interface UserFoodPreferenceRepository extends JpaRepository<UserFoodPreference, Long> {

    /**
     * Find preference by user and food
     */
    Optional<UserFoodPreference> findByUserAndFood(User user, Food food);

    /**
     * Find all preferences for a user
     */
    List<UserFoodPreference> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Find preferences by user with pagination
     */
    Page<UserFoodPreference> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * Find preferences by user and specific type
     */
    List<UserFoodPreference> findByUserAndPreferenceType(User user, UserFoodPreference.PreferenceType preferenceType);

    /**
     * Find preferences by user and multiple types
     */
    @Query("SELECT ufp FROM UserFoodPreference ufp WHERE ufp.user = :user AND " +
            "ufp.preferenceType IN :preferenceTypes ORDER BY ufp.createdAt DESC")
    List<UserFoodPreference> findByUserAndPreferenceTypes(@Param("user") User user,
                                                          @Param("preferenceTypes") List<UserFoodPreference.PreferenceType> preferenceTypes);

    /**
     * Check if preference exists
     */
    boolean existsByUserAndFood(User user, Food food);

    /**
     * Delete preference
     */
    @Modifying
    void deleteByUserAndFood(User user, Food food);

    /**
     * Count preferences by user and type
     */
    @Query("SELECT COUNT(ufp) FROM UserFoodPreference ufp WHERE ufp.user = :user AND ufp.preferenceType = :type")
    long countByUserAndPreferenceType(@Param("user") User user, @Param("type") UserFoodPreference.PreferenceType type);

    /**
     * Find user's favorite foods with food details
     */
    @Query("SELECT ufp FROM UserFoodPreference ufp JOIN FETCH ufp.food f WHERE ufp.user = :user AND " +
            "ufp.preferenceType = 'FAVORITE' AND f.active = true ORDER BY ufp.createdAt DESC")
    List<UserFoodPreference> findUserFavoritesWithFood(@Param("user") User user);

    /**
     * Find user's restricted foods with food details
     */
    @Query("SELECT ufp FROM UserFoodPreference ufp JOIN FETCH ufp.food f WHERE ufp.user = :user AND " +
            "ufp.preferenceType IN ('RESTRICTION', 'AVOID') AND f.active = true ORDER BY f.name ASC")
    List<UserFoodPreference> findUserRestrictionsWithFood(@Param("user") User user);

    /**
     * Find preferences by food category
     */
    @Query("SELECT ufp FROM UserFoodPreference ufp JOIN ufp.food f WHERE ufp.user = :user AND " +
            "f.category = :category AND f.active = true ORDER BY ufp.createdAt DESC")
    List<UserFoodPreference> findByUserAndFoodCategory(@Param("user") User user,
                                                       @Param("category") Food.FoodCategory category);

    /**
     * Get preference statistics for user
     */
    @Query("SELECT ufp.preferenceType, COUNT(ufp) FROM UserFoodPreference ufp WHERE ufp.user = :user " +
            "GROUP BY ufp.preferenceType ORDER BY COUNT(ufp) DESC")
    List<Object[]> getUserPreferenceStatistics(@Param("user") User user);

    /**
     * Find recently added preferences
     */
    @Query("SELECT ufp FROM UserFoodPreference ufp WHERE ufp.user = :user AND ufp.createdAt >= :since " +
            "ORDER BY ufp.createdAt DESC")
    List<UserFoodPreference> findRecentPreferences(@Param("user") User user, @Param("since") LocalDateTime since);

    /**
     * Find preferences with notes
     */
    @Query("SELECT ufp FROM UserFoodPreference ufp WHERE ufp.user = :user AND ufp.notes IS NOT NULL AND ufp.notes != '' " +
            "ORDER BY ufp.createdAt DESC")
    List<UserFoodPreference> findPreferencesWithNotes(@Param("user") User user);

    /**
     * Bulk delete preferences by food IDs
     */
    @Modifying
    @Query("DELETE FROM UserFoodPreference ufp WHERE ufp.user = :user AND ufp.food.id IN :foodIds")
    int bulkDeleteByUserAndFoodIds(@Param("user") User user, @Param("foodIds") List<Long> foodIds);

    /**
     * Update preference type
     */
    @Modifying
    @Query("UPDATE UserFoodPreference ufp SET ufp.preferenceType = :newType WHERE ufp.user = :user AND ufp.food = :food")
    int updatePreferenceType(@Param("user") User user, @Param("food") Food food, @Param("newType") UserFoodPreference.PreferenceType newType);
}
