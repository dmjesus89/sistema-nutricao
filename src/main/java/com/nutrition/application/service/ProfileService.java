package com.nutrition.application.service;

import com.nutrition.application.dto.profile.CreateProfileRequest;
import com.nutrition.application.dto.profile.ProfileRecommendationsResponse;
import com.nutrition.application.dto.profile.ProfileResponse;
import com.nutrition.application.dto.profile.TotalDailyEnergyExpenditureCalculationResponse;
import com.nutrition.application.dto.profile.UpdateProfileRequest;
import com.nutrition.application.dto.profile.WeightHistoryResponse;
import com.nutrition.application.dto.profile.WeightStatsResponse;
import com.nutrition.application.dto.profile.WeightUpdateRequest;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.profile.UserProfile;
import com.nutrition.domain.entity.profile.WeightHistory;
import com.nutrition.infrastructure.exception.NotFoundException;
import com.nutrition.infrastructure.exception.UnprocessableEntityException;
import com.nutrition.infrastructure.repository.ActivityLevelConfigRepository;
import com.nutrition.infrastructure.repository.UserProfileRepository;
import com.nutrition.infrastructure.repository.UserRepository;
import com.nutrition.infrastructure.repository.WeightHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserProfileRepository profileRepository;
    private final WeightHistoryRepository weightHistoryRepository;
    private final TotalDailyEnergyExpenditureCalculationService calculationService;
    private final ProfileValidationService validationService;
    private final ActivityLevelConfigRepository activityLevelConfigRepository;

    @Transactional
    public ProfileResponse createProfile(User currentUser, CreateProfileRequest request) {
        try {

            if (profileRepository.existsByUser(currentUser)) {
                throw new UnprocessableEntityException("Usuário já possui perfil cadastrado");
            }

            UserProfile.Gender gender = parseGender(request.getGender());
            UserProfile.ActivityLevel activityLevel = parseActivityLevel(request.getActivityLevel());
            UserProfile.Goal goal = parseGoal(request.getGoal());

            // VALIDAÇÕES COM ProfileValidationService
            ProfileValidationService.ValidationResult validation = validationService.validateProfileData(
                    request.getBirthDate(), gender, request.getHeight(),
                    request.getCurrentWeight(), request.getTargetWeight(), request.getTargetDate()
            );

            if (!validation.isValid()) {
                String errorMessage = String.join("; ", validation.getErrors());
                log.warn("Profile validation failed for user {}: {}", currentUser.getEmail(), errorMessage);
                throw new UnprocessableEntityException("Dados inválidos: " + errorMessage);
            }

            // Log warnings if any
            if (validation.hasWarnings()) {
                String warnings = String.join("; ", validation.getWarnings());
                log.warn("Profile validation warnings for user {}: {}", currentUser.getEmail(), warnings);
            }

            // Criar perfil
            UserProfile profile = UserProfile.builder()
                    .user(currentUser)
                    .birthDate(request.getBirthDate())
                    .gender(gender)
                    .height(request.getHeight())
                    .currentWeight(request.getCurrentWeight())
                    .targetWeight(request.getTargetWeight())
                    .targetDate(request.getTargetDate())
                    .activityLevel(activityLevel)
                    .goal(goal)
                    .basalMetabolicRate(request.getBasalMetabolicRate())
                    .totalDailyEnergyExpenditure(request.getTotalDailyEnergyExpenditure())
                    .dailyCalorieTarget(request.getDailyCalorieTarget())
                    .build();

            // Validar completude do perfil para cálculos
            ProfileValidationService.ValidationResult completenessValidation = validationService.validateProfileCompleteness(profile);

            if (!completenessValidation.isValid()) {
                String errorMessage = String.join("; ", completenessValidation.getErrors());
                log.error("Profile completeness validation failed: {}", errorMessage);
                throw new UnprocessableEntityException("Perfil incompleto para cálculos: " + errorMessage);
            }

            // Calcular métricas metabólicas in case not informed
            calculationService.updateMetabolicCalculations(profile);
            profile.getGoal().setCalorieAdjustment(profile.getDailyCalorieTarget().subtract(profile.getTotalDailyEnergyExpenditure()));


            // Validar objetivo de peso após cálculos
            ProfileValidationService.ValidationResult goalValidation = validationService.validateWeightGoal(profile);

            if (goalValidation.hasWarnings()) {
                String warnings = String.join("; ", goalValidation.getWarnings());
                log.info("Weight goal warnings for user {}: {}", currentUser.getEmail(), warnings);
            }

            profile = profileRepository.save(profile);

            // Criar primeiro registro de peso
            WeightHistory firstWeight = WeightHistory.builder()
                    .user(currentUser)
                    .weight(request.getCurrentWeight())
                    .recordedDate(LocalDate.now())
                    .notes("Peso inicial")
                    .build();

            weightHistoryRepository.save(firstWeight);

            ProfileResponse response = buildProfileResponse(profile);

            // Adicionar warnings à resposta
            if (goalValidation.hasWarnings()) {
                response.setWarnings(goalValidation.getWarnings());
            }

            // Adicionar recomendações à resposta (se houver)
            List<String> recommendations = validationService.generateRecommendations(profile);
            if (!recommendations.isEmpty()) {
                String recommendationsText = "Recomendações: " + String.join(" | ", recommendations);
                log.info("Generated recommendations for user {}: {}", currentUser.getEmail(), recommendationsText);
            }

            log.info("Profile created successfully for user: {}", currentUser.getEmail());
            return response;

        } catch (Exception e) {
            log.error("Error creating profile: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public ProfileResponse updateProfile(User currentUser, UpdateProfileRequest request) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                throw new UnprocessableEntityException("Perfil não encontrado. Crie um perfil primeiro.");
            }

            boolean needsRecalculation = false;
            boolean needsGoalValidation = false;

            // Atualizar campos se fornecidos
            if (request.getBirthDate() != null) {
                profile.setBirthDate(request.getBirthDate());
                needsRecalculation = true;
            }

            if (request.getGender() != null) {
                UserProfile.Gender newGender = parseGender(request.getGender());
                profile.setGender(newGender);
                needsRecalculation = true;
            }

            if (request.getHeight() != null) {
                ProfileValidationService.ValidationResult heightValidation = validationService.validateProfileData(
                        null, null, request.getHeight(), null, null, null
                );

                if (!heightValidation.isValid()) {
                    String errorMessage = String.join("; ", heightValidation.getErrors());
                    throw new UnprocessableEntityException("Altura inválida: " + errorMessage);
                }

                profile.setHeight(request.getHeight());
                needsRecalculation = true;
            }

            if (request.getTargetWeight() != null) {
                ProfileValidationService.ValidationResult weightValidation = validationService.validateProfileData(
                        null, null, null, null, request.getTargetWeight(), null
                );

                if (!weightValidation.isValid()) {
                    String errorMessage = String.join("; ", weightValidation.getErrors());
                    throw new UnprocessableEntityException("Peso alvo inválido: " + errorMessage);
                }

                profile.setTargetWeight(request.getTargetWeight());
                needsRecalculation = true;
                needsGoalValidation = true;
            }

            if (request.getTargetDate() != null) {
                ProfileValidationService.ValidationResult dateValidation = validationService.validateProfileData(
                        null, null, null, null, null, request.getTargetDate()
                );

                if (!dateValidation.isValid()) {
                    String errorMessage = String.join("; ", dateValidation.getErrors());
                    throw new UnprocessableEntityException("Data alvo inválida: " + errorMessage);
                }

                profile.setTargetDate(request.getTargetDate());
                needsRecalculation = true;
                needsGoalValidation = true;
            }

            if (request.getActivityLevel() != null) {
                profile.setActivityLevel(parseActivityLevel(request.getActivityLevel()));
                needsRecalculation = true;
            }

            if (request.getGoal() != null) {
                profile.setGoal(parseGoal(request.getGoal()));
                needsRecalculation = true;
                needsGoalValidation = true;
            }

            if (request.getBasalMetabolicRate() != null) {
                profile.setBasalMetabolicRate(request.getBasalMetabolicRate());
                needsRecalculation = true;
            }

            if (request.getTotalDailyEnergyExpenditure() != null) {
                profile.setTotalDailyEnergyExpenditure(request.getTotalDailyEnergyExpenditure());
                needsRecalculation = true;
            }

            if (request.getDailyCalorieTarget() != null) {
                profile.setDailyCalorieTarget(request.getDailyCalorieTarget());
                needsRecalculation = true;
            }

            if (needsRecalculation) {
                calculationService.updateMetabolicCalculations(profile);
                profile.getGoal().setCalorieAdjustment(profile.getDailyCalorieTarget().subtract(profile.getTotalDailyEnergyExpenditure()));
            }

            ProfileValidationService.ValidationResult goalValidation = null;
            if (needsGoalValidation) {
                goalValidation = validationService.validateWeightGoal(profile);

                if (goalValidation.hasWarnings()) {
                    String warnings = String.join("; ", goalValidation.getWarnings());
                    log.warn("Weight goal validation warnings for user {}: {}", currentUser.getEmail(), warnings);
                }
            }

            profile = profileRepository.save(profile);

            ProfileResponse response = buildProfileResponse(profile);

            // Adicionar warnings à resposta
            if (goalValidation != null && goalValidation.hasWarnings()) {
                response.setWarnings(goalValidation.getWarnings());
            }

            log.info("Profile updated successfully for user: {}", currentUser.getEmail());
            return response;
        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public WeightHistoryResponse updateWeight(User currentUser, WeightUpdateRequest request) {
        try {


            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                throw new UnprocessableEntityException("Perfil não encontrado. Crie um perfil primeiro.");
            }

            LocalDate recordDate = request.getRecordedDate() != null ? request.getRecordedDate() : LocalDate.now();

            if (recordDate.isAfter(LocalDate.now())) {
                throw new UnprocessableEntityException("Não é possivel inserir peso em data futura");
            }

            // VALIDAÇÕES COM ProfileValidationService
            // Buscar peso anterior para validação
            WeightHistory latestWeight = weightHistoryRepository.findLatestByUser(currentUser).orElse(null);

            BigDecimal previousWeight = latestWeight != null ? latestWeight.getWeight() : null;

            ProfileValidationService.ValidationResult weightValidation = validationService.validateWeightUpdate(request.getWeight(), previousWeight);

            if (!weightValidation.isValid()) {
                String errorMessage = String.join("; ", weightValidation.getErrors());
                log.warn("Weight update validation failed for user {}: {}", currentUser.getEmail(), errorMessage);
                throw new UnprocessableEntityException("Peso inválido: " + errorMessage);
            }

            // Log warnings if any
            if (weightValidation.hasWarnings()) {
                String warnings = String.join("; ", weightValidation.getWarnings());
                log.warn("Weight update warnings for user {}: {}", currentUser.getEmail(), warnings);
            }

            // Verificar se já existe registro para esta data
            WeightHistory existingRecord = weightHistoryRepository.findByUserAndRecordedDate(currentUser, recordDate).orElse(null);

            WeightHistory weightRecord;

            if (existingRecord != null) {

                WeightHistory firstWeight = weightHistoryRepository.findFirstWeightByUser(currentUser).orElse(null);
                if (recordDate.equals(firstWeight.getRecordedDate())) {
                    throw new UnprocessableEntityException("Não é possivel inserir um peso no dia de inicio");
                }

                // Atualizar registro existente
                existingRecord.setWeight(request.getWeight());
                existingRecord.setNotes(request.getNotes());
                weightRecord = weightHistoryRepository.save(existingRecord);
                log.info("Weight record updated for user {} on date {}", currentUser.getEmail(), recordDate);
            } else {
                // Criar novo registro
                weightRecord = WeightHistory.builder()
                        .user(currentUser)
                        .weight(request.getWeight())
                        .recordedDate(recordDate)
                        .notes(request.getNotes())
                        .build();
                weightRecord = weightHistoryRepository.save(weightRecord);
                log.info("New weight record created for user {} on date {}",
                        currentUser.getEmail(), recordDate);
            }

            // Atualizar peso atual no perfil se for o registro mais recente
            WeightHistory newLatestWeight = weightHistoryRepository
                    .findLatestByUser(currentUser)
                    .orElse(null);

            if (newLatestWeight != null && newLatestWeight.getWeight().equals(request.getWeight())) {
                profile.setCurrentWeight(request.getWeight());
                calculationService.updateMetabolicCalculations(profile);

                // Revalidar objetivo após mudança de peso
                ProfileValidationService.ValidationResult goalValidation =
                        validationService.validateWeightGoal(profile);

                if (goalValidation.hasWarnings()) {
                    String warnings = String.join("; ", goalValidation.getWarnings());
                    log.info("Goal validation warnings after weight update for user {}: {}",
                            currentUser.getEmail(), warnings);
                }

                profileRepository.save(profile);
            }

            return buildWeightHistoryResponse(weightRecord, currentUser);
        } catch (Exception e) {
            log.error("Error updating weight: {}", e.getMessage());
            throw e;
        }
    }

    public ProfileResponse getProfile(User currentUser) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                throw new UnprocessableEntityException("Perfil não encontrado");
            }

            return buildProfileResponse(profile);
        } catch (Exception e) {
            log.error("Error getting profile: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void deleteProfile(User currentUser) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                throw new NotFoundException("Perfil não encontrado");
            }

            profileRepository.delete(profile);
            weightHistoryRepository.deleteByUser(currentUser);
        } catch (Exception e) {
            log.error("Error deleting profile: {}", e.getMessage());
            throw e;
        }
    }

    public List<WeightHistoryResponse> getWeightHistory(User currentUser) {
        try {
            List<WeightHistory> history = weightHistoryRepository
                    .findByUserOrderByRecordedDateDesc(currentUser);

            return history.stream()
                    .map(record -> buildWeightHistoryResponse(record, currentUser))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting weight history: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void deleteWeightRecord(User currentUser, Long weightId) {
        try {
            // Find the weight record
            WeightHistory weightRecord = weightHistoryRepository.findById(weightId)
                    .orElseThrow(() -> new NotFoundException("Registro de peso não encontrado"));

            // Verify the weight record belongs to the current user
            if (!weightRecord.getUser().getId().equals(currentUser.getId())) {
                throw new UnprocessableEntityException("Você não tem permissão para remover este registro");
            }

            // Get user profile
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElseThrow(() -> new NotFoundException("Perfil não encontrado"));

            // Check if this is the initial weight (first recorded)
            WeightHistory firstWeight = weightHistoryRepository.findFirstWeightByUser(currentUser).orElse(null);
            if (firstWeight != null && firstWeight.getId().equals(weightId)) {
                throw new UnprocessableEntityException("Não é possível remover o peso inicial");
            }

            // Check if this is the most recent weight
            WeightHistory latestWeight = weightHistoryRepository.findLatestByUser(currentUser).orElse(null);
            boolean isDeletingLatest = latestWeight != null && latestWeight.getId().equals(weightId);

            // Delete the weight record
            weightHistoryRepository.deleteById(weightId);
            log.info("Weight record deleted successfully: {}", weightId);

            // If we deleted the latest weight, update the current weight in profile
            if (isDeletingLatest) {
                WeightHistory newLatestWeight = weightHistoryRepository.findLatestByUser(currentUser).orElse(null);
                if (newLatestWeight != null) {
                    profile.setCurrentWeight(newLatestWeight.getWeight());
                    profileRepository.save(profile);
                    log.info("Updated current weight to: {}", newLatestWeight.getWeight());

                    // Recalculate metabolic data with new current weight
                    calculationService.calculateAndSaveTotalDailyEnergyExpenditure(currentUser);
                }
            }

        } catch (NotFoundException | UnprocessableEntityException e) {
            log.error("Error deleting weight record: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error deleting weight record: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro ao remover registro de peso");
        }
    }

    public WeightStatsResponse getWeightStats(User currentUser) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                throw new UnprocessableEntityException("Perfil não encontrado");
            }

            WeightHistory firstWeight = weightHistoryRepository.findFirstWeightByUser(currentUser).orElse(null);
            WeightHistory latestWeight = weightHistoryRepository.findLatestByUser(currentUser).orElse(null);
            BigDecimal minWeight = weightHistoryRepository.findMinWeightByUser(currentUser).orElse(null);
            BigDecimal maxWeight = weightHistoryRepository.findMaxWeightByUser(currentUser).orElse(null);
            long totalMeasurements = weightHistoryRepository.countByUser(currentUser);

            // Calcular mudanças de peso
            BigDecimal averageWeeklyChange = calculateAverageWeeklyChange(currentUser);
            BigDecimal monthlyChange = calculateMonthlyChange(currentUser);

            WeightStatsResponse.WeightStatsResponseBuilder responseBuilder = WeightStatsResponse.builder()
                    .currentWeight(profile.getCurrentWeight())
                    .targetWeight(profile.getTargetWeight())
                    .initialWeight(firstWeight != null ? firstWeight.getWeight() : null)
                    .lastRecordDate(latestWeight != null ? latestWeight.getRecordedDate() : null)
                    .averageWeeklyChange(averageWeeklyChange)
                    .monthlyChange(monthlyChange)
                    .minWeight(minWeight)
                    .maxWeight(maxWeight)
                    .totalMeasurements(totalMeasurements);

            // Calcular mudança total de peso
            if (firstWeight != null && profile.getCurrentWeight() != null) {
                BigDecimal totalWeightChange = profile.getCurrentWeight().subtract(firstWeight.getWeight());
                responseBuilder.totalWeightChange(totalWeightChange);
            }

            // Calcular peso restante para meta
            if (profile.getTargetWeight() != null && profile.getCurrentWeight() != null) {
                BigDecimal weightToGoal = profile.getTargetWeight().subtract(profile.getCurrentWeight());
                responseBuilder.weightToGoal(weightToGoal);
            }

            // Calcular dias de rastreamento
            if (firstWeight != null) {
                long daysSinceFirst = LocalDate.now().toEpochDay() - firstWeight.getRecordedDate().toEpochDay();
                responseBuilder.daysTracking(Math.max(1, daysSinceFirst));
            }

            return responseBuilder.build();
        } catch (Exception e) {
            log.error("Error getting weight stats: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Calcula a mudança média de peso semanal baseada no histórico
     */
    private BigDecimal calculateAverageWeeklyChange(User currentUser) {
        List<WeightHistory> weightHistory = weightHistoryRepository
                .findByUserOrderByRecordedDateAsc(currentUser); // Ordenar por data crescente

        if (weightHistory.size() < 2) {
            return BigDecimal.ZERO; // Precisa de pelo menos 2 registros
        }

        WeightHistory firstRecord = weightHistory.get(0);
        WeightHistory lastRecord = weightHistory.get(weightHistory.size() - 1);

        // Calcular diferença total de peso
        BigDecimal totalWeightChange = lastRecord.getWeight().subtract(firstRecord.getWeight());

        // Calcular diferença total de dias
        long totalDays = firstRecord.getRecordedDate().until(lastRecord.getRecordedDate()).getDays();

        if (totalDays <= 0) {
            return BigDecimal.ZERO;
        }

        // Converter para semanas e calcular média
        double totalWeeks = totalDays / 7.0;

        if (totalWeeks < 0.5) { // Menos de meio semana
            return BigDecimal.ZERO;
        }

        BigDecimal averageWeeklyChange = totalWeightChange.divide(
                BigDecimal.valueOf(totalWeeks), 3, RoundingMode.HALF_UP);

        return averageWeeklyChange;
    }

    /**
     * Calcula a mudança de peso no último mês
     */
    private BigDecimal calculateMonthlyChange(User currentUser) {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        LocalDate twoWeeksRange = LocalDate.now().minusDays(14); // Give some flexibility

        // Search for weights within a range around one month ago
        List<WeightHistory> weightsAroundMonth = weightHistoryRepository
                .findByUserAndDateRange(currentUser, oneMonthAgo.minusDays(7), oneMonthAgo.plusDays(7));

        // Buscar peso mais recente
        WeightHistory latestWeight = weightHistoryRepository
                .findLatestByUser(currentUser)
                .orElse(null);

        if (weightsAroundMonth.isEmpty() || latestWeight == null) {
            return BigDecimal.ZERO;
        }

        // Find the closest weight to one month ago
        WeightHistory closestWeight = weightsAroundMonth.stream()
                .min((w1, w2) -> {
                    long diff1 = Math.abs(oneMonthAgo.toEpochDay() - w1.getRecordedDate().toEpochDay());
                    long diff2 = Math.abs(oneMonthAgo.toEpochDay() - w2.getRecordedDate().toEpochDay());
                    return Long.compare(diff1, diff2);
                })
                .orElse(weightsAroundMonth.get(0));

        // Calcular diferença
        BigDecimal monthlyChange = latestWeight.getWeight().subtract(closestWeight.getWeight());

        return monthlyChange.setScale(3, RoundingMode.HALF_UP);
    }



    public TotalDailyEnergyExpenditureCalculationResponse getTotalDailyEnergyExpenditureCalculation(User currentUser) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                throw new UnprocessableEntityException("Perfil não encontrado");
            }

            // Get activity multiplier from database configuration
            BigDecimal activityMultiplier = activityLevelConfigRepository
                    .findByCodeAndActive(profile.getActivityLevel().name(), true)
                    .map(config -> config.getMultiplier())
                    .orElse(BigDecimal.valueOf(profile.getActivityLevel().getMultiplier())); // Fallback to enum value

            TotalDailyEnergyExpenditureCalculationResponse response = TotalDailyEnergyExpenditureCalculationResponse.builder()
                    .basalMetabolicRate(profile.getBasalMetabolicRate())
                    .totalDailyEnergyExpenditure(profile.getTotalDailyEnergyExpenditure())
                    .dailyCalorieTarget(profile.getDailyCalorieTarget())
                    .calculationMethod("Mifflin-St Jeor")
                    .activityMultiplier(activityMultiplier.doubleValue())
                    .calorieAdjustment(profile.getDailyCalorieTarget().subtract(profile.getTotalDailyEnergyExpenditure()))
                    .dailyWaterIntake(profile.getDailyWaterIntake())
                    .calculatedAt(profile.getUpdatedAt() != null ?
                            profile.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                            profile.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();

            return response;

        } catch (Exception e) {
            log.error("Error getting TotalDailyEnergyExpenditure calculation: {}", e.getMessage());
            throw e;
        }
    }

    private UserProfile.Gender parseGender(String gender) {
        try {
            return UserProfile.Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Sexo inválido: " + gender);
        }
    }

    private UserProfile.ActivityLevel parseActivityLevel(String activityLevel) {
        try {
            return UserProfile.ActivityLevel.valueOf(activityLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nível de atividade inválido: " + activityLevel);
        }
    }

    private UserProfile.Goal parseGoal(String goal) {
        try {
            return UserProfile.Goal.valueOf(goal.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Objetivo inválido: " + goal);
        }
    }

    private ProfileResponse buildProfileResponse(UserProfile profile) {
        // Get user's preferred locale for translations
        String locale = profile.getUser() != null && profile.getUser().getPreferredLocale() != null
            ? profile.getUser().getPreferredLocale()
            : "en";

        return ProfileResponse.builder()
                .id(profile.getId())
                .birthDate(profile.getBirthDate())
                .age(profile.getAge())
                .gender(profile.getGender().name())
                .genderDisplay(translateGender(profile.getGender(), locale))
                .height(profile.getHeight())
                .currentWeight(profile.getCurrentWeight())
                .targetWeight(profile.getTargetWeight())
                .targetDate(profile.getTargetDate())
                .activityLevel(profile.getActivityLevel().name())
                .activityLevelDisplay(translateActivityLevel(profile.getActivityLevel(), locale))
                .goal(profile.getGoal().name())
                .goalDisplay(translateGoal(profile.getGoal(), locale))
                .basalMetabolicRate(profile.getBasalMetabolicRate())
                .totalDailyEnergyExpenditure(profile.getTotalDailyEnergyExpenditure())
                .dailyCalorieTarget(profile.getDailyCalorieTarget())
                .bodyMassIndex(profile.getBodyMassIndex())
                .bodyMassIndexCategory(profile.getBodyMassIndexCategory())
                .daysToTarget(profile.getDaysToTarget() != null ? profile.getDaysToTarget().longValue() : null)
                .recommendedWeeklyWeightChange(profile.getRecommendedWeeklyWeightChange())
                .dailyWaterIntake(profile.getDailyWaterIntake())
                .createdAt(profile.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .updatedAt(profile.getUpdatedAt() != null ?
                        profile.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }

    private String translateGender(UserProfile.Gender gender, String locale) {
        if (locale.equals("es")) {
            return switch (gender) {
                case MALE -> "Masculino";
                case FEMALE -> "Femenino";
                case OTHER -> "Otro";
            };
        } else if (locale.equals("pt")) {
            return switch (gender) {
                case MALE -> "Masculino";
                case FEMALE -> "Feminino";
                case OTHER -> "Outro";
            };
        }
        // Default to English
        return gender.getDisplayName();
    }

    private String translateActivityLevel(UserProfile.ActivityLevel activityLevel, String locale) {
        if (locale.equals("es")) {
            return switch (activityLevel) {
                case SEDENTARY -> "Sedentario";
                case LIGHTLY_ACTIVE -> "Ligeramente Activo";
                case MODERATELY_ACTIVE -> "Moderadamente Activo";
                case VERY_ACTIVE -> "Muy Activo";
                case EXTREMELY_ACTIVE -> "Extremadamente Activo";
            };
        } else if (locale.equals("pt")) {
            return switch (activityLevel) {
                case SEDENTARY -> "Sedentário";
                case LIGHTLY_ACTIVE -> "Levemente Ativo";
                case MODERATELY_ACTIVE -> "Moderadamente Ativo";
                case VERY_ACTIVE -> "Muito Ativo";
                case EXTREMELY_ACTIVE -> "Extremamente Ativo";
            };
        }
        // Default to English
        return activityLevel.getDisplayName();
    }

    private String translateGoal(UserProfile.Goal goal, String locale) {
        if (locale.equals("es")) {
            return switch (goal) {
                case LOSE_WEIGHT -> "Perder Grasa/Mantener Músculo";
                case MAINTAIN_WEIGHT -> "Mantener Peso";
                case GAIN_WEIGHT -> "Ganar Peso";
            };
        } else if (locale.equals("pt")) {
            return switch (goal) {
                case LOSE_WEIGHT -> "Perder Gordura/Manter Músculo";
                case MAINTAIN_WEIGHT -> "Manter Peso";
                case GAIN_WEIGHT -> "Ganhar Peso";
            };
        }
        // Default to English
        return goal.getDisplayName();
    }

    private WeightHistoryResponse buildWeightHistoryResponse(WeightHistory record, User user) {
        // Buscar peso anterior para calcular diferença
        List<WeightHistory> previousWeights = weightHistoryRepository
                .findByUserOrderByRecordedDateDesc(user);

        BigDecimal weightDifference = BigDecimal.ZERO;
        if (previousWeights.size() > 1) {
            // Encontrar o registro anterior a este
            for (int i = 0; i < previousWeights.size() - 1; i++) {
                if (previousWeights.get(i).getId().equals(record.getId())) {
                    WeightHistory previousRecord = previousWeights.get(i + 1);
                    weightDifference = record.getWeight().subtract(previousRecord.getWeight());
                    break;
                }
            }
        }

        return WeightHistoryResponse.builder()
                .id(record.getId())
                .weight(record.getWeight())
                .recordedDate(record.getRecordedDate())
                .notes(record.getNotes())
                .weightDifference(weightDifference)
                .isRecent(record.isRecentMeasurement())
                .createdAt(record.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    public ProfileRecommendationsResponse getRecommendations(User currentUser) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                throw new UnprocessableEntityException("Perfil não encontrado");
            }

            // Gerar recomendações usando o ProfileValidationService
            List<String> recommendations = validationService.generateRecommendations(profile);

            // Validar completude do perfil
            ProfileValidationService.ValidationResult completeness =
                    validationService.validateProfileCompleteness(profile);

            // Validar objetivo atual
            ProfileValidationService.ValidationResult goalValidation =
                    validationService.validateWeightGoal(profile);

            ProfileRecommendationsResponse response = ProfileRecommendationsResponse.builder()
                    .recommendations(recommendations)
                    .profileCompleteness(completeness.isValid())
                    .completenessWarnings(completeness.getWarnings())
                    .goalWarnings(goalValidation.getWarnings())
                    .currentBodyMassIndex(profile.getBodyMassIndex())
                    .bodyMassIndexCategory(profile.getBodyMassIndexCategory())
                    .weeksToGoal(calculationService.calculateWeeksToGoal(profile))
                    .dailyWaterIntake(calculationService.calculateDailyWaterIntake(profile))
                    .build();

            return response;

        } catch (Exception e) {
            log.error("Error getting recommendations: {}", e.getMessage());
            throw e;
        }
    }
}