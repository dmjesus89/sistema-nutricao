package com.nutrition.application.service;

import com.nutrition.domain.entity.config.ActivityLevelConfig;
import com.nutrition.domain.entity.config.GoalConfig;
import com.nutrition.domain.entity.profile.UserProfile;
import com.nutrition.infrastructure.repository.ActivityLevelConfigRepository;
import com.nutrition.infrastructure.repository.GoalConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TotalDailyEnergyExpenditureCalculationService {

    private final ActivityLevelConfigRepository activityLevelConfigRepository;
    private final GoalConfigRepository goalConfigRepository;

    /**
     * Calcula a Taxa Metabólica Basal (BasalMetabolicRate) usando a fórmula de Mifflin-St Jeor
     * Homens: BasalMetabolicRate = 10 × peso(kg) + 6.25 × altura(cm) - 5 × idade + 5
     * Mulheres: BasalMetabolicRate = 10 × peso(kg) + 6.25 × altura(cm) - 5 × idade - 161
     */
    public BigDecimal calculateBasalMetabolicRate(UserProfile profile) {
        if (!isValidForCalculation(profile)) {
            throw new IllegalArgumentException("Dados insuficientes para cálculo do BasalMetabolicRate");
        }

        BigDecimal weight = profile.getCurrentWeight();
        BigDecimal height = profile.getHeight();
        Integer age = profile.getAge();
        UserProfile.Gender gender = profile.getGender();

        // Componentes da fórmula
        BigDecimal weightComponent = weight.multiply(BigDecimal.valueOf(10));
        BigDecimal heightComponent = height.multiply(BigDecimal.valueOf(6.25));
        BigDecimal ageComponent = BigDecimal.valueOf(age).multiply(BigDecimal.valueOf(5));

        BigDecimal basalMetabolicRate = weightComponent.add(heightComponent).subtract(ageComponent);

        // Ajuste por sexo
        if (gender == UserProfile.Gender.MALE) {
            basalMetabolicRate = basalMetabolicRate.add(BigDecimal.valueOf(5));
        } else if (gender == UserProfile.Gender.FEMALE) {
            basalMetabolicRate = basalMetabolicRate.subtract(BigDecimal.valueOf(161));
        } else {
            // Para "Outro", usa média entre homem e mulher
            basalMetabolicRate = basalMetabolicRate.subtract(BigDecimal.valueOf(78)); // (5 + (-161)) / 2
        }

        log.info("BasalMetabolicRate calculated for user {}: {} kcal/day", profile.getUser().getId(), basalMetabolicRate);
        return basalMetabolicRate.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula o TotalDailyEnergyExpenditure (Total Daily Energy Expenditure) multiplicando BasalMetabolicRate pelo fator de atividade
     */
    public BigDecimal calculateTotalDailyEnergyExpenditure(UserProfile profile) {
        BigDecimal basalMetabolicRate = calculateBasalMetabolicRate(profile);

        // Fetch activity multiplier from database configuration
        BigDecimal activityMultiplier = getActivityMultiplierFromConfig(profile.getActivityLevel());

        BigDecimal totalDailyEnergyExpenditure = basalMetabolicRate.multiply(activityMultiplier);

        log.info("TotalDailyEnergyExpenditure calculated for user {}: {} kcal/day (BasalMetabolicRate: {}, Activity: {})",
                profile.getUser().getId(), totalDailyEnergyExpenditure, basalMetabolicRate, activityMultiplier);

        return totalDailyEnergyExpenditure.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Obtém o multiplicador de atividade da configuração do banco de dados
     */
    private BigDecimal getActivityMultiplierFromConfig(UserProfile.ActivityLevel activityLevel) {
        ActivityLevelConfig config = activityLevelConfigRepository
                .findByCodeAndActive(activityLevel.name(), true)
                .orElseThrow(() -> new IllegalStateException(
                        "Activity level configuration not found for: " + activityLevel.name()));
        return config.getMultiplier();
    }

    /**
     * Calcula as calorias diárias baseadas no objetivo e data alvo do usuário
     */
    public BigDecimal calculateDailyCalories(UserProfile profile) {
        BigDecimal totalDailyEnergyExpenditure = calculateTotalDailyEnergyExpenditure(profile);

        // Peso atual e meta
        BigDecimal currentWeight = profile.getCurrentWeight();
        BigDecimal targetWeight = profile.getTargetWeight();

        // Data alvo
        LocalDate targetDate = profile.getTargetDate();
        LocalDate today = LocalDate.now();
        long daysToTarget = ChronoUnit.DAYS.between(today, targetDate);

        // Se não há prazo válido, retorna TDEE sem ajuste
        if (daysToTarget <= 0) {
            log.warn("Target date is invalid or already passed for user {}. Using TDEE without adjustment.",
                    profile.getUser().getId());
            return totalDailyEnergyExpenditure.setScale(0, RoundingMode.HALF_UP);
        }

        // Diferença de peso (kg)
        BigDecimal weightDiff = currentWeight.subtract(targetWeight);

        // Calorias totais necessárias (≈ 7700 kcal por kg)
        BigDecimal caloriesToLose = weightDiff.multiply(BigDecimal.valueOf(7700));

        // Ajuste diário (negativo se perder peso, positivo se ganhar)
        BigDecimal dailyAdjustment = caloriesToLose.divide(BigDecimal.valueOf(daysToTarget), RoundingMode.HALF_UP);

        // Cálculo final
        BigDecimal dailyCalories = totalDailyEnergyExpenditure.subtract(dailyAdjustment);

        log.info("Daily calories calculated for user {}: {} kcal/day (TDEE: {}, Adjustment: {})",
                profile.getUser().getId(), dailyCalories, totalDailyEnergyExpenditure, dailyAdjustment.negate());

        return dailyCalories.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Calcula a necessidade de água diária (ml) baseada no peso
     * Fórmula: 35ml por kg de peso corporal
     */
    public BigDecimal calculateDailyWaterIntake(UserProfile profile) {
        if (profile.getCurrentWeight() == null) {
            return BigDecimal.valueOf(2000); // padrão de 2L
        }

        BigDecimal dailyWaterIntake = profile.getCurrentWeight().multiply(BigDecimal.valueOf(35));

        // Mínimo de 1.5L, máximo de 4L
        if (dailyWaterIntake.compareTo(BigDecimal.valueOf(1500)) < 0) {
            dailyWaterIntake = BigDecimal.valueOf(1500);
        } else if (dailyWaterIntake.compareTo(BigDecimal.valueOf(4000)) > 0) {
            dailyWaterIntake = BigDecimal.valueOf(4000);
        }

        log.info("Daily water intake calculated for user {}: {} ml", profile.getUser().getId(), dailyWaterIntake);
        return dailyWaterIntake.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Atualiza todos os cálculos metabólicos do perfil
     */
    public void updateMetabolicCalculations(UserProfile profile) {
        try {
            profile.setBasalMetabolicRate(calculateBasalMetabolicRate(profile));
            profile.setTotalDailyEnergyExpenditure(calculateTotalDailyEnergyExpenditure(profile));
            profile.setDailyCalorieTarget(calculateDailyCalories(profile));
            profile.setDailyWaterIntake(calculateDailyWaterIntake(profile));

            log.info("Metabolic calculations updated for user {}: BasalMetabolicRate={}, TotalDailyEnergyExpenditure={}, Daily Calories={}, Water Intake={}",
                    profile.getUser().getId(), profile.getBasalMetabolicRate(), profile.getTotalDailyEnergyExpenditure(),
                    profile.getDailyCalorieTarget(), profile.getDailyWaterIntake());
        } catch (Exception e) {
            log.error("Error updating metabolic calculations for user {}: {}",
                    profile.getUser().getId(), e.getMessage());
            throw new RuntimeException("Erro ao calcular métricas metabólicas", e);
        }
    }

    /**
     * Calcula quantas semanas levará para atingir o peso alvo
     */
    public Integer calculateWeeksToGoal(UserProfile profile) {
        if (profile.getCurrentWeight() == null || profile.getTargetWeight() == null) {
            return null;
        }

        BigDecimal weightDifference = profile.getTargetWeight().subtract(profile.getCurrentWeight()).abs();

        // Get calorie adjustment from the profile's calculated value, not from the goal enum
        BigDecimal calorieAdjustmentDecimal = profile.getDailyCalorieTarget() != null && profile.getTotalDailyEnergyExpenditure() != null
                ? profile.getTotalDailyEnergyExpenditure().subtract(profile.getDailyCalorieTarget()).abs()
                : BigDecimal.ZERO;

        int calorieAdjustment = calorieAdjustmentDecimal.intValue();

        if (calorieAdjustment == 0) {
            return null; // Mantendo peso, não há tempo estimado
        }

        // Assumindo que 7000 kcal = 1 kg (aproximadamente)
        // Peso a perder/ganhar em kg × 7000 kcal/kg ÷ déficit/superávit diário ÷ 7 dias
        BigDecimal totalCalories = weightDifference.multiply(BigDecimal.valueOf(7000));
        BigDecimal weeksDecimal = totalCalories.divide(BigDecimal.valueOf(calorieAdjustment * 7), 1, RoundingMode.HALF_UP);

        return weeksDecimal.intValue();
    }

    /**
     * Obtém o ajuste calórico da configuração do banco de dados
     */
    private GoalConfig getGoalConfigFromDatabase(UserProfile.Goal goal) {
        return goalConfigRepository
                .findByCodeAndActive(goal.name(), true)
                .orElseThrow(() -> new IllegalStateException(
                        "Goal configuration not found for: " + goal.name()));
    }

    /**
     * Verifica se os dados são suficientes para cálculos
     */
    private boolean isValidForCalculation(UserProfile profile) {
        return profile.getCurrentWeight() != null &&
                profile.getHeight() != null &&
                profile.getBirthDate() != null &&
                profile.getGender() != null &&
                profile.getActivityLevel() != null &&
                profile.getCurrentWeight().compareTo(BigDecimal.ZERO) > 0 &&
                profile.getHeight().compareTo(BigDecimal.ZERO) > 0 &&
                profile.getAge() != null && profile.getAge() > 0;
    }


}