package com.nutrition.application.service;

import com.nutrition.domain.entity.profile.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProfileValidationService {

    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 120;
    private static final BigDecimal MIN_HEIGHT = BigDecimal.valueOf(100);
    private static final BigDecimal MAX_HEIGHT = BigDecimal.valueOf(250);
    private static final BigDecimal MIN_WEIGHT = BigDecimal.valueOf(30);
    private static final BigDecimal MAX_WEIGHT = BigDecimal.valueOf(300);
    private static final BigDecimal MAX_WEIGHT_DIFFERENCE = BigDecimal.valueOf(50);
    private static final int MAX_YEAR_TARGET = 2;
    private static final int MIN_DAYS_TARGET = 30;
    public static final BigDecimal MIN_HEALTHY_WEEKLY_WEIGHT_CHANGE = BigDecimal.valueOf(0.25);
    public static final BigDecimal MAX_HEALTHY_WEEKLY_WEIGHT_CHANGE = BigDecimal.valueOf(1.0);

    //TODO deixar essas infos em base de dados.

    public ValidationResult validateProfileData(LocalDate birthDate, UserProfile.Gender gender,
                                                BigDecimal height, BigDecimal currentWeight,
                                                BigDecimal targetWeight, LocalDate targetDate) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Validar idade
        if (birthDate != null) {
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            if (age < MIN_AGE) {
                errors.add("Idade deve ser no mínimo " + MIN_AGE + " anos");
            } else if (age > MAX_AGE) {
                errors.add("Idade deve ser no máximo " + MAX_AGE + " anos");
            }
        }

        // Validar altura
        if (height != null) {
            if (height.compareTo(MIN_HEIGHT) < 0 || height.compareTo(MAX_HEIGHT) > 0) {
                errors.add(String.format("Altura deve estar entre %.0f e %.0f cm", MIN_HEIGHT, MAX_HEIGHT));
            }
        }

        // Validar peso atual
        if (currentWeight != null) {
            if (currentWeight.compareTo(MIN_WEIGHT) < 0 || currentWeight.compareTo(MAX_WEIGHT) > 0) {
                errors.add(String.format("Peso atual deve estar entre %.0f e %.0f kg", MIN_WEIGHT, MAX_WEIGHT));
            }
        }

        // Validar peso alvo
        if (targetWeight != null) {
            if (targetWeight.compareTo(MIN_WEIGHT) < 0 || targetWeight.compareTo(MAX_WEIGHT) > 0) {
                errors.add(String.format("Peso alvo deve estar entre %.0f e %.0f kg", MIN_WEIGHT, MAX_WEIGHT));
            }

            // Verificar se a diferença de peso é realista
            if (currentWeight != null) {
                BigDecimal weightDifference = targetWeight.subtract(currentWeight).abs();
                if (weightDifference.compareTo(MAX_WEIGHT_DIFFERENCE) > 0) {
                    warnings.add("Diferença entre peso atual e alvo é muito grande (>" + MAX_WEIGHT_DIFFERENCE + "kg). " + "Considere objetivos intermediários.");
                }
            }
        }

        if (targetDate != null) {
            LocalDate today = LocalDate.now();
            if (!targetDate.isAfter(today)) {
                errors.add("Data alvo deve ser no futuro");
            } else {
                // Verificar se a data alvo é muito distante
                long daysToTarget = today.until(targetDate).getDays();
                if (daysToTarget > 365 * MAX_YEAR_TARGET) {
                    warnings.add("Data alvo muito distante (>" + MAX_YEAR_TARGET + " anos). Considere objetivos intermediários.");
                } else if (daysToTarget < MIN_DAYS_TARGET) {
                    warnings.add("Data alvo muito próxima (<" + MIN_DAYS_TARGET + " dias). Pode ser difícil alcançar de forma saudável.");
                }

                // Validar taxa de perda/ganho baseada na data alvo
                if (currentWeight != null && targetWeight != null) {
                    BigDecimal weightDifference = targetWeight.subtract(currentWeight);
                    double weeksToTarget = daysToTarget / 7.0;
                    BigDecimal weeklyWeightChange = weightDifference.divide(BigDecimal.valueOf(weeksToTarget), 3, BigDecimal.ROUND_HALF_UP);

                    // Taxa saudável: 0.25kg - 1kg por semana
                    BigDecimal absWeeklyChange = weeklyWeightChange.abs();
                    if (absWeeklyChange.compareTo(MAX_HEALTHY_WEEKLY_WEIGHT_CHANGE) > 0) {
                        warnings.add(String.format("Taxa de mudança de peso muito alta (%.2f kg/semana). Recomendado máximo 1kg/semana.", absWeeklyChange));
                    } else if (absWeeklyChange.compareTo(MIN_HEALTHY_WEEKLY_WEIGHT_CHANGE) < 0) {
                        warnings.add(String.format("Taxa de mudança de peso muito baixa (%.2f kg/semana). Recomendado mínimo 0.25kg/semana.", absWeeklyChange));
                    }
                }
            }
        }

        // Validações adicionais de saúde
        if (height != null && currentWeight != null) {
            BigDecimal bodyMassIndex = calculateBodyMassIndex(currentWeight, height);
            if (bodyMassIndex.compareTo(BigDecimal.valueOf(16)) < 0) {
                warnings.add("IMC muito baixo (" + bodyMassIndex + "). Consulte um profissional de saúde.");
            } else if (bodyMassIndex.compareTo(BigDecimal.valueOf(40)) > 0) {
                warnings.add("IMC muito alto (" + bodyMassIndex + "). Consulte um profissional de saúde.");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Valida objetivo de peso baseado no perfil
     */
    public ValidationResult validateWeightGoal(UserProfile profile) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (profile.getCurrentWeight() == null || profile.getTargetWeight() == null) {
            return new ValidationResult(true, errors, warnings);
        }

        BigDecimal currentBodyMassIndex = profile.getBodyMassIndex();
        BigDecimal targetBodyMassIndex = calculateBodyMassIndex(profile.getTargetWeight(), profile.getHeight());

        // Validar se o objetivo é saudável
        if (currentBodyMassIndex != null && targetBodyMassIndex != null) {
            // Se pessoa está no peso normal e quer perder muito peso
            if (currentBodyMassIndex.compareTo(BigDecimal.valueOf(18.5)) >= 0 && currentBodyMassIndex.compareTo(BigDecimal.valueOf(25)) < 0 && targetBodyMassIndex.compareTo(BigDecimal.valueOf(18.5)) < 0) {
                warnings.add("Objetivo pode levar a peso abaixo do normal. Consulte um nutricionista.");
            }

            // Se pessoa tem sobrepeso/obesidade e quer ganhar peso
            if (currentBodyMassIndex.compareTo(BigDecimal.valueOf(25)) >= 0 &&
                    profile.getTargetWeight().compareTo(profile.getCurrentWeight()) > 0) {
                warnings.add("Ganho de peso pode não ser recomendado para seu IMC atual.");
            }
        }

        if (profile.getTargetDate() != null &&
                profile.getCurrentWeight() != null &&
                profile.getTargetWeight() != null) {

            LocalDate today = LocalDate.now();
            long daysToTarget = today.until(profile.getTargetDate()).getDays();

            if (daysToTarget > 0) {
                BigDecimal weightDifference = profile.getTargetWeight().subtract(profile.getCurrentWeight());
                double weeksToTarget = daysToTarget / 7.0;
                BigDecimal weeklyWeightChange = weightDifference.divide(
                        BigDecimal.valueOf(weeksToTarget), 3, BigDecimal.ROUND_HALF_UP);

                // Validar se o objetivo é realista para a data
                // Calculate calorie adjustment from profile's TDEE and daily calorie target
                if (profile.getDailyCalorieTarget() != null && profile.getTotalDailyEnergyExpenditure() != null) {
                    BigDecimal calorieAdjustment = profile.getTotalDailyEnergyExpenditure().subtract(profile.getDailyCalorieTarget());
                    int dailyCalorieAdjustment = Math.abs(calorieAdjustment.intValue());
                    int weeklyCalorieDeficit = dailyCalorieAdjustment * 7;

                    // 1kg ≈ 7700 kcal
                    BigDecimal expectedWeeklyWeightLoss = BigDecimal.valueOf(weeklyCalorieDeficit / 7700.0);
                    BigDecimal requiredWeeklyChange = weeklyWeightChange.abs();

                    if (requiredWeeklyChange.compareTo(expectedWeeklyWeightLoss.multiply(BigDecimal.valueOf(1.5))) > 0) {
                        warnings.add("Objetivo pode ser difícil de alcançar na data definida com o déficit calórico atual. " +
                                "Considere ajustar a data ou o objetivo.");
                    }
                }
            }
        }


        // Validar taxa de perda/ganho semanal segura
        // Calculate calorie adjustment from profile's TDEE and daily calorie target
        if (profile.getDailyCalorieTarget() != null && profile.getTotalDailyEnergyExpenditure() != null) {
            BigDecimal calorieAdjustment = profile.getTotalDailyEnergyExpenditure().subtract(profile.getDailyCalorieTarget());
            int weeklyCalorieDeficit = Math.abs(calorieAdjustment.intValue()) * 7;

            if (weeklyCalorieDeficit > 3500) { // Mais de 1 kg por semana
                warnings.add("Taxa de perda/ganho de peso muito alta. Recomendado máximo 0.5-1kg por semana.");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Valida atualização de peso
     */
    public ValidationResult validateWeightUpdate(BigDecimal newWeight, BigDecimal previousWeight) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Validar faixa do peso
        if (newWeight.compareTo(MIN_WEIGHT) < 0 || newWeight.compareTo(MAX_WEIGHT) > 0) {
            errors.add(String.format("Peso deve estar entre %.0f e %.0f kg", MIN_WEIGHT, MAX_WEIGHT));
        }

        // Validar mudanças drásticas
        if (previousWeight != null) {
            BigDecimal difference = newWeight.subtract(previousWeight).abs();
            if (difference.compareTo(BigDecimal.valueOf(5)) > 0) {
                warnings.add("Mudança de peso muito grande em relação ao registro anterior. " +
                        "Verifique se o valor está correto.");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    public Integer calculateDaysToTarget(UserProfile profile) {
        if (profile.getTargetDate() == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        if (profile.getTargetDate().isBefore(today) || profile.getTargetDate().isEqual(today)) {
            return 0;
        }

        return today.until(profile.getTargetDate()).getDays();
    }

    /**
     * Calcula mudança de peso semanal recomendada
     */
    public BigDecimal calculateRecommendedWeeklyWeightChange(UserProfile profile) {
        if (profile.getTargetDate() == null ||
                profile.getCurrentWeight() == null ||
                profile.getTargetWeight() == null) {
            return null;
        }

        Integer daysToTarget = calculateDaysToTarget(profile);
        if (daysToTarget == null || daysToTarget <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal weightDifference = profile.getTargetWeight().subtract(profile.getCurrentWeight());
        double weeksToTarget = daysToTarget / 7.0;

        return weightDifference.divide(BigDecimal.valueOf(weeksToTarget), 3, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Valida se o perfil está completo para cálculos
     */
    public ValidationResult validateProfileCompleteness(UserProfile profile) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (profile.getBirthDate() == null) {
            errors.add("Data de nascimento é obrigatória para cálculos metabólicos");
        }

        if (profile.getGender() == null) {
            errors.add("Sexo é obrigatório para cálculos metabólicos");
        }

        if (profile.getHeight() == null) {
            errors.add("Altura é obrigatória para cálculos metabólicos");
        }

        if (profile.getCurrentWeight() == null) {
            errors.add("Peso atual é obrigatório para cálculos metabólicos");
        }

        if (profile.getActivityLevel() == null) {
            warnings.add("Nível de atividade não informado, usando padrão (sedentário)");
        }

        if (profile.getGoal() == null) {
            warnings.add("Objetivo não informado, usando padrão (manter peso)");
        }

        if (profile.getTargetWeight() == null) {
            warnings.add("Peso alvo não informado, não será possível calcular prazo para objetivo");
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Gera recomendações baseadas no perfil
     */
    public List<String> generateRecommendations(UserProfile profile) {
        List<String> recommendations = new ArrayList<>();

        if (profile.getCurrentWeight() == null || profile.getHeight() == null) {
            return recommendations;
        }

        BigDecimal bodyMassIndex = profile.getBodyMassIndex();
        if (bodyMassIndex == null) {
            return recommendations;
        }

        // Recomendações baseadas no IMC
        if (bodyMassIndex.compareTo(BigDecimal.valueOf(18.5)) < 0) {
            recommendations.add("Seu IMC indica baixo peso. Considere aumentar a ingestão calórica saudável.");
            recommendations.add("Inclua alimentos ricos em nutrientes como castanhas, abacate e azeite.");
            recommendations.add("Consulte um nutricionista para um plano de ganho de peso saudável.");
        } else if (bodyMassIndex.compareTo(BigDecimal.valueOf(25)) < 0) {
            recommendations.add("Seu IMC está na faixa normal. Mantenha hábitos saudáveis!");
            recommendations.add("Continue com atividades físicas regulares e alimentação equilibrada.");
        } else if (bodyMassIndex.compareTo(BigDecimal.valueOf(30)) < 0) {
            recommendations.add("Seu IMC indica sobrepeso. Pequenas mudanças podem fazer diferença.");
            recommendations.add("Foque em déficit calórico moderado (300-500 kcal/dia).");
            recommendations.add("Aumente a atividade física gradualmente.");
        } else {
            recommendations.add("Seu IMC indica obesidade. Busque acompanhamento profissional.");
            recommendations.add("Considere consultar médico e nutricionista.");
            recommendations.add("Foque em mudanças graduais e sustentáveis.");
        }

        // Recomendações baseadas no nível de atividade
        if (profile.getActivityLevel() == UserProfile.ActivityLevel.SEDENTARY) {
            recommendations.add("Considere aumentar seu nível de atividade física gradualmente.");
            recommendations.add("Comece com 150 minutos de atividade moderada por semana.");
        }

        // Recomendações baseadas na idade
        Integer age = profile.getAge();
        if (age != null) {
            if (age >= 65) {
                recommendations.add("Para sua faixa etária, priorize exercícios de força e equilíbrio.");
                recommendations.add("Mantenha ingestão adequada de proteínas (1.2-1.6g/kg).");
            } else if (age >= 40) {
                recommendations.add("Após os 40, mantenha massa muscular com exercícios de resistência.");
                recommendations.add("Monitore regularmente sua composição corporal.");
            }
        }

        return recommendations;
    }

    private BigDecimal calculateBodyMassIndex(BigDecimal weight, BigDecimal height) {
        if (weight == null || height == null ||
                weight.compareTo(BigDecimal.ZERO) <= 0 ||
                height.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        BigDecimal heightInMeters = height.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal heightSquared = heightInMeters.multiply(heightInMeters);

        return weight.divide(heightSquared, 2, BigDecimal.ROUND_HALF_UP);
    }

    // ========== Classe para resultado de validação ==========

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;

        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
            this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public List<String> getWarnings() {
            return new ArrayList<>(warnings);
        }

        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }

    }
}