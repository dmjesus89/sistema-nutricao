package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.Food;
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

    Page<Food> findByActiveTrueOrderByNameAsc(Pageable pageable);


    Optional<Food> findByIdAndActiveTrue(Long id);

    Page<Food> findByCategoryAndActiveTrueOrderByNameAsc(Food.FoodCategory category, Pageable pageable);

    Optional<Food> findByBarcodeAndActiveTrue(String barcode);

    @Query("SELECT f FROM Food f JOIN f.userPreferences up WHERE up.user = :user AND " +
            "up.preferenceType IS NOT NULL AND f.active = true ORDER BY up.createdAt DESC")
    List<Food> findUserWithPreferences(@Param("user") User user);

    @Query("SELECT f FROM Food f JOIN f.userPreferences up WHERE up.user = :user AND " +
            "up.preferenceType = 'FAVORITE' AND f.active = true ORDER BY up.createdAt DESC")
    List<Food> findUserFavorites(@Param("user") User user);

    @Query("SELECT DISTINCT f FROM Food f WHERE f.active = true AND f.id NOT IN " +
            "(SELECT up.food.id FROM UserFoodPreference up WHERE up.user = :user AND " +
            "up.preferenceType IN ('RESTRICTION', 'AVOID', 'DISLIKE')) " +
            "ORDER BY f.name ASC")
    Page<Food> findSuitableFoodsForUser(@Param("user") User user, Pageable pageable);

    @Query(value = "SELECT f.* FROM foods f WHERE f.active = true AND " +
            "(:name IS NULL OR remove_accents(LOWER(f.name)) LIKE remove_accents(LOWER(CONCAT('%', :name, '%')))) AND " +
            "(:category IS NULL OR f.category = CAST(:category AS text)) AND " +
            "(:minCalories IS NULL OR f.calories_per_100g >= :minCalories) AND " +
            "(:maxCalories IS NULL OR f.calories_per_100g <= :maxCalories) AND " +
            "(:minProtein IS NULL OR f.protein_per_100g >= :minProtein) AND " +
            "(:maxCarbs IS NULL OR f.carbs_per_100g <= :maxCarbs) AND " +
            "(:maxFat IS NULL OR f.fat_per_100g <= :maxFat) AND " +
            "(:minFiber IS NULL OR f.fiber_per_100g IS NULL OR f.fiber_per_100g >= :minFiber) AND " +
            "(:maxSodium IS NULL OR f.sodium_per_100g IS NULL OR f.sodium_per_100g <= :maxSodium) AND " +
            "(:barcode IS NULL OR f.barcode = :barcode) " +
            "ORDER BY f.name ASC", nativeQuery = true)
    Page<Food> findByAdvancedFilters(@Param("name") String name,
                                     @Param("category") String category,
                                     @Param("minCalories") BigDecimal minCalories,
                                     @Param("maxCalories") BigDecimal maxCalories,
                                     @Param("minProtein") BigDecimal minProtein,
                                     @Param("maxCarbs") BigDecimal maxCarbs,
                                     @Param("maxFat") BigDecimal maxFat,
                                     @Param("minFiber") BigDecimal minFiber,
                                     @Param("maxSodium") BigDecimal maxSodium,
                                     @Param("barcode") String barcode,
                                     Pageable pageable);


    @Query("SELECT COUNT(f) FROM Food f WHERE f.active = true")
    long countActiveFoods();

    @Query("SELECT f.category, COUNT(f) FROM Food f WHERE f.active = true GROUP BY f.category ORDER BY COUNT(f) DESC")
    List<Object[]> countFoodsByCategory();


}