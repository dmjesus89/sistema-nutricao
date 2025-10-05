package com.nutrition.application.service;

import com.nutrition.application.dto.food.CreateSupplementRequest;
import com.nutrition.application.dto.food.SupplementResponse;
import com.nutrition.application.dto.food.UserPreferenceRequest;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.Supplement;
import com.nutrition.domain.entity.food.UserSupplementPreference;
import com.nutrition.infrastructure.exception.UnprocessableEntityException;
import com.nutrition.infrastructure.repository.SupplementRepository;
import com.nutrition.infrastructure.repository.UserRepository;
import com.nutrition.infrastructure.repository.UserSupplementPreferenceRepository;
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
    private final UserSupplementPreferenceRepository preferenceRepository;
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
                    .verified(false) // Admin needs to verify separately
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
                        category != null ? parseSupplementCategory(category) : null,  // category
                        form != null ? parseSupplementForm(form) : null,  // form
                        brand,  // brand
                        null,   // ingredient
                        null,   // servingUnit
                        verified,  // verified
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
                    category != null ? parseSupplementCategory(category) : null,
                    form != null ? parseSupplementForm(form) : null,
                    brand,
                    ingredient,
                    servingUnit != null ? parseServingUnit(servingUnit) : null,
                    verified,
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

    @Transactional
    public void setSupplementPreference(Long supplementId, UserPreferenceRequest request) {
        try {
            User currentUser = getCurrentUser();

            Supplement supplement = supplementRepository.findByIdAndActiveTrue(supplementId)
                    .orElse(null);

            if (supplement == null) {
                throw new UnprocessableEntityException("Suplemento não encontrado");
            }

            UserSupplementPreference.PreferenceType preferenceType = parseSupplementPreferenceType(request.getPreferenceType());

            // Verificar se já existe preferência
            UserSupplementPreference existingPreference = preferenceRepository.findByUserAndSupplement(currentUser, supplement)
                    .orElse(null);

            if (existingPreference != null) {
                // Atualizar preferência existente
                existingPreference.setPreferenceType(preferenceType);
                existingPreference.setNotes(request.getNotes());
                preferenceRepository.save(existingPreference);

                log.info("Supplement preference updated: {} for supplement {} by user {}",
                        preferenceType, supplement.getName(), currentUser.getEmail());
            } else {
                // Criar nova preferência
                UserSupplementPreference newPreference = UserSupplementPreference.builder()
                        .user(currentUser)
                        .supplement(supplement)
                        .preferenceType(preferenceType)
                        .notes(request.getNotes())
                        .build();

                preferenceRepository.save(newPreference);

                log.info("Supplement preference created: {} for supplement {} by user {}",
                        preferenceType, supplement.getName(), currentUser.getEmail());
            }

        } catch (IllegalArgumentException e) {
            throw new UnprocessableEntityException(e.getMessage());
        } catch (Exception e) {
            log.error("Error setting supplement preference: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    @Transactional
    public void removeSupplementPreference(Long supplementId) {
        try {
            User currentUser = getCurrentUser();

            Supplement supplement = supplementRepository.findByIdAndActiveTrue(supplementId)
                    .orElse(null);

            if (supplement == null) {
                throw new UnprocessableEntityException("Suplemento não encontrado");
            }

            preferenceRepository.deleteByUserAndSupplement(currentUser, supplement);

            log.info("Supplement preference removed for supplement {} by user {}",
                    supplement.getName(), currentUser.getEmail());

        } catch (Exception e) {
            log.error("Error removing supplement preference: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public List<SupplementResponse> getUserFavorites() {
        try {
            User currentUser = getCurrentUser();

            List<Supplement> favorites = supplementRepository.findUserFavorites(currentUser);
            return favorites.stream()
                    .map(supplement -> buildSupplementResponse(supplement, currentUser))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting user favorites: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public List<SupplementResponse> getCurrentSupplements() {
        try {
            User currentUser = getCurrentUser();

            List<Supplement> currentSupplements = supplementRepository.findUserCurrentSupplements(currentUser);
            return currentSupplements.stream()
                    .map(supplement -> buildSupplementResponse(supplement, currentUser))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting current supplements: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public List<SupplementResponse> getUserWishlist() {
        try {
            User currentUser = getCurrentUser();

            List<Supplement> wishlist = supplementRepository.findUserWishlistSupplements(currentUser);
            return wishlist.stream()
                    .map(supplement -> buildSupplementResponse(supplement, currentUser))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting user wishlist: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public Page<SupplementResponse> getRecommendedSupplements(int page, int size) {
        try {
            User currentUser = getCurrentUser();

            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Supplement> recommendations = supplementRepository.findRecommendedSupplementsForUser(currentUser, pageable);

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

            supplement.setVerified(true);
            supplementRepository.save(supplement);

            log.info("Supplement verified: {} by admin: {}", supplement.getName(), getCurrentUser().getEmail());
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

    private UserSupplementPreference.PreferenceType parseSupplementPreferenceType(String preferenceType) {
        try {
            return UserSupplementPreference.PreferenceType.valueOf(preferenceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de preferência inválido: " + preferenceType +
                    ". Tipos válidos: " + String.join(", ", getValidPreferenceTypes()));
        }
    }

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

    private String[] getValidPreferenceTypes() {
        return java.util.Arrays.stream(UserSupplementPreference.PreferenceType.values())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    private SupplementResponse buildSupplementResponse(Supplement supplement, User currentUser) {
        // Buscar preferência do usuário se logado
        String userPreference = null;
        if (currentUser != null) {
            UserSupplementPreference preference = preferenceRepository.findByUserAndSupplement(currentUser, supplement)
                    .orElse(null);
            if (preference != null) {
                userPreference = preference.getPreferenceType().name();
            }
        }

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
                .verified(supplement.getVerified())
                .displayName(supplement.getDisplayName())
                .hasNutritionalValue(supplement.hasNutritionalValue())
                .userPreference(userPreference)
                .createdAt(supplement.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .updatedAt(supplement.getUpdatedAt() != null ?
                        supplement.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }


    public List<SupplementResponse> getUserPreferences() {
        try {
            User currentUser = getCurrentUser();

            List<Supplement> preferences = supplementRepository.findUserWithPreferences(currentUser);
            return preferences.stream()
                    .map(preference -> buildSupplementResponse(preference, currentUser))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting user preferences: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }
}