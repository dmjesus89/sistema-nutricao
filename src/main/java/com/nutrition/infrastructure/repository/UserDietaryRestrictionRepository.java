package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.UserDietaryRestriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDietaryRestrictionRepository extends JpaRepository<UserDietaryRestriction, Long> {

    /**
     * Find active dietary restrictions for user
     */
    List<UserDietaryRestriction> findByUserAndActiveTrueOrderByCreatedAtDesc(User user);

    /**
     * Find dietary restrictions by user and type
     */
    List<UserDietaryRestriction> findByUserAndRestrictionTypeAndActiveTrue(User user,
                                                                           UserDietaryRestriction.DietaryRestrictionType restrictionType);

    /**
     * Find dietary restrictions by user and severity
     */
    List<UserDietaryRestriction> findByUserAndSeverityAndActiveTrue(User user,
                                                                    UserDietaryRestriction.Severity severity);

    /**
     * Check if user has specific dietary restriction
     */
    boolean existsByUserAndRestrictionTypeAndActiveTrue(User user,
                                                        UserDietaryRestriction.DietaryRestrictionType restrictionType);

    /**
     * Count active restrictions by user
     */
    @Query("SELECT COUNT(udr) FROM UserDietaryRestriction udr WHERE udr.user = :user AND udr.active = true")
    long countActiveRestrictionsByUser(@Param("user") User user);

    /**
     * Find restrictions with notes
     */
    @Query("SELECT udr FROM UserDietaryRestriction udr WHERE udr.user = :user AND udr.active = true AND " +
            "udr.notes IS NOT NULL AND udr.notes != '' ORDER BY udr.createdAt DESC")
    List<UserDietaryRestriction> findActiveRestrictionsWithNotes(@Param("user") User user);

    /**
     * Find restrictions by severity level
     */
    @Query("SELECT udr FROM UserDietaryRestriction udr WHERE udr.user = :user AND udr.active = true AND " +
            "udr.severity IN :severities ORDER BY udr.severity DESC, udr.createdAt DESC")
    List<UserDietaryRestriction> findByUserAndSeverities(@Param("user") User user,
                                                         @Param("severities") List<UserDietaryRestriction.Severity> severities);

    /**
     * Get dietary restriction statistics for user
     */
    @Query("SELECT udr.restrictionType, COUNT(udr) FROM UserDietaryRestriction udr WHERE udr.user = :user AND udr.active = true " +
            "GROUP BY udr.restrictionType ORDER BY COUNT(udr) DESC")
    List<Object[]> getUserRestrictionStatistics(@Param("user") User user);

    /**
     * Get severity distribution for user
     */
    @Query("SELECT udr.severity, COUNT(udr) FROM UserDietaryRestriction udr WHERE udr.user = :user AND udr.active = true " +
            "GROUP BY udr.severity ORDER BY udr.severity DESC")
    List<Object[]> getUserSeverityDistribution(@Param("user") User user);

    /**
     * Find users with specific dietary restriction (for statistics)
     */
    @Query("SELECT COUNT(DISTINCT udr.user) FROM UserDietaryRestriction udr WHERE udr.restrictionType = :restrictionType AND udr.active = true")
    long countUsersWithRestriction(@Param("restrictionType") UserDietaryRestriction.DietaryRestrictionType restrictionType);

    /**
     * Find most common dietary restrictions (global statistics)
     */
    @Query("SELECT udr.restrictionType, COUNT(udr) FROM UserDietaryRestriction udr WHERE udr.active = true " +
            "GROUP BY udr.restrictionType ORDER BY COUNT(udr) DESC")
    List<Object[]> getMostCommonRestrictions();

    /**
     * Deactivate dietary restriction (soft delete)
     */
    @Modifying
    @Query("UPDATE UserDietaryRestriction udr SET udr.active = false WHERE udr.user = :user AND udr.restrictionType = :restrictionType")
    int deactivateRestriction(@Param("user") User user, @Param("restrictionType") UserDietaryRestriction.DietaryRestrictionType restrictionType);

    /**
     * Bulk deactivate restrictions
     */
    @Modifying
    @Query("UPDATE UserDietaryRestriction udr SET udr.active = false WHERE udr.user = :user AND udr.id IN :ids")
    int bulkDeactivateRestrictions(@Param("user") User user, @Param("ids") List<Long> ids);

    /**
     * Update restriction severity
     */
    @Modifying
    @Query("UPDATE UserDietaryRestriction udr SET udr.severity = :newSeverity WHERE udr.user = :user AND udr.restrictionType = :restrictionType AND udr.active = true")
    int updateRestrictionSeverity(@Param("user") User user,
                                  @Param("restrictionType") UserDietaryRestriction.DietaryRestrictionType restrictionType,
                                  @Param("newSeverity") UserDietaryRestriction.Severity newSeverity);
}
