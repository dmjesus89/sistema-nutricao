package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.Supplement;
import com.nutrition.domain.entity.food.UserFoodPreference;
import com.nutrition.domain.entity.food.UserSupplementPreference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplementRepository extends JpaRepository<Supplement, Long> {

    // ========== BASIC QUERIES ==========

    /**
     * Find all active supplements with pagination
     */
    Page<Supplement> findByActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find all active supplements as list
     */
    List<Supplement> findByActiveTrueOrderByNameAsc();

    /**
     * Find active supplement by ID
     */
    Optional<Supplement> findByIdAndActiveTrue(Long id);

    /**
     * Check if supplement exists by ID and is active
     */
    boolean existsByIdAndActiveTrue(Long id);

    // ========== SEARCH QUERIES ==========

    /**
     * Search supplements by text in name, description, brand, or main ingredient
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND " +
            "(LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.mainIngredient) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Supplement> searchByNameDescriptionBrandOrIngredient(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search supplements with multiple text criteria
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:description IS NULL OR LOWER(s.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:brand IS NULL OR LOWER(s.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:ingredient IS NULL OR LOWER(s.mainIngredient) LIKE LOWER(CONCAT('%', :ingredient, '%')))")
    Page<Supplement> searchByMultipleTextCriteria(@Param("name") String name,
                                                  @Param("description") String description,
                                                  @Param("brand") String brand,
                                                  @Param("ingredient") String ingredient,
                                                  Pageable pageable);

    // ========== CATEGORY QUERIES ==========

    /**
     * Find supplements by category
     */
    Page<Supplement> findByCategoryAndActiveTrueOrderByNameAsc(Supplement.SupplementCategory category, Pageable pageable);

    /**
     * Find supplements by multiple categories
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND s.category IN :categories ORDER BY s.name ASC")
    Page<Supplement> findByCategoriesAndActiveTrue(@Param("categories") List<Supplement.SupplementCategory> categories, Pageable pageable);

    /**
     * Get supplements count by category
     */
    @Query("SELECT s.category, COUNT(s) FROM Supplement s WHERE s.active = true GROUP BY s.category ORDER BY COUNT(s) DESC")
    List<Object[]> countSupplementsByCategory();

    // ========== FORM QUERIES ==========

    /**
     * Find supplements by form
     */
    Page<Supplement> findByFormAndActiveTrueOrderByNameAsc(Supplement.SupplementForm form, Pageable pageable);

    /**
     * Find supplements by multiple forms
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND s.form IN :forms ORDER BY s.name ASC")
    Page<Supplement> findByFormsAndActiveTrue(@Param("forms") List<Supplement.SupplementForm> forms, Pageable pageable);

    /**
     * Get supplements count by form
     */
    @Query("SELECT s.form, COUNT(s) FROM Supplement s WHERE s.active = true GROUP BY s.form ORDER BY COUNT(s) DESC")
    List<Object[]> countSupplementsByForm();

    // ========== BRAND QUERIES ==========

    /**
     * Find supplements by brand (case insensitive)
     */
    Page<Supplement> findByBrandIgnoreCaseAndActiveTrueOrderByNameAsc(String brand, Pageable pageable);

    /**
     * Find supplements by multiple brands
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND LOWER(s.brand) IN :brands ORDER BY s.name ASC")
    Page<Supplement> findByBrandsIgnoreCaseAndActiveTrue(@Param("brands") List<String> brands, Pageable pageable);

    /**
     * Get all distinct brands
     */
    @Query("SELECT DISTINCT s.brand FROM Supplement s WHERE s.active = true AND s.brand IS NOT NULL ORDER BY s.brand")
    List<String> findAllDistinctBrands();

    /**
     * Get top brands by supplement count
     */
    @Query("SELECT s.brand, COUNT(s) FROM Supplement s WHERE s.active = true AND s.brand IS NOT NULL " +
            "GROUP BY s.brand ORDER BY COUNT(s) DESC")
    List<Object[]> getTopBrandsBySupplementCount(Pageable pageable);

    // ========== VERIFICATION QUERIES ==========

    /**
     * Find verified supplements
     */
    Page<Supplement> findByVerifiedTrueAndActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find unverified supplements (for admin review)
     */
    Page<Supplement> findByVerifiedFalseAndActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Count supplements by verification status
     */
    @Query("SELECT s.verified, COUNT(s) FROM Supplement s WHERE s.active = true GROUP BY s.verified")
    List<Object[]> countSupplementsByVerificationStatus();

    // ========== SERVING UNIT QUERIES ==========

    /**
     * Find supplements by serving unit
     */
    Page<Supplement> findByServingUnitAndActiveTrueOrderByNameAsc(Supplement.ServingUnit servingUnit, Pageable pageable);

    /**
     * Get supplements count by serving unit
     */
    @Query("SELECT s.servingUnit, COUNT(s) FROM Supplement s WHERE s.active = true GROUP BY s.servingUnit ORDER BY COUNT(s) DESC")
    List<Object[]> countSupplementsByServingUnit();

    // ========== NUTRITIONAL VALUE QUERIES ==========

    /**
     * Find supplements with nutritional value (calories > 0)
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND s.caloriesPerServing IS NOT NULL AND s.caloriesPerServing > 0 ORDER BY s.name ASC")
    Page<Supplement> findSupplementsWithNutritionalValue(Pageable pageable);

    /**
     * Find high protein supplements
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND s.proteinPerServing IS NOT NULL AND s.proteinPerServing >= :minProtein ORDER BY s.proteinPerServing DESC")
    Page<Supplement> findHighProteinSupplements(@Param("minProtein") java.math.BigDecimal minProtein, Pageable pageable);

    /**
     * Find low calorie supplements
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND " +
            "(s.caloriesPerServing IS NULL OR s.caloriesPerServing <= :maxCalories) ORDER BY s.name ASC")
    Page<Supplement> findLowCalorieSupplements(@Param("maxCalories") java.math.BigDecimal maxCalories, Pageable pageable);

    // ========== USER PREFERENCE QUERIES ==========

    @Query("SELECT s FROM Supplement s JOIN s.userPreferences up WHERE up.user = :user AND " +
            "up.preferenceType IS NOT NULL AND s.active = true ORDER BY up.createdAt DESC")
    List<Supplement> findUserWithPreferences(@Param("user") User user);

    /**
     * Find user's favorite supplements
     */
    @Query("SELECT s FROM Supplement s JOIN s.userPreferences up WHERE up.user = :user AND " +
            "up.preferenceType = 'FAVORITE' AND s.active = true ORDER BY up.createdAt DESC")
    List<Supplement> findUserFavorites(@Param("user") User user);

    /**
     * Find user's current supplements
     */
    @Query("SELECT s FROM Supplement s JOIN s.userPreferences up WHERE up.user = :user AND " +
            "up.preferenceType = 'CURRENTLY_USING' AND s.active = true ORDER BY up.createdAt DESC")
    List<Supplement> findUserCurrentSupplements(@Param("user") User user);

    /**
     * Find supplements user used before
     */
    @Query("SELECT s FROM Supplement s JOIN s.userPreferences up WHERE up.user = :user AND " +
            "up.preferenceType = 'USED_BEFORE' AND s.active = true ORDER BY up.createdAt DESC")
    List<Supplement> findUserPreviousSupplements(@Param("user") User user);

    /**
     * Find supplements user wants to try
     */
    @Query("SELECT s FROM Supplement s JOIN s.userPreferences up WHERE up.user = :user AND " +
            "up.preferenceType = 'WANT_TO_TRY' AND s.active = true ORDER BY up.createdAt DESC")
    List<Supplement> findUserWishlistSupplements(@Param("user") User user);

    /**
     * Find user's restricted supplements
     */
    @Query("SELECT s FROM Supplement s JOIN s.userPreferences up WHERE up.user = :user AND " +
            "up.preferenceType IN ('RESTRICTION', 'NOT_SUITABLE') AND s.active = true ORDER BY s.name ASC")
    List<Supplement> findUserRestrictions(@Param("user") User user);

    /**
     * Find suitable supplements for user (excluding restrictions)
     */
    @Query("SELECT DISTINCT s FROM Supplement s WHERE s.active = true AND s.id NOT IN " +
            "(SELECT up.supplement.id FROM UserSupplementPreference up WHERE up.user = :user AND " +
            "up.preferenceType IN ('RESTRICTION', 'NOT_SUITABLE')) " +
            "ORDER BY s.name ASC")
    Page<Supplement> findSuitableSupplementsForUser(@Param("user") User user, Pageable pageable);

    /**
     * Find recommended supplements for user (suitable + prioritize favorites and wishlist)
     */
    @Query("SELECT s FROM Supplement s " +
            "WHERE s.active = true AND s.verified = true AND s.id NOT IN " +
            "(SELECT up.supplement.id FROM UserSupplementPreference up WHERE up.user = :user AND " +
            "up.preferenceType IN ('RESTRICTION', 'NOT_SUITABLE')) " +
            "AND s.id IN (" +
            "   SELECT DISTINCT s2.id FROM Supplement s2" +
            ") " +
            "ORDER BY CASE " +
            "WHEN s.id IN (SELECT up2.supplement.id FROM UserSupplementPreference up2 WHERE up2.user = :user AND up2.preferenceType = 'FAVORITE') THEN 0 " +
            "WHEN s.id IN (SELECT up3.supplement.id FROM UserSupplementPreference up3 WHERE up3.user = :user AND up3.preferenceType = 'WANT_TO_TRY') THEN 1 " +
            "ELSE 2 END, s.name ASC")
    Page<Supplement> findRecommendedSupplementsForUser(@Param("user") User user, Pageable pageable);

    // ========== INGREDIENT QUERIES ==========

    /**
     * Find supplements by main ingredient
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND " +
            "LOWER(s.mainIngredient) LIKE LOWER(CONCAT('%', :ingredient, '%')) ORDER BY s.name ASC")
    Page<Supplement> findByMainIngredient(@Param("ingredient") String ingredient, Pageable pageable);

    /**
     * Get all distinct main ingredients
     */
    @Query("SELECT DISTINCT s.mainIngredient FROM Supplement s WHERE s.active = true AND s.mainIngredient IS NOT NULL ORDER BY s.mainIngredient")
    List<String> findAllDistinctMainIngredients();

    /**
     * Get top ingredients by supplement count
     */
    @Query("SELECT s.mainIngredient, COUNT(s) FROM Supplement s WHERE s.active = true AND s.mainIngredient IS NOT NULL " +
            "GROUP BY s.mainIngredient ORDER BY COUNT(s) DESC")
    List<Object[]> getTopIngredientsBySupplementCount(Pageable pageable);

    // ========== ADVANCED SEARCH ==========

    /**
     * Advanced search with all possible filters
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:category IS NULL OR s.category = :category) AND " +
            "(:form IS NULL OR s.form = :form) AND " +
            "(:brand IS NULL OR LOWER(s.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:ingredient IS NULL OR LOWER(s.mainIngredient) LIKE LOWER(CONCAT('%', :ingredient, '%'))) AND " +
            "(:servingUnit IS NULL OR s.servingUnit = :servingUnit) AND " +
            "(:verified IS NULL OR s.verified = :verified) AND " +
            "(:hasNutritionalValue IS NULL OR " +
            "(:hasNutritionalValue = false) OR " +
            "(:hasNutritionalValue = true AND s.caloriesPerServing IS NOT NULL AND s.caloriesPerServing > 0)) " +
            "ORDER BY s.name ASC")
    Page<Supplement> findByAdvancedFilters(@Param("name") String name,
                                           @Param("category") Supplement.SupplementCategory category,
                                           @Param("form") Supplement.SupplementForm form,
                                           @Param("brand") String brand,
                                           @Param("ingredient") String ingredient,
                                           @Param("servingUnit") Supplement.ServingUnit servingUnit,
                                           @Param("verified") Boolean verified,
                                           @Param("hasNutritionalValue") Boolean hasNutritionalValue,
                                           Pageable pageable);

    // ========== STATISTICS QUERIES ==========

    /**
     * Count all active supplements
     */
    @Query("SELECT COUNT(s) FROM Supplement s WHERE s.active = true")
    long countActiveSupplements();

    /**
     * Count verified supplements
     */
    @Query("SELECT COUNT(s) FROM Supplement s WHERE s.verified = true AND s.active = true")
    long countVerifiedSupplements();

    /**
     * Count supplements with nutritional information
     */
    @Query("SELECT COUNT(s) FROM Supplement s WHERE s.active = true AND s.caloriesPerServing IS NOT NULL AND s.caloriesPerServing > 0")
    long countSupplementsWithNutritionalValue();

    /**
     * Get average serving size by form
     */
    @Query("SELECT s.form, AVG(s.servingSize) FROM Supplement s WHERE s.active = true AND s.servingSize IS NOT NULL " +
            "GROUP BY s.form ORDER BY s.form ASC")
    List<Object[]> getAverageServingSizeByForm();

    /**
     * Get supplements with most servings per container
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND s.servingsPerContainer IS NOT NULL " +
            "ORDER BY s.servingsPerContainer DESC")
    List<Supplement> findSupplementsOrderByServingsPerContainer(Pageable pageable);

    // ========== PRICE AND VALUE QUERIES (for future implementation) ==========

    /**
     * Find supplements by category and form combination
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND s.category = :category AND s.form = :form ORDER BY s.name ASC")
    Page<Supplement> findByCategoryAndForm(@Param("category") Supplement.SupplementCategory category,
                                           @Param("form") Supplement.SupplementForm form,
                                           Pageable pageable);

    /**
     * Find supplements with warnings
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND s.warnings IS NOT NULL AND s.warnings != '' ORDER BY s.name ASC")
    Page<Supplement> findSupplementsWithWarnings(Pageable pageable);

    /**
     * Find supplements with regulatory information
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND s.regulatoryInfo IS NOT NULL AND s.regulatoryInfo != '' ORDER BY s.name ASC")
    Page<Supplement> findSupplementsWithRegulatoryInfo(Pageable pageable);

    // ========== MAINTENANCE QUERIES ==========

    /**
     * Find supplements without main ingredient
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND (s.mainIngredient IS NULL OR s.mainIngredient = '') ORDER BY s.name ASC")
    List<Supplement> findSupplementsWithoutMainIngredient();

    /**
     * Find supplements without usage instructions
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND (s.usageInstructions IS NULL OR s.usageInstructions = '') ORDER BY s.name ASC")
    List<Supplement> findSupplementsWithoutUsageInstructions();

    /**
     * Find supplements without recommended dosage
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND (s.recommendedDosage IS NULL OR s.recommendedDosage = '') ORDER BY s.name ASC")
    List<Supplement> findSupplementsWithoutRecommendedDosage();

    /**
     * Find potential duplicate supplements (same name and brand)
     */
    @Query("SELECT s1 FROM Supplement s1, Supplement s2 WHERE s1.active = true AND s2.active = true AND " +
            "s1.id < s2.id AND LOWER(s1.name) = LOWER(s2.name) AND " +
            "((s1.brand IS NULL AND s2.brand IS NULL) OR LOWER(s1.brand) = LOWER(s2.brand)) " +
            "ORDER BY s1.name ASC")
    List<Supplement> findPotentialDuplicates();

    /**
     * Find supplements created by specific user
     */
    Page<Supplement> findByCreatedByAndActiveTrueOrderByCreatedAtDesc(User createdBy, Pageable pageable);

    /**
     * Find supplements created in date range
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND s.createdAt BETWEEN :startDate AND :endDate ORDER BY s.createdAt DESC")
    Page<Supplement> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                            @Param("endDate") java.time.LocalDateTime endDate,
                                            Pageable pageable);

    // ========== RECOMMENDATIONS AND COMPATIBILITY ==========

    /**
     * Find supplements compatible with user's dietary restrictions
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND s.verified = true AND " +
            "(:isVegetarian = false OR s.category != 'PROTEIN' OR LOWER(s.name) NOT LIKE '%whey%') AND " +
            "(:isVegan = false OR (s.category != 'PROTEIN' OR (LOWER(s.name) NOT LIKE '%whey%' AND LOWER(s.name) NOT LIKE '%casein%'))) AND " +
            "(:isDiabetic = false OR s.carbsPerServing IS NULL OR s.carbsPerServing <= 5) " +
            "ORDER BY s.name ASC")
    Page<Supplement> findCompatibleSupplements(@Param("isVegetarian") boolean isVegetarian,
                                               @Param("isVegan") boolean isVegan,
                                               @Param("isDiabetic") boolean isDiabetic,
                                               Pageable pageable);

    /**
     * Find supplements by goal compatibility (weight loss, muscle gain, etc.)
     */
    @Query("SELECT s FROM Supplement s WHERE s.active = true AND s.verified = true AND " +
            "(:goalWeightLoss = false OR s.category IN ('WEIGHT_LOSS', 'PROTEIN', 'VITAMINS')) AND " +
            "(:goalMuscleGain = false OR s.category IN ('PROTEIN', 'CREATINE', 'AMINO_ACIDS', 'WEIGHT_GAIN')) AND " +
            "(:goalEndurance = false OR s.category IN ('ENERGY', 'VITAMINS', 'MINERALS', 'PRE_WORKOUT')) " +
            "ORDER BY s.name ASC")
    Page<Supplement> findSupplementsByGoalCompatibility(@Param("goalWeightLoss") boolean goalWeightLoss,
                                                        @Param("goalMuscleGain") boolean goalMuscleGain,
                                                        @Param("goalEndurance") boolean goalEndurance,
                                                        Pageable pageable);

    // ========== BULK OPERATIONS ==========

    /**
     * Bulk update verification status
     */
    @Query("UPDATE Supplement s SET s.verified = :verified WHERE s.id IN :ids")
    int bulkUpdateVerificationStatus(@Param("ids") List<Long> ids, @Param("verified") boolean verified);

    /**
     * Bulk soft delete
     */
    @Query("UPDATE Supplement s SET s.active = false WHERE s.id IN :ids")
    int bulkSoftDelete(@Param("ids") List<Long> ids);

    /**
     * Bulk update category
     */
    @Query("UPDATE Supplement s SET s.category = :category WHERE s.id IN :ids")
    int bulkUpdateCategory(@Param("ids") List<Long> ids, @Param("category") Supplement.SupplementCategory category);


    @Query("SELECT ufp FROM UserSupplementPreference ufp WHERE ufp.user = :user AND " +
            "ufp.preferenceType IN :preferenceTypes ORDER BY ufp.createdAt DESC")
    List<UserSupplementPreference> findByUserAndPreferenceTypes(@Param("user") User user,
                                                                @Param("preferenceTypes") List<UserSupplementPreference.PreferenceType> preferenceTypes);

    @Query("SELECT ufp FROM UserSupplementPreference ufp WHERE ufp.user = :user AND ufp.preferenceType = 'FAVORITE'")
    Collection<UserSupplementPreference> findByUserAndPreferenceTypeFavorite(@Param("user") User user);

}