package com.nutrition.application.service;

import com.nutrition.application.dto.food.AddScheduleRequest;
import com.nutrition.application.dto.food.AddSupplementRequest;
import com.nutrition.application.dto.food.CreateSupplementRequest;
import com.nutrition.application.dto.food.ScheduleResponse;
import com.nutrition.application.dto.food.SupplementResponse;
import com.nutrition.application.dto.food.TimeRoutineRequest;
import com.nutrition.application.dto.food.UpdateScheduleRequest;
import com.nutrition.application.dto.food.UpdateSupplementFrequencyRequest;
import com.nutrition.application.dto.food.UserPreferenceRequest;
import com.nutrition.application.dto.food.UserSupplementResponse;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.Supplement;
import com.nutrition.domain.entity.food.UserSupplement;
import com.nutrition.domain.entity.food.UserSupplementSchedule;
import com.nutrition.infrastructure.exception.UnprocessableEntityException;
import com.nutrition.infrastructure.repository.SupplementRepository;
import com.nutrition.infrastructure.repository.UserRepository;
import com.nutrition.infrastructure.repository.UserSupplementRepository;
import com.nutrition.infrastructure.repository.UserSupplementScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplementService {

    private final SupplementRepository supplementRepository;
    private final UserSupplementRepository userSupplementRepository;
    private final UserSupplementScheduleRepository userSupplementScheduleRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SupplementResponse createSupplement(CreateSupplementRequest request) {
        try {
            User currentUser = getCurrentUser();

            // Validar categoria, forma e unidade
            Supplement.SupplementCategory category = parseSupplementCategory(request.getCategory());
            Supplement.SupplementForm form = parseSupplementForm(request.getForm());
            Supplement.ServingUnit servingUnit = parseServingUnit(request.getServingUnit());

            Supplement supplement = Supplement.builder()
                    .name(request.getName().trim())
                    .description(request.getDescription())
                    .brand(request.getBrand() != null ? request.getBrand().trim() : null)
                    .category(category)
                    .form(form)
                    .servingSize(request.getServingSize())
                    .servingUnit(servingUnit)
                    .servingsPerContainer(request.getServingsPerContainer())
                    .caloriesPerServing(request.getCaloriesPerServing())
                    .carbsPerServing(request.getCarbsPerServing())
                    .proteinPerServing(request.getProteinPerServing())
                    .fatPerServing(request.getFatPerServing())
                    .mainIngredient(request.getMainIngredient())
                    .ingredientAmount(request.getIngredientAmount())
                    .ingredientUnit(request.getIngredientUnit())
                    .recommendedDosage(request.getRecommendedDosage())
                    .usageInstructions(request.getUsageInstructions())
                    .warnings(request.getWarnings())
                    .regulatoryInfo(request.getRegulatoryInfo())
                    .active(true)
                    .createdBy(currentUser)
                    .build();

            supplement = supplementRepository.save(supplement);

            SupplementResponse response = buildSupplementResponse(supplement, null);

            log.info("Supplement created successfully: {} by user: {}", supplement.getName(), currentUser.getEmail());
            return response;

        } catch (Exception e) {
            log.error("Error creating supplement: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    /**
     * Enhanced search method with proper parameter mapping
     */
    public Page<SupplementResponse> searchSupplements(String searchTerm, String category,
                                                      String form, String brand, Boolean verified,
                                                      int page, int size) {
        try {
            User currentUser = getCurrentUserOrNull();

            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Supplement> supplements;

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                // Simple text search
                supplements = supplementRepository.searchByNameDescriptionBrandOrIngredient(searchTerm, pageable);
            } else if (hasAdvancedFilters(category, form, brand, verified)) {
                // Advanced filtered search - FIX: passing all required parameters
                supplements = supplementRepository.findByAdvancedFilters(
                        null,  // name
                        category,  // category (String)
                        form,  // form (String)
                        brand,  // brand
                        null,   // ingredient
                        null,   // servingUnit
                        null,   // hasNutritionalValue
                        pageable
                );
            } else {
                // Default: all active supplements
                supplements = supplementRepository.findByActiveTrueOrderByNameAsc(pageable);
            }

            Page<SupplementResponse> responses = supplements.map(supplement -> buildSupplementResponse(supplement, currentUser));

            log.info("Supplement search completed: {} results", supplements.getTotalElements());
            return responses;

        } catch (Exception e) {
            log.error("Error searching supplements: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    /**
     * Enhanced search with all filter parameters
     */
    public Page<SupplementResponse> searchSupplementsAdvanced(String name, String category, String form,
                                                              String brand, String ingredient,
                                                              String servingUnit, Boolean verified,
                                                              Boolean hasNutritionalValue,
                                                              int page, int size) {
        try {
            User currentUser = getCurrentUserOrNull();

            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

            Page<Supplement> supplements = supplementRepository.findByAdvancedFilters(
                    name,
                    category,  // category (String)
                    form,  // form (String)
                    brand,
                    ingredient,
                    servingUnit,  // servingUnit (String)
                    hasNutritionalValue,
                    pageable
            );

            Page<SupplementResponse> responses = supplements.map(supplement -> buildSupplementResponse(supplement, currentUser));

            log.info("Advanced supplement search completed: {} results", supplements.getTotalElements());
            return responses;

        } catch (Exception e) {
            log.error("Error in advanced supplement search: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public SupplementResponse getSupplementById(Long supplementId) {
        try {
            User currentUser = getCurrentUserOrNull();

            Supplement supplement = supplementRepository.findByIdAndActiveTrue(supplementId)
                    .orElse(null);

            if (supplement == null) {
                throw new UnprocessableEntityException("Suplemento não encontrado");
            }

            return buildSupplementResponse(supplement, currentUser);

        } catch (Exception e) {
            log.error("Error getting supplement by ID: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public Page<SupplementResponse> getSupplementsByCategory(String categoryName, int page, int size) {
        try {
            User currentUser = getCurrentUserOrNull();
            Supplement.SupplementCategory category = parseSupplementCategory(categoryName);

            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Supplement> supplements = supplementRepository.findByCategoryAndActiveTrueOrderByNameAsc(category, pageable);

            return supplements.map(supplement -> buildSupplementResponse(supplement, currentUser));
        } catch (Exception e) {
            log.error("Error getting supplements by category: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public Page<SupplementResponse> getSupplementsByForm(String formName, int page, int size) {
        try {
            User currentUser = getCurrentUserOrNull();
            Supplement.SupplementForm form = parseSupplementForm(formName);

            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Supplement> supplements = supplementRepository.findByFormAndActiveTrueOrderByNameAsc(form, pageable);

            return supplements.map(supplement -> buildSupplementResponse(supplement, currentUser));
        } catch (Exception e) {
            log.error("Error getting supplements by form: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    // ========== DEPRECATED METHODS - Removed in favor of frequency-based tracking ==========
    // Old preference-based methods are no longer needed.
    // Use the new methods: addSupplement(), updateSupplementFrequency(), removeSupplement(), etc.

    public Page<SupplementResponse> getRecommendedSupplements(int page, int size) {
        try {
            User currentUser = getCurrentUser();

            // DEPRECATED: Preference-based recommendations removed
            // Returning all active supplements instead
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Supplement> recommendations = supplementRepository.findByActiveTrueOrderByNameAsc(pageable);

            return recommendations.map(supplement -> buildSupplementResponse(supplement, currentUser));
        } catch (Exception e) {
            log.error("Error getting recommended supplements: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void verifySupplement(Long supplementId) {
        try {
            Supplement supplement = supplementRepository.findByIdAndActiveTrue(supplementId)
                    .orElse(null);

            if (supplement == null) {
                throw new UnprocessableEntityException("Suplemento não encontrado");
            }

            // Note: Verified field has been removed - all supplements are now considered verified upon creation
            log.info("Supplement verify request (no-op): {} by admin: {}", supplement.getName(), getCurrentUser().getEmail());
        } catch (Exception e) {
            log.error("Error verifying supplement: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteSupplement(Long supplementId) {
        try {
            Supplement supplement = supplementRepository.findByIdAndActiveTrue(supplementId)
                    .orElse(null);

            if (supplement == null) {
                throw new UnprocessableEntityException("Suplemento não encontrado");
            }

            // Soft delete
            supplement.setActive(false);
            supplementRepository.save(supplement);

            log.info("Supplement deleted (soft): {} by admin: {}", supplement.getName(), getCurrentUser().getEmail());
        } catch (Exception e) {
            log.error("Error deleting supplement: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    // ========== UTILITY METHODS ==========

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private User getCurrentUserOrNull() {
        try {
            return getCurrentUser();
        } catch (Exception e) {
            return null;
        }
    }

    private Supplement.SupplementCategory parseSupplementCategory(String category) {
        try {
            return Supplement.SupplementCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoria inválida: " + category +
                    ". Categorias válidas: " + String.join(", ", getValidCategories()));
        }
    }

    private Supplement.SupplementForm parseSupplementForm(String form) {
        try {
            return Supplement.SupplementForm.valueOf(form.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Forma inválida: " + form +
                    ". Formas válidas: " + String.join(", ", getValidForms()));
        }
    }

    private Supplement.ServingUnit parseServingUnit(String unit) {
        try {
            return Supplement.ServingUnit.valueOf(unit.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unidade inválida: " + unit +
                    ". Unidades válidas: " + String.join(", ", getValidServingUnits()));
        }
    }

    // DEPRECATED: parseSupplementPreferenceType removed - use frequency-based tracking instead

    private boolean hasAdvancedFilters(String category, String form, String brand, Boolean verified) {
        return category != null || form != null || brand != null || verified != null;
    }

    // Helper methods for error messages
    private String[] getValidCategories() {
        return java.util.Arrays.stream(Supplement.SupplementCategory.values())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    private String[] getValidForms() {
        return java.util.Arrays.stream(Supplement.SupplementForm.values())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    private String[] getValidServingUnits() {
        return java.util.Arrays.stream(Supplement.ServingUnit.values())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    // DEPRECATED: getValidPreferenceTypes removed - use frequency-based tracking instead

    private SupplementResponse buildSupplementResponse(Supplement supplement, User currentUser) {
        // Note: userPreference field removed - use frequency-based tracking instead
        return SupplementResponse.builder()
                .id(supplement.getId())
                .name(supplement.getName())
                .description(supplement.getDescription())
                .brand(supplement.getBrand())
                .category(supplement.getCategory().name())
                .categoryDisplay(supplement.getCategory().getDisplayName())
                .form(supplement.getForm().name())
                .formDisplay(supplement.getForm().getDisplayName())
                .servingSize(supplement.getServingSize())
                .servingUnit(supplement.getServingUnit().name())
                .servingSizeDescription(supplement.getServingSizeDescription())
                .servingsPerContainer(supplement.getServingsPerContainer())
                .caloriesPerServing(supplement.getCaloriesPerServing())
                .carbsPerServing(supplement.getCarbsPerServing())
                .proteinPerServing(supplement.getProteinPerServing())
                .fatPerServing(supplement.getFatPerServing())
                .mainIngredient(supplement.getMainIngredient())
                .ingredientAmount(supplement.getIngredientAmount())
                .ingredientUnit(supplement.getIngredientUnit())
                .mainIngredientDescription(supplement.getMainIngredientDescription())
                .recommendedDosage(supplement.getRecommendedDosage())
                .usageInstructions(supplement.getUsageInstructions())
                .warnings(supplement.getWarnings())
                .regulatoryInfo(supplement.getRegulatoryInfo())
                .displayName(supplement.getDisplayName())
                .hasNutritionalValue(supplement.hasNutritionalValue())
                .userPreference(null)  // DEPRECATED: Always null now
                .createdAt(supplement.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .updatedAt(supplement.getUpdatedAt() != null ?
                        supplement.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }


    // DEPRECATED: getUserPreferences removed - use getUserSupplements() instead for frequency-based tracking

    // ========== NEW SUPPLEMENT TRACKING METHODS (Frequency-based) ==========

    /**
     * Add a supplement to user's tracking list with frequency settings
     */
    @Transactional
    public UserSupplementResponse addSupplement(Long supplementId, AddSupplementRequest request) {
        try {
            User currentUser = getCurrentUser();

            Supplement supplement = supplementRepository.findByIdAndActiveTrue(supplementId)
                    .orElseThrow(() -> new UnprocessableEntityException("Suplemento não encontrado"));

            // Check if already exists
            if (userSupplementRepository.existsByUserAndSupplement(currentUser, supplement)) {
                throw new UnprocessableEntityException("Este suplemento já está na sua lista");
            }

            // Parse frequency
            UserSupplement.Frequency frequency = parseFrequency(request.getFrequency());

            // Create user supplement
            UserSupplement userSupplement = UserSupplement.builder()
                    .user(currentUser)
                    .supplement(supplement)
                    .frequency(frequency)
                    .notes(request.getNotes())
                    .dosageTime(request.getDosageTime() != null ?
                            java.time.LocalTime.parse(request.getDosageTime()) : null)
                    .daysOfWeek(request.getDaysOfWeek())
                    .emailReminderEnabled(request.getEmailReminderEnabled())
                    .build();

            userSupplement = userSupplementRepository.save(userSupplement);

            log.info("Supplement added to tracking: {} for user {} with frequency {}",
                    supplement.getName(), currentUser.getEmail(), frequency);

            return buildUserSupplementResponse(userSupplement);

        } catch (UnprocessableEntityException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error adding supplement to tracking: {}", e.getMessage(), e);
            throw new UnprocessableEntityException("Erro ao adicionar suplemento");
        }
    }

    /**
     * Update supplement frequency and reminder settings
     */
    @Transactional
    public UserSupplementResponse updateSupplementFrequency(Long supplementId, UpdateSupplementFrequencyRequest request) {
        try {
            User currentUser = getCurrentUser();

            Supplement supplement = supplementRepository.findByIdAndActiveTrue(supplementId)
                    .orElseThrow(() -> new UnprocessableEntityException("Suplemento não encontrado"));

            UserSupplement userSupplement = userSupplementRepository.findByUserAndSupplement(currentUser, supplement)
                    .orElseThrow(() -> new UnprocessableEntityException(
                            "Suplemento não está na sua lista de acompanhamento"));

            // Update fields
            userSupplement.setFrequency(parseFrequency(request.getFrequency()));
            userSupplement.setNotes(request.getNotes());
            userSupplement.setDosageTime(request.getDosageTime() != null ?
                    java.time.LocalTime.parse(request.getDosageTime()) : null);
            userSupplement.setDaysOfWeek(request.getDaysOfWeek());
            userSupplement.setEmailReminderEnabled(request.getEmailReminderEnabled());

            userSupplement = userSupplementRepository.save(userSupplement);

            log.info("Supplement frequency updated: {} for user {}",
                    supplement.getName(), currentUser.getEmail());

            return buildUserSupplementResponse(userSupplement);

        } catch (UnprocessableEntityException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating supplement frequency: {}", e.getMessage(), e);
            throw new UnprocessableEntityException("Erro ao atualizar frequência do suplemento");
        }
    }

    /**
     * Remove supplement from tracking
     */
    @Transactional
    public void removeSupplement(Long supplementId) {
        try {
            User currentUser = getCurrentUser();

            Supplement supplement = supplementRepository.findByIdAndActiveTrue(supplementId)
                    .orElseThrow(() -> new UnprocessableEntityException("Suplemento não encontrado"));

            userSupplementRepository.deleteByUserAndSupplement(currentUser, supplement);

            log.info("Supplement removed from tracking: {} by user {}",
                    supplement.getName(), currentUser.getEmail());

        } catch (UnprocessableEntityException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error removing supplement from tracking: {}", e.getMessage(), e);
            throw new UnprocessableEntityException("Erro ao remover suplemento");
        }
    }

    /**
     * Mark supplement as taken (updates lastTakenAt timestamp)
     */
    @Transactional
    public UserSupplementResponse markSupplementAsTaken(Long supplementId) {
        try {
            User currentUser = getCurrentUser();

            Supplement supplement = supplementRepository.findByIdAndActiveTrue(supplementId)
                    .orElseThrow(() -> new UnprocessableEntityException("Suplemento não encontrado"));

            UserSupplement userSupplement = userSupplementRepository.findByUserAndSupplement(currentUser, supplement)
                    .orElseThrow(() -> new UnprocessableEntityException(
                            "Suplemento não está na sua lista de acompanhamento"));

            userSupplement.markAsTaken();
            userSupplement = userSupplementRepository.save(userSupplement);

            log.info("Supplement marked as taken: {} by user {}",
                    supplement.getName(), currentUser.getEmail());

            return buildUserSupplementResponse(userSupplement);

        } catch (UnprocessableEntityException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error marking supplement as taken: {}", e.getMessage(), e);
            throw new UnprocessableEntityException("Erro ao marcar suplemento como tomado");
        }
    }

    /**
     * Get all user's tracked supplements
     */
    public List<UserSupplementResponse> getUserSupplements() {
        try {
            User currentUser = getCurrentUser();

            List<UserSupplement> userSupplements = userSupplementRepository.findByUserWithSupplementDetails(currentUser);

            return userSupplements.stream()
                    .map(this::buildUserSupplementResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting user supplements: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro ao buscar suplementos");
        }
    }

    /**
     * Get user's supplements filtered by frequency
     */
    public List<UserSupplementResponse> getUserSupplementsByFrequency(String frequencyStr) {
        try {
            User currentUser = getCurrentUser();
            UserSupplement.Frequency frequency = parseFrequency(frequencyStr);

            List<UserSupplement> userSupplements = userSupplementRepository
                    .findByUserAndFrequencyWithDetails(currentUser, frequency);

            return userSupplements.stream()
                    .map(this::buildUserSupplementResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting user supplements by frequency: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro ao buscar suplementos por frequência");
        }
    }

    // ========== NEW HELPER METHODS ==========

    private UserSupplement.Frequency parseFrequency(String frequency) {
        try {
            return UserSupplement.Frequency.valueOf(frequency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Frequência inválida: " + frequency +
                    ". Frequências válidas: DAILY, WEEKLY, TWICE_WEEKLY, THREE_TIMES_WEEKLY, MONTHLY");
        }
    }

    private UserSupplementResponse buildUserSupplementResponse(UserSupplement userSupplement) {
        Supplement supplement = userSupplement.getSupplement();

        SupplementResponse supplementResponse = buildSupplementResponse(supplement, userSupplement.getUser());

        return UserSupplementResponse.builder()
                .id(userSupplement.getId())
                .supplement(supplementResponse)
                .frequency(userSupplement.getFrequency().name())
                .frequencyDisplay(userSupplement.getFrequency().getDisplayName())
                .notes(userSupplement.getNotes())
                .dosageTime(userSupplement.getDosageTime() != null ?
                        userSupplement.getDosageTime().toString() : null)
                .daysOfWeek(userSupplement.getDaysOfWeek())
                .emailReminderEnabled(userSupplement.getEmailReminderEnabled())
                .lastTakenAt(userSupplement.getLastTakenAt())
                .createdAt(userSupplement.getCreatedAt())
                .updatedAt(userSupplement.getUpdatedAt())
                .build();
    }

    // ========== SCHEDULE MANAGEMENT METHODS ==========

    /**
     * Add a new dosage time/schedule to a user supplement
     */
    @Transactional
    public ScheduleResponse addSchedule(Long userSupplementId, AddScheduleRequest request) {
        try {
            User currentUser = getCurrentUser();

            // Find the user supplement and verify ownership
            UserSupplement userSupplement = userSupplementRepository.findById(userSupplementId)
                    .orElseThrow(() -> new UnprocessableEntityException("Suplemento não encontrado"));

            if (!userSupplement.getUser().getId().equals(currentUser.getId())) {
                throw new UnprocessableEntityException("Você não tem permissão para adicionar horários a este suplemento");
            }

            // Parse dosage time
            java.time.LocalTime dosageTime = java.time.LocalTime.parse(request.getDosageTime());

            // Create and save the schedule
            UserSupplementSchedule schedule = UserSupplementSchedule.builder()
                    .userSupplement(userSupplement)
                    .dosageTime(dosageTime)
                    .label(request.getLabel())
                    .build();

            schedule = userSupplementScheduleRepository.save(schedule);

            log.info("Schedule added for user supplement ID {}: {}", userSupplementId, request.getDosageTime());

            return buildScheduleResponse(schedule);

        } catch (Exception e) {
            log.error("Error adding schedule: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro ao adicionar horário de dosagem");
        }
    }

    /**
     * Get all schedules for a user supplement
     */
    public List<ScheduleResponse> getSchedules(Long userSupplementId) {
        try {
            User currentUser = getCurrentUser();

            // Find the user supplement and verify ownership
            UserSupplement userSupplement = userSupplementRepository.findById(userSupplementId)
                    .orElseThrow(() -> new UnprocessableEntityException("Suplemento não encontrado"));

            if (!userSupplement.getUser().getId().equals(currentUser.getId())) {
                throw new UnprocessableEntityException("Você não tem permissão para ver os horários deste suplemento");
            }

            List<UserSupplementSchedule> schedules = userSupplementScheduleRepository
                    .findByUserSupplementId(userSupplementId);

            return schedules.stream()
                    .map(this::buildScheduleResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting schedules: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro ao buscar horários de dosagem");
        }
    }

    /**
     * Update a specific schedule
     */
    @Transactional
    public ScheduleResponse updateSchedule(Long userSupplementId, Long scheduleId, UpdateScheduleRequest request) {
        try {
            User currentUser = getCurrentUser();

            // Find the user supplement and verify ownership
            UserSupplement userSupplement = userSupplementRepository.findById(userSupplementId)
                    .orElseThrow(() -> new UnprocessableEntityException("Suplemento não encontrado"));

            if (!userSupplement.getUser().getId().equals(currentUser.getId())) {
                throw new UnprocessableEntityException("Você não tem permissão para atualizar horários deste suplemento");
            }

            // Find the schedule
            UserSupplementSchedule schedule = userSupplementScheduleRepository
                    .findByIdAndUserSupplementId(scheduleId, userSupplementId)
                    .orElseThrow(() -> new UnprocessableEntityException("Horário de dosagem não encontrado"));

            // Update fields if provided
            if (request.getDosageTime() != null) {
                schedule.setDosageTime(java.time.LocalTime.parse(request.getDosageTime()));
            }
            if (request.getLabel() != null) {
                schedule.setLabel(request.getLabel());
            }

            schedule = userSupplementScheduleRepository.save(schedule);

            log.info("Schedule updated for user supplement ID {}: schedule ID {}", userSupplementId, scheduleId);

            return buildScheduleResponse(schedule);

        } catch (Exception e) {
            log.error("Error updating schedule: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro ao atualizar horário de dosagem");
        }
    }

    /**
     * Remove a specific schedule
     */
    @Transactional
    public void removeSchedule(Long userSupplementId, Long scheduleId) {
        try {
            User currentUser = getCurrentUser();

            // Find the user supplement and verify ownership
            UserSupplement userSupplement = userSupplementRepository.findById(userSupplementId)
                    .orElseThrow(() -> new UnprocessableEntityException("Suplemento não encontrado"));

            if (!userSupplement.getUser().getId().equals(currentUser.getId())) {
                throw new UnprocessableEntityException("Você não tem permissão para remover horários deste suplemento");
            }

            // Find and delete the schedule
            UserSupplementSchedule schedule = userSupplementScheduleRepository
                    .findByIdAndUserSupplementId(scheduleId, userSupplementId)
                    .orElseThrow(() -> new UnprocessableEntityException("Horário de dosagem não encontrado"));

            userSupplementScheduleRepository.delete(schedule);

            log.info("Schedule removed for user supplement ID {}: schedule ID {}", userSupplementId, scheduleId);

        } catch (Exception e) {
            log.error("Error removing schedule: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro ao remover horário de dosagem");
        }
    }

    private ScheduleResponse buildScheduleResponse(UserSupplementSchedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .dosageTime(schedule.getDosageTime().toString())
                .label(schedule.getLabel())
                .createdAt(schedule.getCreatedAt())
                .build();
    }
}