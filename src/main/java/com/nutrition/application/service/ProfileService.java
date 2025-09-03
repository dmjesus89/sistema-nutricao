package com.nutrition.application.service;

import com.nutrition.application.dto.auth.ApiResponse;
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
import com.nutrition.infrastructure.repository.UserProfileRepository;
import com.nutrition.infrastructure.repository.UserRepository;
import com.nutrition.infrastructure.repository.WeightHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final WeightHistoryRepository weightHistoryRepository;
    private final TotalDailyEnergyExpenditureCalculationService calculationService;
    private final ProfileValidationService validationService;

    @Transactional
    public ApiResponse<ProfileResponse> createProfile(User currentUser, CreateProfileRequest request) {
        try {

            if (profileRepository.existsByUser(currentUser)) {
                return ApiResponse.error("Usuário já possui perfil cadastrado");
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
                return ApiResponse.error("Dados inválidos: " + errorMessage);
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
                return ApiResponse.error("Perfil incompleto para cálculos: " + errorMessage);
            }

            // Calcular métricas metabólicas in case not informed
            calculationService.updateMetabolicCalculations(profile);

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

            // Adicionar recomendações à resposta (se houver)
            List<String> recommendations = validationService.generateRecommendations(profile);
            if (!recommendations.isEmpty()) {
                String recommendationsText = "Recomendações: " + String.join(" | ", recommendations);
                log.info("Generated recommendations for user {}: {}", currentUser.getEmail(), recommendationsText);
            }

            log.info("Profile created successfully for user: {}", currentUser.getEmail());
            return ApiResponse.success("Perfil criado com sucesso", response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid data in profile creation: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating profile: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    @Transactional
    public ApiResponse<ProfileResponse> updateProfile(User currentUser, UpdateProfileRequest request) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                return ApiResponse.error("Perfil não encontrado. Crie um perfil primeiro.");
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
                // Validar nova altura
                ProfileValidationService.ValidationResult heightValidation = validationService.validateProfileData(
                        null, null, request.getHeight(), null, null, null
                );

                if (!heightValidation.isValid()) {
                    String errorMessage = String.join("; ", heightValidation.getErrors());
                    return ApiResponse.error("Altura inválida: " + errorMessage);
                }

                profile.setHeight(request.getHeight());
                needsRecalculation = true;
            }

            if (request.getTargetWeight() != null) {
                // Validar novo peso alvo
                ProfileValidationService.ValidationResult weightValidation = validationService.validateProfileData(
                        null, null, null, null, request.getTargetWeight(), null
                );

                if (!weightValidation.isValid()) {
                    String errorMessage = String.join("; ", weightValidation.getErrors());
                    return ApiResponse.error("Peso alvo inválido: " + errorMessage);
                }

                profile.setTargetWeight(request.getTargetWeight());
                needsRecalculation = true;
                needsGoalValidation = true;
            }

            if (request.getTargetDate() != null) {
                // Validate target date
                ProfileValidationService.ValidationResult dateValidation = validationService.validateProfileData(
                        null, null, null, null, null, request.getTargetDate()
                );

                if (!dateValidation.isValid()) {
                    String errorMessage = String.join("; ", dateValidation.getErrors());
                    return ApiResponse.error("Data alvo inválida: " + errorMessage);
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

            // Recalcular métricas se necessário
            if (needsRecalculation) {
                calculationService.updateMetabolicCalculations(profile);
            }

            // Validar objetivo após atualizações
            if (needsGoalValidation) {
                ProfileValidationService.ValidationResult goalValidation =
                        validationService.validateWeightGoal(profile);

                if (goalValidation.hasWarnings()) {
                    String warnings = String.join("; ", goalValidation.getWarnings());
                    log.warn("Weight goal validation warnings for user {}: {}", currentUser.getEmail(), warnings);
                }
            }

            profile = profileRepository.save(profile);

            ProfileResponse response = buildProfileResponse(profile);

            log.info("Profile updated successfully for user: {}", currentUser.getEmail());
            return ApiResponse.success("Perfil atualizado com sucesso", response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid data in profile update: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    @Transactional
    public ApiResponse<WeightHistoryResponse> updateWeight(User currentUser, WeightUpdateRequest request) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                return ApiResponse.error("Perfil não encontrado. Crie um perfil primeiro.");
            }

            LocalDate recordDate = request.getRecordedDate() != null ?
                    request.getRecordedDate() : LocalDate.now();

            // VALIDAÇÕES COM ProfileValidationService
            // Buscar peso anterior para validação
            WeightHistory latestWeight = weightHistoryRepository
                    .findLatestByUser(currentUser)
                    .orElse(null);

            BigDecimal previousWeight = latestWeight != null ? latestWeight.getWeight() : null;

            ProfileValidationService.ValidationResult weightValidation =
                    validationService.validateWeightUpdate(request.getWeight(), previousWeight);

            if (!weightValidation.isValid()) {
                String errorMessage = String.join("; ", weightValidation.getErrors());
                log.warn("Weight update validation failed for user {}: {}", currentUser.getEmail(), errorMessage);
                return ApiResponse.error("Peso inválido: " + errorMessage);
            }

            // Log warnings if any
            if (weightValidation.hasWarnings()) {
                String warnings = String.join("; ", weightValidation.getWarnings());
                log.warn("Weight update warnings for user {}: {}", currentUser.getEmail(), warnings);
            }

            // Verificar se já existe registro para esta data
            WeightHistory existingRecord = weightHistoryRepository
                    .findByUserAndRecordedDate(currentUser, recordDate)
                    .orElse(null);

            WeightHistory weightRecord;

            if (existingRecord != null) {
                // Atualizar registro existente
                existingRecord.setWeight(request.getWeight());
                existingRecord.setNotes(request.getNotes());
                weightRecord = weightHistoryRepository.save(existingRecord);
                log.info("Weight record updated for user {} on date {}",
                        currentUser.getEmail(), recordDate);
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

            WeightHistoryResponse response = buildWeightHistoryResponse(weightRecord, currentUser);

            return ApiResponse.success("Peso atualizado com sucesso", response);

        } catch (Exception e) {
            log.error("Error updating weight: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    public ApiResponse<ProfileResponse> getProfile(User currentUser) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                return ApiResponse.error("Perfil não encontrado");
            }

            ProfileResponse response = buildProfileResponse(profile);
            return ApiResponse.success(response);

        } catch (Exception e) {
            log.error("Error getting profile: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    public ApiResponse<List<WeightHistoryResponse>> getWeightHistory(User currentUser) {
        try {
            List<WeightHistory> history = weightHistoryRepository
                    .findByUserOrderByRecordedDateDesc(currentUser);

            List<WeightHistoryResponse> responses = history.stream()
                    .map(record -> buildWeightHistoryResponse(record, currentUser))
                    .collect(Collectors.toList());

            return ApiResponse.success(responses);

        } catch (Exception e) {
            log.error("Error getting weight history: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    public ApiResponse<WeightStatsResponse> getWeightStats(User currentUser) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                return ApiResponse.error("Perfil não encontrado");
            }

            WeightHistory firstWeight = weightHistoryRepository.findFirstWeightByUser(currentUser).orElse(null);
            WeightHistory latestWeight = weightHistoryRepository.findLatestByUser(currentUser).orElse(null);
            BigDecimal minWeight = weightHistoryRepository.findMinWeightByUser(currentUser).orElse(null);
            BigDecimal maxWeight = weightHistoryRepository.findMaxWeightByUser(currentUser).orElse(null);
            long totalMeasurements = weightHistoryRepository.countByUser(currentUser);

            WeightStatsResponse.WeightStatsResponseBuilder responseBuilder = WeightStatsResponse.builder()
                    .currentWeight(profile.getCurrentWeight())
                    .targetWeight(profile.getTargetWeight())
                    .initialWeight(firstWeight != null ? firstWeight.getWeight() : null)
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

            WeightStatsResponse response = responseBuilder.build();
            return ApiResponse.success(response);

        } catch (Exception e) {
            log.error("Error getting weight stats: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    public ApiResponse<TotalDailyEnergyExpenditureCalculationResponse> getTotalDailyEnergyExpenditureCalculation(User currentUser) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                return ApiResponse.error("Perfil não encontrado");
            }

            TotalDailyEnergyExpenditureCalculationResponse response = TotalDailyEnergyExpenditureCalculationResponse.builder()
                    .basalMetabolicRate(profile.getBasalMetabolicRate())
                    .totalDailyEnergyExpenditure(profile.getTotalDailyEnergyExpenditure())
                    .dailyCalorieTarget(profile.getDailyCalorieTarget())
                    .calculationMethod("Mifflin-St Jeor")
                    .activityMultiplier(profile.getActivityLevel().getMultiplier())
                    .calorieAdjustment(profile.getGoal().getCalorieAdjustment())
                    .calculatedAt(profile.getUpdatedAt() != null ?
                            profile.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                            profile.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();

            return ApiResponse.success(response);

        } catch (Exception e) {
            log.error("Error getting TotalDailyEnergyExpenditure calculation: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    // Métodos auxiliares

//    private User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
//    }

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
        return ProfileResponse.builder()
                .id(profile.getId())
                .birthDate(profile.getBirthDate())
                .age(profile.getAge())
                .gender(profile.getGender().name())
                .genderDisplay(profile.getGender().getDisplayName())
                .height(profile.getHeight())
                .currentWeight(profile.getCurrentWeight())
                .targetWeight(profile.getTargetWeight())
                .targetDate(profile.getTargetDate())
                .activityLevel(profile.getActivityLevel().name())
                .activityLevelDisplay(profile.getActivityLevel().getDisplayName())
                .goal(profile.getGoal().name())
                .goalDisplay(profile.getGoal().getDisplayName())
                .basalMetabolicRate(profile.getBasalMetabolicRate())
                .totalDailyEnergyExpenditure(profile.getTotalDailyEnergyExpenditure())
                .dailyCalorieTarget(profile.getDailyCalorieTarget())
                .bodyMassIndex(profile.getBodyMassIndex())
                .bodyMassIndexCategory(profile.getBodyMassIndexCategory())
                .createdAt(profile.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .updatedAt(profile.getUpdatedAt() != null ?
                        profile.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
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

    public ApiResponse<ProfileRecommendationsResponse> getRecommendations(User currentUser) {
        try {
            UserProfile profile = profileRepository.findByUser(currentUser)
                    .orElse(null);

            if (profile == null) {
                return ApiResponse.error("Perfil não encontrado");
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

            return ApiResponse.success(response);

        } catch (Exception e) {
            log.error("Error getting recommendations: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }
}