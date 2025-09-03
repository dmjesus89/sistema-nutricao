package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.food.Food;
import com.nutrition.domain.entity.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    // ========== BASIC QUERIES ==========

    /**
     * Find all active foods with pagination
     */
    Page<Food> findByActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find all active foods as list
     */
    List<Food> findByActiveTrueOrderByNameAsc();

    /**
     * Find active food by ID
     */
    Optional<Food> findByIdAndActiveTrue(Long id);

    /**
     * Check if food exists by ID and is active
     */
    boolean existsByIdAndActiveTrue(Long id);

    // ========== SEARCH QUERIES ==========

    /**
     * Search foods by text in name, description, or brand
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND " +
            "(LOWER(f.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(f.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(f.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Food> searchByNameDescriptionOrBrand(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search foods with multiple text criteria
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND " +
            "(:name IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:description IS NULL OR LOWER(f.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:brand IS NULL OR LOWER(f.brand) LIKE LOWER(CONCAT('%', :brand, '%')))")
    Page<Food> searchByMultipleTextCriteria(@Param("name") String name,
                                            @Param("description") String description,
                                            @Param("brand") String brand,
                                            Pageable pageable);

    // ========== CATEGORY QUERIES ==========

    /**
     * Find foods by category
     */
    Page<Food> findByCategoryAndActiveTrueOrderByNameAsc(Food.FoodCategory category, Pageable pageable);

    /**
     * Find foods by multiple categories
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND f.category IN :categories ORDER BY f.name ASC")
    Page<Food> findByCategoriesAndActiveTrue(@Param("categories") List<Food.FoodCategory> categories, Pageable pageable);

    // ========== BRAND QUERIES ==========

    /**
     * Find foods by brand (case insensitive)
     */
    Page<Food> findByBrandIgnoreCaseAndActiveTrueOrderByNameAsc(String brand, Pageable pageable);

    /**
     * Find foods by multiple brands
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND LOWER(f.brand) IN :brands ORDER BY f.name ASC")
    Page<Food> findByBrandsIgnoreCaseAndActiveTrue(@Param("brands") List<String> brands, Pageable pageable);

    /**
     * Get all distinct brands
     */
    @Query("SELECT DISTINCT f.brand FROM Food f WHERE f.active = true AND f.brand IS NOT NULL ORDER BY f.brand")
    List<String> findAllDistinctBrands();

    // ========== BARCODE QUERIES ==========

    /**
     * Find food by barcode
     */
    Optional<Food> findByBarcodeAndActiveTrue(String barcode);

    /**
     * Check if barcode exists
     */
    boolean existsByBarcodeAndActiveTrue(String barcode);

    // ========== VERIFICATION QUERIES ==========

    /**
     * Find verified foods
     */
    Page<Food> findByVerifiedTrueAndActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find unverified foods (for admin review)
     */
    Page<Food> findByVerifiedFalseAndActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    // ========== NUTRITIONAL FILTERS ==========

    /**
     * Find foods within calorie range
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND f.caloriesPer100g BETWEEN :minCalories AND :maxCalories ORDER BY f.name ASC")
    Page<Food> findByCalorieRange(@Param("minCalories") BigDecimal minCalories,
                                  @Param("maxCalories") BigDecimal maxCalories,
                                  Pageable pageable);

    /**
     * Find high protein foods
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND f.proteinPer100g >= :minProtein ORDER BY f.proteinPer100g DESC")
    Page<Food> findHighProteinFoods(@Param("minProtein") BigDecimal minProtein, Pageable pageable);

    /**
     * Find low carb foods
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND f.carbsPer100g <= :maxCarbs ORDER BY f.carbsPer100g ASC")
    Page<Food> findLowCarbFoods(@Param("maxCarbs") BigDecimal maxCarbs, Pageable pageable);

    /**
     * Find low fat foods
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND f.fatPer100g <= :maxFat ORDER BY f.fatPer100g ASC")
    Page<Food> findLowFatFoods(@Param("maxFat") BigDecimal maxFat, Pageable pageable);

    /**
     * Find high fiber foods
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND f.fiberPer100g >= :minFiber ORDER BY f.fiberPer100g DESC")
    Page<Food> findHighFiberFoods(@Param("minFiber") BigDecimal minFiber, Pageable pageable);

    /**
     * Find low sodium foods
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND " +
            "(f.sodiumPer100g IS NULL OR f.sodiumPer100g <= :maxSodium) ORDER BY f.name ASC")
    Page<Food> findLowSodiumFoods(@Param("maxSodium") BigDecimal maxSodium, Pageable pageable);

    /**
     * Find foods with specific nutritional characteristics
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND " +
            "(:highProtein = false OR f.proteinPer100g >= 20) AND " +
            "(:lowCarb = false OR f.carbsPer100g <= 5) AND " +
            "(:highFiber = false OR f.fiberPer100g >= 6) AND " +
            "(:lowSodium = false OR f.sodiumPer100g IS NULL OR f.sodiumPer100g <= 140) " +
            "ORDER BY f.name ASC")
    Page<Food> findByNutritionalCharacteristics(@Param("highProtein") boolean highProtein,
                                                @Param("lowCarb") boolean lowCarb,
                                                @Param("highFiber") boolean highFiber,
                                                @Param("lowSodium") boolean lowSodium,
                                                Pageable pageable);

    // ========== USER PREFERENCE QUERIES ==========

    /**
     * Find user's favorite foods
     */
    @Query("SELECT f FROM Food f JOIN f.userPreferences up WHERE up.user = :user AND " +
            "up.preferenceType = 'FAVORITE' AND f.active = true ORDER BY up.createdAt DESC")
    List<Food> findUserFavorites(@Param("user") User user);

    /**
     * Find user's restricted foods
     */
    @Query("SELECT f FROM Food f JOIN f.userPreferences up WHERE up.user = :user AND " +
            "up.preferenceType IN ('RESTRICTION', 'AVOID') AND f.active = true ORDER BY f.name ASC")
    List<Food> findUserRestrictions(@Param("user") User user);

    /**
     * Find foods user dislikes
     */
    @Query("SELECT f FROM Food f JOIN f.userPreferences up WHERE up.user = :user AND " +
            "up.preferenceType = 'DISLIKE' AND f.active = true ORDER BY f.name ASC")
    List<Food> findUserDislikes(@Param("user") User user);

    /**
     * Find suitable foods for user (excluding restrictions and dislikes)
     */
    @Query("SELECT DISTINCT f FROM Food f WHERE f.active = true AND f.id NOT IN " +
            "(SELECT up.food.id FROM UserFoodPreference up WHERE up.user = :user AND " +
            "up.preferenceType IN ('RESTRICTION', 'AVOID', 'DISLIKE')) " +
            "ORDER BY f.name ASC")
    Page<Food> findSuitableFoodsForUser(@Param("user") User user, Pageable pageable);

    /**
     * Find recommended foods for user (suitable + prioritize favorites)
     */
    @Query("SELECT DISTINCT f FROM Food f WHERE f.active = true AND f.verified = true AND f.id NOT IN " +
            "(SELECT up.food.id FROM UserFoodPreference up WHERE up.user = :user AND " +
            "up.preferenceType IN ('RESTRICTION', 'AVOID', 'DISLIKE')) " +
            "ORDER BY CASE WHEN f.id IN " +
            "(SELECT up2.food.id FROM UserFoodPreference up2 WHERE up2.user = :user AND up2.preferenceType = 'FAVORITE') " +
            "THEN 0 ELSE 1 END, f.name ASC")
    Page<Food> findRecommendedFoodsForUser(@Param("user") User user, Pageable pageable);

    // ========== ADVANCED SEARCH ==========

    /**
     * Advanced search with all possible filters
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND " +
            "(:name IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:category IS NULL OR f.category = :category) AND " +
            "(:brand IS NULL OR LOWER(f.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:minCalories IS NULL OR f.caloriesPer100g >= :minCalories) AND " +
            "(:maxCalories IS NULL OR f.caloriesPer100g <= :maxCalories) AND " +
            "(:minProtein IS NULL OR f.proteinPer100g >= :minProtein) AND " +
            "(:maxCarbs IS NULL OR f.carbsPer100g <= :maxCarbs) AND " +
            "(:maxFat IS NULL OR f.fatPer100g <= :maxFat) AND " +
            "(:minFiber IS NULL OR f.fiberPer100g IS NULL OR f.fiberPer100g >= :minFiber) AND " +
            "(:maxSodium IS NULL OR f.sodiumPer100g IS NULL OR f.sodiumPer100g <= :maxSodium) AND " +
            "(:verified IS NULL OR f.verified = :verified) AND " +
            "(:barcode IS NULL OR f.barcode = :barcode) " +
            "ORDER BY f.name ASC")
    Page<Food> findByAdvancedFilters(@Param("name") String name,
                                     @Param("category") Food.FoodCategory category,
                                     @Param("brand") String brand,
                                     @Param("minCalories") BigDecimal minCalories,
                                     @Param("maxCalories") BigDecimal maxCalories,
                                     @Param("minProtein") BigDecimal minProtein,
                                     @Param("maxCarbs") BigDecimal maxCarbs,
                                     @Param("maxFat") BigDecimal maxFat,
                                     @Param("minFiber") BigDecimal minFiber,
                                     @Param("maxSodium") BigDecimal maxSodium,
                                     @Param("verified") Boolean verified,
                                     @Param("barcode") String barcode,
                                     Pageable pageable);


    // ========== STATISTICS QUERIES ==========

    /**
     * Count all active foods
     */
    @Query("SELECT COUNT(f) FROM Food f WHERE f.active = true")
    long countActiveFoods();

    /**
     * Count verified foods
     */
    @Query("SELECT COUNT(f) FROM Food f WHERE f.verified = true AND f.active = true")
    long countVerifiedFoods();

    /**
     * Count foods by category
     */
    @Query("SELECT f.category, COUNT(f) FROM Food f WHERE f.active = true GROUP BY f.category ORDER BY COUNT(f) DESC")
    List<Object[]> countFoodsByCategory();

    /**
     * Count foods by verification status
     */
    @Query("SELECT f.verified, COUNT(f) FROM Food f WHERE f.active = true GROUP BY f.verified")
    List<Object[]> countFoodsByVerificationStatus();

    /**
     * Get top brands by food count
     */
    @Query("SELECT f.brand, COUNT(f) FROM Food f WHERE f.active = true AND f.brand IS NOT NULL " +
            "GROUP BY f.brand ORDER BY COUNT(f) DESC")
    List<Object[]> getTopBrandsByFoodCount(Pageable pageable);

    /**
     * Get nutritional statistics
     */
    @Query("SELECT " +
            "AVG(f.caloriesPer100g) as avgCalories, " +
            "AVG(f.proteinPer100g) as avgProtein, " +
            "AVG(f.carbsPer100g) as avgCarbs, " +
            "AVG(f.fatPer100g) as avgFat " +
            "FROM Food f WHERE f.active = true")
    Object[] getNutritionalStatistics();

    // ========== MAINTENANCE QUERIES ==========

    /**
     * Find foods without serving size
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND f.servingSize IS NULL ORDER BY f.name ASC")
    List<Food> findFoodsWithoutServingSize();

    /**
     * Find foods without fiber information
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND f.fiberPer100g IS NULL ORDER BY f.name ASC")
    List<Food> findFoodsWithoutFiberInfo();

    /**
     * Find duplicate foods (same name and brand)
     */
    @Query("SELECT f1 FROM Food f1, Food f2 WHERE f1.active = true AND f2.active = true AND " +
            "f1.id < f2.id AND LOWER(f1.name) = LOWER(f2.name) AND " +
            "((f1.brand IS NULL AND f2.brand IS NULL) OR LOWER(f1.brand) = LOWER(f2.brand)) " +
            "ORDER BY f1.name ASC")
    List<Food> findPotentialDuplicates();

    /**
     * Find foods created by specific user
     */
    Page<Food> findByCreatedByAndActiveTrueOrderByCreatedAtDesc(User createdBy, Pageable pageable);

    /**
     * Find foods created in date range
     */
    @Query("SELECT f FROM Food f WHERE f.active = true AND f.createdAt BETWEEN :startDate AND :endDate ORDER BY f.createdAt DESC")
    Page<Food> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                      @Param("endDate") java.time.LocalDateTime endDate,
                                      Pageable pageable);

    // ========== BULK OPERATIONS ==========

    /**
     * Bulk update verification status
     */
    @Query("UPDATE Food f SET f.verified = :verified WHERE f.id IN :ids")
    int bulkUpdateVerificationStatus(@Param("ids") List<Long> ids, @Param("verified") boolean verified);

    /**
     * Bulk soft delete
     */
    @Query("UPDATE Food f SET f.active = false WHERE f.id IN :ids")
    int bulkSoftDelete(@Param("ids") List<Long> ids);

}