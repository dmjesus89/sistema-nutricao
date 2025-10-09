package com.nutrition.domain.entity.meal;

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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "meal_time", nullable = false)
    private LocalTime mealTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<MealFood> foods = new ArrayList<>();

    // Template and meal type fields
    @Column(name = "is_template", nullable = false)
    @Builder.Default
    private Boolean isTemplate = true;

    @Column(name = "is_one_time", nullable = false)
    @Builder.Default
    private Boolean isOneTime = false;

    // Relationship to meal consumptions
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<MealConsumption> consumptions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Existing utility methods...
    public BigDecimal getTotalCalories() {
        if (foods == null || foods.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return foods.stream()
                .map(MealFood::getTotalCalories)
                .filter(calories -> calories != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCarbs() {
        if (foods == null || foods.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return foods.stream()
                .map(MealFood::getTotalCarbs)
                .filter(carbs -> carbs != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalProtein() {
        if (foods == null || foods.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return foods.stream()
                .map(MealFood::getTotalProtein)
                .filter(protein -> protein != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalFat() {
        if (foods == null || foods.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return foods.stream()
                .map(MealFood::getTotalFat)
                .filter(fat -> fat != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalFiber() {
        if (foods == null || foods.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return foods.stream()
                .map(mealFood -> mealFood.getTotalFiber() != null ? mealFood.getTotalFiber() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalSodium() {
        if (foods == null || foods.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return foods.stream()
                .map(mealFood -> mealFood.getTotalSodium() != null ? mealFood.getTotalSodium() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Existing methods...
    public void addFood(MealFood mealFood) {
        foods.add(mealFood);
        mealFood.setMeal(this);
    }

    public void removeFood(MealFood mealFood) {
        foods.remove(mealFood);
        mealFood.setMeal(null);
    }
}