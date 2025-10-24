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
import lombok.Setter;

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
    private BigDecimal height; // in centimeters

    @Column(name = "current_weight", precision = 5, scale = 2)
    private BigDecimal currentWeight; // in kilograms

    @Column(name = "target_weight", precision = 5, scale = 2)
    private BigDecimal targetWeight; // in kilograms

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

    @Column(name = "basal_metabolic_rate", precision = 8, scale = 2)
    private BigDecimal basalMetabolicRate; // Basal Metabolic Rate

    @Column(name = "total_daily_energy_expenditure", precision = 8, scale = 2)
    private BigDecimal totalDailyEnergyExpenditure; // Total Daily Energy Expenditure

    @Column(name = "daily_calories", precision = 8, scale = 2)
    private BigDecimal dailyCalorieTarget; // Daily calories based on goal

    @Column(name = "daily_water_intake", precision = 8, scale = 2)
    private BigDecimal dailyWaterIntake;

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

        // BMI = weight (kg) / (height (m))²
        BigDecimal heightInMeters = height.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal heightSquared = heightInMeters.multiply(heightInMeters);

        return currentWeight.divide(heightSquared, 2, BigDecimal.ROUND_HALF_UP);
    }

    public String getBodyMassIndexCategory() {
        BigDecimal bodyMassIndex = getBodyMassIndex();
        if (bodyMassIndex == null) {
            return "Not calculated";
        }

        if (bodyMassIndex.compareTo(BigDecimal.valueOf(18.5)) < 0) {
            return "Underweight";
        } else if (bodyMassIndex.compareTo(BigDecimal.valueOf(25)) < 0) {
            return "Normal weight";
        } else if (bodyMassIndex.compareTo(BigDecimal.valueOf(30)) < 0) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    public enum Gender {
        MALE("Male"),
        FEMALE("Female"),
        OTHER("Other");

        private final String displayName;

        Gender(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ActivityLevel {
        SEDENTARY("Sedentary", 1.2),           // Seated work, minimal movement, very still routine
        LIGHTLY_ACTIVE("Lightly Active", 1.375), // Seated work but moves throughout the day (walks a lot, shops, takes care of home, uses public transport, etc.)
        MODERATELY_ACTIVE("Moderately Active", 1.55), // Routine with frequent movement + some recreational physical activities (e.g., walks daily + long walks, but no structured training)
        VERY_ACTIVE("Very Active", 1.725),        // Physically demanding work (bricklayer, waiter walking 10h, bike delivery, moving helper, etc.)
        EXTREMELY_ACTIVE("Extremely Active", 1.9); // Heavy load rural workers, athletes, military, people who spend energy intensively daily

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
        LOSE_WEIGHT("Lose Fat/Maintain Lean Muscle"),
        MAINTAIN_WEIGHT("Maintain Weight"),
        GAIN_WEIGHT("Gain Weight");

        private final String displayName;
        @Setter
        private BigDecimal calorieAdjustment; // Calories to add/subtract from TDEE

        Goal(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public BigDecimal getCalorieAdjustment() {
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
     * Calculates required weekly weight change
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
     * Checks if the target date is still viable
     */
    public boolean isTargetDateRealistic() {
        if (targetDate == null || currentWeight == null || targetWeight == null) {
            return true; // Cannot determine, assumes it is realistic
        }

        BigDecimal weeklyChange = getRecommendedWeeklyWeightChange();
        if (weeklyChange == null) {
            return true;
        }

        // Considers realistic if weekly change is <= 1kg
        return weeklyChange.abs().compareTo(BigDecimal.valueOf(1.0)) <= 0;
    }
}