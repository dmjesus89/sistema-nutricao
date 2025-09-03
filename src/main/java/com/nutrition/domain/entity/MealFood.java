
package com.nutrition.domain.entity;

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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_foods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "quantity_grams", nullable = false, precision = 8, scale = 2)
    private BigDecimal quantityGrams;

    @Column(name = "serving_description", length = 100)
    private String servingDescription;

    // Pre-calculated nutrition values for this quantity
    @Column(name = "calculated_calories", nullable = false, precision = 8, scale = 2)
    private BigDecimal calculatedCalories;

    @Column(name = "calculated_carbs", nullable = false, precision = 8, scale = 2)
    private BigDecimal calculatedCarbs;

    @Column(name = "calculated_protein", nullable = false, precision = 8, scale = 2)
    private BigDecimal calculatedProtein;

    @Column(name = "calculated_fat", nullable = false, precision = 8, scale = 2)
    private BigDecimal calculatedFat;

    // Consumption tracking
    @Column(name = "is_consumed", nullable = false)
    @Builder.Default
    private Boolean isConsumed = false;

    @Column(name = "consumed_quantity", precision = 8, scale = 2)
    private BigDecimal consumedQuantity;

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper methods
    public void consume(BigDecimal actualQuantity) {
        this.isConsumed = true;
        this.consumedQuantity = actualQuantity != null ? actualQuantity : this.quantityGrams;
        this.consumedAt = LocalDateTime.now();
    }

    public BigDecimal getActualCalories() {
        if (!isConsumed || consumedQuantity == null) return BigDecimal.ZERO;
        return calculatedCalories.multiply(consumedQuantity)
                .divide(quantityGrams, 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getActualCarbs() {
        if (!isConsumed || consumedQuantity == null) return BigDecimal.ZERO;
        return calculatedCarbs.multiply(consumedQuantity)
                .divide(quantityGrams, 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getActualProtein() {
        if (!isConsumed || consumedQuantity == null) return BigDecimal.ZERO;
        return calculatedProtein.multiply(consumedQuantity)
                .divide(quantityGrams, 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getActualFat() {
        if (!isConsumed || consumedQuantity == null) return BigDecimal.ZERO;
        return calculatedFat.multiply(consumedQuantity)
                .divide(quantityGrams, 2, java.math.RoundingMode.HALF_UP);
    }
}
