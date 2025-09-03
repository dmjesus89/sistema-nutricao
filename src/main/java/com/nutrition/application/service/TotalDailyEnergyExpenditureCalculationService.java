package com.nutrition.application.service;

import com.nutrition.domain.entity.profile.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class TotalDailyEnergyExpenditureCalculationService {

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
        double activityMultiplier = profile.getActivityLevel().getMultiplier();

        BigDecimal totalDailyEnergyExpenditure = basalMetabolicRate.multiply(BigDecimal.valueOf(activityMultiplier));

        log.info("TotalDailyEnergyExpenditure calculated for user {}: {} kcal/day (BasalMetabolicRate: {}, Activity: {})",
                profile.getUser().getId(), totalDailyEnergyExpenditure, basalMetabolicRate, activityMultiplier);

        return totalDailyEnergyExpenditure.setScale(2, RoundingMode.HALF_UP);
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
     * Atualiza todos os cálculos metabólicos do perfil
     */
    public void updateMetabolicCalculations(UserProfile profile) {
        try {

            profile.setBasalMetabolicRate(calculateBasalMetabolicRate(profile));
            profile.setTotalDailyEnergyExpenditure(calculateTotalDailyEnergyExpenditure(profile));
            profile.setDailyCalorieTarget(calculateDailyCalories(profile));

            profile.setBasalMetabolicRate(profile.getBasalMetabolicRate());
            profile.setTotalDailyEnergyExpenditure(profile.getTotalDailyEnergyExpenditure());
            profile.setDailyCalorieTarget(profile.getDailyCalorieTarget());

            log.info("Metabolic calculations updated for user {}: BasalMetabolicRate={}, TotalDailyEnergyExpenditure={}, Daily Calories={}", profile.getUser().getId(), profile.getBasalMetabolicRate(), profile.getTotalDailyEnergyExpenditure(), profile.getDailyCalorieTarget());
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
        int calorieAdjustment = Math.abs(profile.getGoal().getCalorieAdjustment());

        if (calorieAdjustment == 0) {
            return null; // Mantendo peso, não há tempo estimado
        }

        // Assumindo que 7000 kcal = 1 kg (aproximadamente)
        // Peso a perder/ganhar em kg × 7000 kcal/kg ÷ déficit/superávit diário ÷ 7 dias
        BigDecimal totalCalories = weightDifference.multiply(BigDecimal.valueOf(7000));
        BigDecimal weeksDecimal = totalCalories.divide(
                BigDecimal.valueOf(calorieAdjustment * 7), 1, RoundingMode.HALF_UP);

        return weeksDecimal.intValue();
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

    /**
     * Retorna o mínimo de calorias seguro por sexo
     */
    private BigDecimal getMinimumCalories(UserProfile.Gender gender) {
        return switch (gender) {
            case MALE -> BigDecimal.valueOf(1500);
            case FEMALE -> BigDecimal.valueOf(1200);
            case OTHER -> BigDecimal.valueOf(1350); // média
        };
    }

    /**
     * Calcula a necessidade de água diária (ml) baseada no peso
     * Fórmula: 35ml por kg de peso corporal
     */
    public BigDecimal calculateDailyWaterIntake(UserProfile profile) {
        if (profile.getCurrentWeight() == null) {
            return BigDecimal.valueOf(2000); // padrão de 2L
        }

        BigDecimal waterIntake = profile.getCurrentWeight().multiply(BigDecimal.valueOf(35));

        // Mínimo de 1.5L, máximo de 4L
        if (waterIntake.compareTo(BigDecimal.valueOf(1500)) < 0) {
            waterIntake = BigDecimal.valueOf(1500);
        } else if (waterIntake.compareTo(BigDecimal.valueOf(4000)) > 0) {
            waterIntake = BigDecimal.valueOf(4000);
        }

        return waterIntake.setScale(0, RoundingMode.HALF_UP);
    }
}