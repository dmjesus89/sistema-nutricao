package com.nutrition.domain.entity.profile;

import com.nutrition.domain.entity.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "height", precision = 5, scale = 2)
    private BigDecimal height; // em centímetros

    @Column(name = "current_weight", precision = 5, scale = 2)
    private BigDecimal currentWeight; // em quilogramas

    @Column(name = "target_weight", precision = 5, scale = 2)
    private BigDecimal targetWeight; // em quilogramas

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false)
    @Builder.Default
    private ActivityLevel activityLevel = ActivityLevel.SEDENTARY;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal", nullable = false)
    @Builder.Default
    private Goal goal = Goal.MAINTAIN_WEIGHT;

    @Column(name = "basal_metabolic_rate", precision = 7, scale = 2)
    private BigDecimal basalMetabolicRate; // Taxa Metabólica Basal

    @Column(name = "total_daily_energy_expenditure", precision = 7, scale = 2)
    private BigDecimal totalDailyEnergyExpenditure; // Total Daily Energy Expenditure

    @Column(name = "daily_calories", precision = 7, scale = 2)
    private BigDecimal dailyCalorieTarget; // Calorias diárias baseadas no objetivo

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    public BigDecimal getBodyMassIndex() {
        if (currentWeight == null || height == null ||
                currentWeight.compareTo(BigDecimal.ZERO) <= 0 ||
                height.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        // BodyMassIndex = peso (kg) / (altura (m))²
        BigDecimal heightInMeters = height.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal heightSquared = heightInMeters.multiply(heightInMeters);

        return currentWeight.divide(heightSquared, 2, BigDecimal.ROUND_HALF_UP);
    }

    public String getBodyMassIndexCategory() {
        BigDecimal bodyMassIndex = getBodyMassIndex();
        if (bodyMassIndex == null) {
            return "Não calculado";
        }

        if (bodyMassIndex.compareTo(BigDecimal.valueOf(18.5)) < 0) {
            return "Abaixo do peso";
        } else if (bodyMassIndex.compareTo(BigDecimal.valueOf(25)) < 0) {
            return "Peso normal";
        } else if (bodyMassIndex.compareTo(BigDecimal.valueOf(30)) < 0) {
            return "Sobrepeso";
        } else {
            return "Obesidade";
        }
    }

    public enum Gender {
        MALE("Masculino"),
        FEMALE("Feminino"),
        OTHER("Outro");

        private final String displayName;

        Gender(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ActivityLevel {
        SEDENTARY("Sedentário", 1.2),           // trabalha sentado, deslocamento mínimo, rotina bem parada.
        LIGHTLY_ACTIVE("Levemente ativo", 1.375), // trabalha sentado mas se movimenta no dia (anda bastante, faz compras, cuida da casa, pega transporte público etc.).
        MODERATELY_ACTIVE("Moderadamente ativo", 1.55), // rotina com movimento frequente + algumas atividades físicas recreativas (ex: anda muito todo dia + faz caminhadas longas, mas sem treino estruturado).
        VERY_ACTIVE("Muito ativo", 1.725),        // trabalho fisicamente exigente (pedreiro, garçom andando 10h, entregador de bike, ajudante de mudanças, etc.)
        EXTREMELY_ACTIVE("Extremamente ativo", 1.9); // trabalhadores rurais de carga pesada, atletas, militares, pessoas que gastam energia de forma intensa diária.

        private final String displayName;
        private final double multiplier;

        ActivityLevel(String displayName, double multiplier) {
            this.displayName = displayName;
            this.multiplier = multiplier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }

    public enum Goal {
        LOSE_WEIGHT("Perder peso", -500),
        MAINTAIN_WEIGHT("Manter peso", 0),
        GAIN_WEIGHT("Ganhar peso", 500);

        private final String displayName;
        private final int calorieAdjustment; // calorias a adicionar/subtrair do TotalDailyEnergyExpenditure

        Goal(String displayName, int calorieAdjustment) {
            this.displayName = displayName;
            this.calorieAdjustment = calorieAdjustment;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getCalorieAdjustment() {
            return calorieAdjustment;
        }
    }


    public Integer getDaysToTarget() {
        if (targetDate == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        if (targetDate.isBefore(today) || targetDate.isEqual(today)) {
            return 0;
        }

        return today.until(targetDate).getDays();
    }

    /**
     * Calcula mudança de peso semanal necessária
     */
    public BigDecimal getRecommendedWeeklyWeightChange() {
        if (targetDate == null || currentWeight == null || targetWeight == null) {
            return null;
        }

        Integer daysToTarget = getDaysToTarget();
        if (daysToTarget == null || daysToTarget <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal weightDifference = targetWeight.subtract(currentWeight);
        double weeksToTarget = daysToTarget / 7.0;

        return weightDifference.divide(BigDecimal.valueOf(weeksToTarget), 3, RoundingMode.HALF_UP);
    }

    /**
     * Verifica se o objetivo ainda é viável
     */
    public boolean isTargetDateRealistic() {
        if (targetDate == null || currentWeight == null || targetWeight == null) {
            return true; // Não pode determinar, assume que é realista
        }

        BigDecimal weeklyChange = getRecommendedWeeklyWeightChange();
        if (weeklyChange == null) {
            return true;
        }

        // Considera realista se a mudança semanal for <= 1kg
        return weeklyChange.abs().compareTo(BigDecimal.valueOf(1.0)) <= 0;
    }
}