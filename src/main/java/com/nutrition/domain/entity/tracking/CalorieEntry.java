package com.nutrition.domain.entity.tracking;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.Food;
import com.nutrition.domain.entity.meal.Meal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "calorie_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalorieEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false)
    private EntryType entryType;

    // Calorias registradas
    @Column(name = "calories", nullable = false, precision = 8, scale = 2)
    private BigDecimal calories;

    @Column(name = "carbs", precision = 6, scale = 2)
    private BigDecimal carbs;

    @Column(name = "protein", precision = 6, scale = 2)
    private BigDecimal protein;

    @Column(name = "fat", precision = 6, scale = 2)
    private BigDecimal fat;

    // Referências opcionais
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    // Detalhes da entrada
    @Column(name = "quantity_grams", precision = 8, scale = 2)
    private BigDecimal quantityGrams;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum EntryType {
        MANUAL("Entrada manual de calorias"),
        FOOD("Alimento específico"),
        MEAL("Refeição completa");

        private final String description;

        EntryType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}