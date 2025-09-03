package com.nutrition.domain.entity.food;

import com.nutrition.domain.entity.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "supplements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplement {

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
    private SupplementCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "form", nullable = false)
    private SupplementForm form;

    // Informações da dosagem
    @Column(name = "serving_size", nullable = false, precision = 8, scale = 2)
    private BigDecimal servingSize; // Quantidade da porção

    @Enumerated(EnumType.STRING)
    @Column(name = "serving_unit", nullable = false)
    private ServingUnit servingUnit;

    @Column(name = "servings_per_container", precision = 6, scale = 0)
    private BigDecimal servingsPerContainer;

    // Informações nutricionais por porção
    @Column(name = "calories_per_serving", precision = 6, scale = 2)
    private BigDecimal caloriesPerServing;

    @Column(name = "carbs_per_serving", precision = 6, scale = 2)
    private BigDecimal carbsPerServing;

    @Column(name = "protein_per_serving", precision = 6, scale = 2)
    private BigDecimal proteinPerServing;

    @Column(name = "fat_per_serving", precision = 6, scale = 2)
    private BigDecimal fatPerServing;

    // Ingredientes ativos principais
    @Column(name = "main_ingredient", length = 200)
    private String mainIngredient;

    @Column(name = "ingredient_amount", precision = 10, scale = 2)
    private BigDecimal ingredientAmount;

    @Column(name = "ingredient_unit", length = 20)
    private String ingredientUnit; // mg, mcg, IU, etc.

    // Instruções de uso
    @Column(name = "recommended_dosage", length = 500)
    private String recommendedDosage;

    @Column(name = "usage_instructions", length = 1000)
    private String usageInstructions;

    @Column(name = "warnings", length = 1000)
    private String warnings;

    // Informações regulatórias
    @Column(name = "regulatory_info", length = 500)
    private String regulatoryInfo; // Anvisa, FDA, etc.

    @Column(name = "verified", nullable = false)
    @Builder.Default
    private Boolean verified = false;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    // Metadados
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Relacionamentos
    @OneToMany(mappedBy = "supplement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserSupplementPreference> userPreferences = new ArrayList<>();

    // Métodos utilitários
    public String getDisplayName() {
        if (brand != null && !brand.trim().isEmpty()) {
            return brand + " " + name;
        }
        return name;
    }

    public String getServingSizeDescription() {
        return servingSize + " " + servingUnit.getDisplayName();
    }

    public String getMainIngredientDescription() {
        if (mainIngredient != null && ingredientAmount != null && ingredientUnit != null) {
            return mainIngredient + " " + ingredientAmount + ingredientUnit;
        }
        return mainIngredient;
    }

    public boolean hasNutritionalValue() {
        return caloriesPerServing != null && caloriesPerServing.compareTo(BigDecimal.ZERO) > 0;
    }

    public enum SupplementCategory {
        PROTEIN("Proteínas"),
        VITAMINS("Vitaminas"),
        MINERALS("Minerais"),
        AMINO_ACIDS("Aminoácidos"),
        CREATINE("Creatina"),
        PRE_WORKOUT("Pré-treino"),
        POST_WORKOUT("Pós-treino"),
        WEIGHT_LOSS("Emagrecedores"),
        WEIGHT_GAIN("Hipercalóricos"),
        OMEGA3("Ômega 3"),
        PROBIOTICS("Probióticos"),
        ENERGY("Energéticos"),
        JOINT_HEALTH("Saúde Articular"),
        IMMUNE_SUPPORT("Suporte Imunológico"),
        OTHER("Outros");

        private final String displayName;

        SupplementCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum SupplementForm {
        CAPSULE("Cápsula"),
        TABLET("Comprimido"),
        POWDER("Pó"),
        LIQUID("Líquido"),
        GUMMY("Goma"),
        CHEWABLE("Mastigável"),
        SOFTGEL("Softgel"),
        SPRAY("Spray"),
        DROPS("Gotas"),
        OTHER("Outro");

        private final String displayName;

        SupplementForm(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ServingUnit {
        GRAMS("g", "gramas"),
        MILLIGRAMS("mg", "miligramas"),
        MICROGRAMS("mcg", "microgramas"),
        CAPSULES("cáps", "cápsulas"),
        TABLETS("comp", "comprimidos"),
        SCOOPS("scoop", "scoops"),
        TEASPOON("col. chá", "colher de chá"),
        TABLESPOON("col. sopa", "colher de sopa"),
        MILLILITERS("ml", "mililitros"),
        UNITS("un", "unidades"),
        IU("UI", "unidades internacionais");

        private final String abbreviation;
        private final String displayName;

        ServingUnit(String abbreviation, String displayName) {
            this.abbreviation = abbreviation;
            this.displayName = displayName;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}