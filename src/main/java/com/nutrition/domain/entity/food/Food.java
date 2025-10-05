package com.nutrition.domain.entity.food;

import com.nutrition.domain.entity.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "foods")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "brand", length = 100)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private FoodCategory category;

    @Column(name = "barcode", unique = true, length = 50)
    private String barcode;

    // Informações nutricionais por 100g
    @Column(name = "calories_per_100g", nullable = false, precision = 8, scale = 2)
    private BigDecimal caloriesPer100g;

    @Column(name = "carbs_per_100g", nullable = false, precision = 6, scale = 2)
    private BigDecimal carbsPer100g; // em gramas

    @Column(name = "protein_per_100g", nullable = false, precision = 6, scale = 2)
    private BigDecimal proteinPer100g; // em gramas

    @Column(name = "fat_per_100g", nullable = false, precision = 6, scale = 2)
    private BigDecimal fatPer100g; // em gramas

    @Column(name = "fiber_per_100g", precision = 6, scale = 2)
    private BigDecimal fiberPer100g; // em gramas

    @Column(name = "sugar_per_100g", precision = 6, scale = 2)
    private BigDecimal sugarPer100g; // em gramas

    @Column(name = "sodium_per_100g", precision = 8, scale = 2)
    private BigDecimal sodiumPer100g; // em mg

    @Column(name = "saturated_fat_per_100g", precision = 6, scale = 2)
    private BigDecimal saturatedFatPer100g; // em gramas

    @Column(name = "quantity_equivalence")
    private String quantityEquivalence;

    // Informações de porção
    @Column(name = "serving_size", precision = 6, scale = 2)
    private BigDecimal servingSize; // em gramas

    @Column(name = "serving_description", length = 100)
    private String servingDescription; // ex: "1 fatia", "1 xícara"

    // MISSING FIELD - serving unit
    @Column(name = "serving_unit", length = 20)
    private String servingUnit; // ex: "g", "ml", "unidade"

    // Informações de fonte e verificação
    @Column(name = "source", length = 100)
    private String source; // USDA, TACO, Manual, etc.

    @Column(name = "verified", nullable = false)
    @Builder.Default
    private Boolean verified = false; // FIXED: renamed from 'verified'

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true; // FIXED: renamed from 'active'

    // Metadados de criação
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relacionamentos
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserFoodPreference> userPreferences = new ArrayList<>();

    // Métodos utilitários
    public BigDecimal getCaloriesPerServing() {
        if (servingSize == null || servingSize.compareTo(BigDecimal.ZERO) <= 0) {
            return caloriesPer100g;
        }
        return caloriesPer100g.multiply(servingSize).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getCarbsPerServing() {
        if (servingSize == null || servingSize.compareTo(BigDecimal.ZERO) <= 0) {
            return carbsPer100g;
        }
        return carbsPer100g.multiply(servingSize).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getProteinPerServing() {
        if (servingSize == null || servingSize.compareTo(BigDecimal.ZERO) <= 0) {
            return proteinPer100g;
        }
        return proteinPer100g.multiply(servingSize).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getFatPerServing() {
        if (servingSize == null || servingSize.compareTo(BigDecimal.ZERO) <= 0) {
            return fatPer100g;
        }
        return fatPer100g.multiply(servingSize).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    public String getDisplayName() {
        if (brand != null && !brand.trim().isEmpty()) {
            return brand + " " + name;
        }
        return name;
    }

    public boolean isHighProtein() {
        return proteinPer100g.compareTo(BigDecimal.valueOf(20)) >= 0;
    }

    public boolean isHighFiber() {
        return fiberPer100g != null && fiberPer100g.compareTo(BigDecimal.valueOf(6)) >= 0;
    }

    public boolean isLowSodium() {
        return sodiumPer100g != null && sodiumPer100g.compareTo(BigDecimal.valueOf(140)) <= 0;
    }

    // MISSING GETTERS - adding the ones referenced in the controller
    public String getServingUnit() {
        return servingUnit;
    }

    public Boolean getIsVerified() {
        return verified;
    }

    public Boolean getIsActive() {
        return active;
    }

    public enum FoodCategory {
        CEREALS_GRAINS("Cereais e Grãos"),
        VEGETABLES("Vegetais e Legumes"),
        FRUITS("Frutas"),
        PROTEINS("Proteínas"),
        DAIRY("Laticínios"),
        FATS_OILS("Gorduras e Óleos"),
        BEVERAGES("Bebidas"),
        SWEETS_DESSERTS("Doces e Sobremesas"),
        SNACKS("Lanches e Petiscos"),
        CONDIMENTS_SPICES("Condimentos e Temperos"),
        PREPARED_FOODS("Alimentos Preparados"),
        SUPPLEMENTS("Suplementos"),
        NUTS_SEEDS("Castanhas e Sementes"), // Added missing category
        OTHER("Outros");

        private final String displayName;

        FoodCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}