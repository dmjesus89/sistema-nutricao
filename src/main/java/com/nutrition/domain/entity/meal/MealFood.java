package com.nutrition.domain.entity.meal;

import com.nutrition.domain.entity.food.Food;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "meal_foods")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class MealFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(name = "quantity", nullable = false, precision = 8, scale = 2)
    private BigDecimal quantity; // quantidade em gramas ou na unidade especificada

    @Column(name = "unit", length = 20)
    private String unit; // "g", "ml", "porção", etc.

    // Métodos para calcular valores nutricionais baseados na quantidade
    public BigDecimal getTotalCalories() {
        if (food == null || quantity == null) return BigDecimal.ZERO;

        BigDecimal multiplier = getQuantityMultiplier();
        return food.getCaloriesPer100g()
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalCarbs() {
        if (food == null || quantity == null) return BigDecimal.ZERO;

        BigDecimal multiplier = getQuantityMultiplier();
        return food.getCarbsPer100g()
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalProtein() {
        if (food == null || quantity == null) return BigDecimal.ZERO;

        BigDecimal multiplier = getQuantityMultiplier();
        return food.getProteinPer100g()
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalFat() {
        if (food == null || quantity == null) return BigDecimal.ZERO;

        BigDecimal multiplier = getQuantityMultiplier();
        return food.getFatPer100g()
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalFiber() {
        if (food == null || quantity == null || food.getFiberPer100g() == null) return BigDecimal.ZERO;

        BigDecimal multiplier = getQuantityMultiplier();
        return food.getFiberPer100g()
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalSodium() {
        if (food == null || quantity == null || food.getSodiumPer100g() == null) return BigDecimal.ZERO;

        BigDecimal multiplier = getQuantityMultiplier();
        return food.getSodiumPer100g()
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalSugar() {
        if (food == null || quantity == null || food.getSugarPer100g() == null) return BigDecimal.ZERO;

        BigDecimal multiplier = getQuantityMultiplier();
        return food.getSugarPer100g()
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalSaturatedFat() {
        if (food == null || quantity == null || food.getSaturatedFatPer100g() == null) return BigDecimal.ZERO;

        BigDecimal multiplier = getQuantityMultiplier();
        return food.getSaturatedFatPer100g()
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula o multiplicador baseado na quantidade e unidade
     * Se a unidade é "porção" e o alimento tem servingSize, usa essa referência
     * Caso contrário, assume que a quantidade é em gramas
     */
    private BigDecimal getQuantityMultiplier() {
        if ("porção".equals(unit) || "serving".equals(unit)) {
            if (food.getServingSize() != null && food.getServingSize().compareTo(BigDecimal.ZERO) > 0) {
                // quantidade * servingSize / 100
                return quantity.multiply(food.getServingSize()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            }
        }

        // Assume quantidade em gramas: quantidade / 100
        return quantity.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

    // Getter para quantidade como Double para compatibilidade com DTOs
    public Double getQuantityAsDouble() {
        return quantity != null ? quantity.doubleValue() : null;
    }

    // Setter para quantidade como Double
    public void setQuantityAsDouble(Double quantity) {
        this.quantity = quantity != null ? BigDecimal.valueOf(quantity) : null;
    }
}