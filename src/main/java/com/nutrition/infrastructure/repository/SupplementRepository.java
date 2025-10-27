package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.food.Supplement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplementRepository extends JpaRepository<Supplement, Long> {

    Page<Supplement> findByActiveTrueOrderByNameAsc(Pageable pageable);

    Optional<Supplement> findByIdAndActiveTrue(Long id);

    @Query(value = "SELECT s.* FROM supplements s WHERE s.active = true AND " +
            "(remove_accents(LOWER(s.name)) LIKE remove_accents(LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "remove_accents(LOWER(s.description)) LIKE remove_accents(LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "remove_accents(LOWER(s.brand)) LIKE remove_accents(LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "remove_accents(LOWER(s.main_ingredient)) LIKE remove_accents(LOWER(CONCAT('%', :searchTerm, '%'))))",
            nativeQuery = true)
    Page<Supplement> searchByNameDescriptionBrandOrIngredient(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<Supplement> findByCategoryAndActiveTrueOrderByNameAsc(Supplement.SupplementCategory category, Pageable pageable);

    @Query("SELECT s.category, COUNT(s) FROM Supplement s WHERE s.active = true GROUP BY s.category ORDER BY COUNT(s) DESC")
    List<Object[]> countSupplementsByCategory();

    Page<Supplement> findByFormAndActiveTrueOrderByNameAsc(Supplement.SupplementForm form, Pageable pageable);

    @Query(value = "SELECT s.* FROM supplements s WHERE s.active = true AND " +
            "(:name IS NULL OR remove_accents(LOWER(s.name)) LIKE remove_accents(LOWER(CONCAT('%', :name, '%')))) AND " +
            "(:category IS NULL OR s.category = CAST(:category AS text)) AND " +
            "(:form IS NULL OR s.form = CAST(:form AS text)) AND " +
            "(:brand IS NULL OR remove_accents(LOWER(s.brand)) LIKE remove_accents(LOWER(CONCAT('%', :brand, '%')))) AND " +
            "(:ingredient IS NULL OR remove_accents(LOWER(s.main_ingredient)) LIKE remove_accents(LOWER(CONCAT('%', :ingredient, '%')))) AND " +
            "(:servingUnit IS NULL OR s.serving_unit = CAST(:servingUnit AS text)) AND " +
            "(:hasNutritionalValue IS NULL OR " +
            "(:hasNutritionalValue = false) OR " +
            "(:hasNutritionalValue = true AND s.calories_per_serving IS NOT NULL AND s.calories_per_serving > 0)) " +
            "ORDER BY s.name ASC", nativeQuery = true)
    Page<Supplement> findByAdvancedFilters(@Param("name") String name,
                                           @Param("category") String category,
                                           @Param("form") String form,
                                           @Param("brand") String brand,
                                           @Param("ingredient") String ingredient,
                                           @Param("servingUnit") String servingUnit,
                                           @Param("hasNutritionalValue") Boolean hasNutritionalValue,
                                           Pageable pageable);


    @Query("SELECT COUNT(s) FROM Supplement s WHERE s.active = true")
    long countActiveSupplements();

}