package com.nutrition.application.service;

import com.nutrition.application.dto.food.CreateFoodRequest;
import com.nutrition.application.dto.food.FoodResponse;
import com.nutrition.application.dto.food.FoodSearchRequest;
import com.nutrition.application.dto.food.UpdateFoodRequest;
import com.nutrition.application.dto.food.UserPreferenceRequest;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.Food;
import com.nutrition.domain.entity.food.UserFoodPreference;
import com.nutrition.infrastructure.exception.UnprocessableEntityException;
import com.nutrition.infrastructure.repository.FoodRepository;
import com.nutrition.infrastructure.repository.UserFoodPreferenceRepository;
import com.nutrition.infrastructure.repository.UserRepository;
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
public class FoodService {

    private final FoodRepository foodRepository;
    private final UserFoodPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public FoodResponse createFood(CreateFoodRequest request) {
        try {
            User currentUser = getCurrentUser();

            // Verificar se já existe alimento com mesmo código de barras
            if (request.getBarcode() != null && !request.getBarcode().trim().isEmpty()) {
                if (foodRepository.findByBarcodeAndActiveTrue(request.getBarcode()).isPresent()) {
                    throw new UnprocessableEntityException("Já existe um alimento com este código de barras");
                }
            }

            // Validar categoria
            Food.FoodCategory category = parseFoodCategory(request.getCategory());

            Food food = Food.builder()
                    .name(request.getName().trim())
                    .description(request.getDescription())
                    .brand(request.getBrand() != null ? request.getBrand().trim() : null)
                    .category(category)
                    .barcode(request.getBarcode())
                    .caloriesPer100g(request.getCaloriesPer100g())
                    .carbsPer100g(request.getCarbsPer100g())
                    .proteinPer100g(request.getProteinPer100g())
                    .fatPer100g(request.getFatPer100g())
                    .fiberPer100g(request.getFiberPer100g())
                    .sugarPer100g(request.getSugarPer100g())
                    .sodiumPer100g(request.getSodiumPer100g())
                    .saturatedFatPer100g(request.getSaturatedFatPer100g())
                    .servingSize(request.getServingSize())
                    .servingDescription(request.getServingDescription())
                    .source(request.getSource() != null ? request.getSource() : "Manual")
                    .active(true)
                    .createdBy(currentUser)
                    .build();

            food = foodRepository.save(food);

            FoodResponse response = buildFoodResponse(food, null);

            log.info("Food created successfully: {} by user: {}", food.getName(), currentUser.getEmail());
            return response;

        } catch (IllegalArgumentException e) {
            log.warn("Invalid data in food creation: {}", e.getMessage());
            throw new UnprocessableEntityException(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating food: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public FoodResponse updateFood(Long foodId, UpdateFoodRequest request) {
        try {
            Food food = foodRepository.findByIdAndActiveTrue(foodId)
                    .orElse(null);

            if (food == null) {
                throw new UnprocessableEntityException("Alimento não encontrado");
            }

            // Atualizar campos se fornecidos
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                food.setName(request.getName().trim());
            }

            if (request.getDescription() != null) {
                food.setDescription(request.getDescription());
            }

            if (request.getBrand() != null) {
                food.setBrand(request.getBrand().trim());
            }

            if (request.getCategory() != null) {
                food.setCategory(parseFoodCategory(request.getCategory()));
            }

            if (request.getBarcode() != null) {
                // Verificar se já existe outro alimento com este código
                foodRepository.findByBarcodeAndActiveTrue(request.getBarcode())
                        .ifPresent(existingFood -> {
                            if (!existingFood.getId().equals(foodId)) {
                                throw new IllegalArgumentException("Já existe outro alimento com este código de barras");
                            }
                        });
                food.setBarcode(request.getBarcode());
            }

            // Atualizar informações nutricionais
            if (request.getCaloriesPer100g() != null) {
                food.setCaloriesPer100g(request.getCaloriesPer100g());
            }
            if (request.getCarbsPer100g() != null) {
                food.setCarbsPer100g(request.getCarbsPer100g());
            }
            if (request.getProteinPer100g() != null) {
                food.setProteinPer100g(request.getProteinPer100g());
            }
            if (request.getFatPer100g() != null) {
                food.setFatPer100g(request.getFatPer100g());
            }

            food = foodRepository.save(food);

            FoodResponse response = buildFoodResponse(food, null);

            log.info("Food updated successfully: {} by user: {}", food.getName(), getCurrentUser().getEmail());
            return response;

        } catch (IllegalArgumentException e) {
            log.warn("Invalid data in food update: {}", e.getMessage());
            throw new UnprocessableEntityException(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating food: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public Page<FoodResponse> searchFoods(FoodSearchRequest searchRequest, int page, int size) {
        try {
            User currentUser = getCurrentUserOrNull();

            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Food> foods;

            // Se tem critérios de busca, usar busca avançada
            if (hasSearchCriteria(searchRequest)) {
                foods = foodRepository.findByAdvancedFilters(
                        searchRequest.getName(),
                        searchRequest.getCategory(),
                        searchRequest.getBrand(),
                        searchRequest.getMinCalories(),
                        searchRequest.getMaxCalories(),
                        searchRequest.getMinProtein(),
                        searchRequest.getMaxCarbs(),
                        searchRequest.getMaxFat(),
                        searchRequest.getMinFiber(),
                        searchRequest.getMaxSodium(),
                        null, // barcode not used for search
                        searchRequest.getVerifiedOnly(),
                        pageable
                );
            } else {
                // Busca geral
                foods = foodRepository.findByActiveTrueOrderByNameAsc(pageable);
            }

            // Apply post-filters for boolean flags (highProtein, lowCarb, highFiber)
            List<Food> filteredList = foods.getContent().stream()
                    .filter(food -> applyBooleanFilters(food, searchRequest))
                    .filter(food -> applyUserPreferenceFilter(food, currentUser, searchRequest))
                    .collect(java.util.stream.Collectors.toList());

            // Convert filtered list back to Page
            Page<Food> filteredFoods = new org.springframework.data.domain.PageImpl<>(
                    filteredList,
                    pageable,
                    filteredList.size()
            );

            Page<FoodResponse> responses = filteredFoods.map(food -> buildFoodResponse(food, currentUser));

            log.info("Food search completed: {} results", responses.getTotalElements());
            return responses;

        } catch (IllegalArgumentException e) {
            log.warn("Invalid search parameters: {}", e.getMessage());
            throw new UnprocessableEntityException(e.getMessage());
        } catch (Exception e) {
            log.error("Error searching foods: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    /**
     * Apply boolean filters (highProtein, lowCarb, highFiber)
     */
    private boolean applyBooleanFilters(Food food, FoodSearchRequest request) {
        if (Boolean.TRUE.equals(request.getHighProtein()) && !food.isHighProtein()) {
            return false;
        }
        if (Boolean.TRUE.equals(request.getLowCarb()) && !food.isLowCarb()) {
            return false;
        }
        if (Boolean.TRUE.equals(request.getHighFiber()) && !food.isHighFiber()) {
            return false;
        }
        return true;
    }

    /**
     * Apply user preference filter (FAVORITE, DISLIKED, etc.)
     */
    private boolean applyUserPreferenceFilter(Food food, User currentUser, FoodSearchRequest request) {
        if (request.getUserPreference() == null || request.getUserPreference().isEmpty()) {
            return true; // No filter applied
        }

        if (currentUser == null) {
            return false; // Can't filter by preference if not logged in
        }

        // Get user's preference for this food
        return preferenceRepository.findByUserAndFood(currentUser, food)
                .map(pref -> pref.getPreferenceType().name().equals(request.getUserPreference()))
                .orElse(false);
    }

    public FoodResponse getFoodById(Long foodId) {
        try {
            User currentUser = getCurrentUserOrNull();

            Food food = foodRepository.findByIdAndActiveTrue(foodId)
                    .orElse(null);

            if (food == null) {
                throw new UnprocessableEntityException("Alimento não encontrado");
            }

            return buildFoodResponse(food, currentUser);
        } catch (Exception e) {
            log.error("Error getting food by ID: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public Page<FoodResponse> getFoodsByCategory(String categoryName, int page, int size) {
        try {
            User currentUser = getCurrentUserOrNull();
            Food.FoodCategory category = parseFoodCategory(categoryName);

            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Food> foods = foodRepository.findByCategoryAndActiveTrueOrderByNameAsc(category, pageable);

            return foods.map(food -> buildFoodResponse(food, currentUser));
        } catch (IllegalArgumentException e) {
            throw new UnprocessableEntityException("Categoria inválida: " + categoryName);
        } catch (Exception e) {
            log.error("Error getting foods by category: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    @Transactional
    public void setFoodPreference(Long foodId, UserPreferenceRequest request) {
        try {
            User currentUser = getCurrentUser();

            Food food = foodRepository.findByIdAndActiveTrue(foodId)
                    .orElse(null);

            if (food == null) {
                throw new UnprocessableEntityException("Alimento não encontrado");
            }

            UserFoodPreference.PreferenceType preferenceType = parsePreferenceType(request.getPreferenceType());

            // Verificar se já existe preferência
            UserFoodPreference existingPreference = preferenceRepository.findByUserAndFood(currentUser, food)
                    .orElse(null);

            if (existingPreference != null) {
                // Atualizar preferência existente
                existingPreference.setPreferenceType(preferenceType);
                existingPreference.setNotes(request.getNotes());
                preferenceRepository.save(existingPreference);

                log.info("Food preference updated: {} for food {} by user {}",
                        preferenceType, food.getName(), currentUser.getEmail());
            } else {
                // Criar nova preferência
                UserFoodPreference newPreference = UserFoodPreference.builder()
                        .user(currentUser)
                        .food(food)
                        .preferenceType(preferenceType)
                        .notes(request.getNotes())
                        .build();

                preferenceRepository.save(newPreference);

                log.info("Food preference created: {} for food {} by user {}",
                        preferenceType, food.getName(), currentUser.getEmail());
            }
        } catch (Exception e) {
            log.error("Error setting food preference: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    @Transactional
    public void removeFoodPreference(Long foodId) {
        try {
            User currentUser = getCurrentUser();

            Food food = foodRepository.findByIdAndActiveTrue(foodId)
                    .orElse(null);

            if (food == null) {
                throw new UnprocessableEntityException("Alimento não encontrado");
            }

            preferenceRepository.deleteByUserAndFood(currentUser, food);

            log.info("Food preference removed for food {} by user {}",
                    food.getName(), currentUser.getEmail());
        } catch (Exception e) {
            log.error("Error removing food preference: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public List<FoodResponse> getUserFavorites() {
        try {
            User currentUser = getCurrentUser();

            List<Food> favorites = foodRepository.findUserFavorites(currentUser);
            return favorites.stream()
                    .map(food -> buildFoodResponse(food, currentUser))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting user favorites: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public List<FoodResponse> getUserPreferences() {
        try {
            User currentUser = getCurrentUser();

            List<Food> favorites = foodRepository.findUserWithPreferences(currentUser);
            return favorites.stream()
                    .map(food -> buildFoodResponse(food, currentUser))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting user preferences: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    public Page<FoodResponse> getRecommendedFoods(int page, int size) {
        try {
            User currentUser = getCurrentUser();

            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Food> recommendations = foodRepository.findSuitableFoodsForUser(currentUser, pageable);

            return recommendations.map(food -> buildFoodResponse(food, currentUser));
        } catch (Exception e) {
            log.error("Error getting recommended foods: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void verifyFood(Long foodId) {
        try {
            Food food = foodRepository.findByIdAndActiveTrue(foodId)
                    .orElse(null);

            if (food == null) {
                throw new UnprocessableEntityException("Alimento não encontrado");
            }

            // Note: Verified field has been removed - all foods are now considered verified upon creation
            log.info("Food verify request (no-op): {} by admin: {}", food.getName(), getCurrentUser().getEmail());
        } catch (Exception e) {
            log.error("Error verifying food: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteFood(Long foodId) {
        try {
            Food food = foodRepository.findByIdAndActiveTrue(foodId)
                    .orElse(null);

            if (food == null) {
                throw new UnprocessableEntityException("Alimento não encontrado");
            }

            // Soft delete
            food.setActive(false);
            foodRepository.save(food);

            log.info("Food deleted (soft): {} by admin: {}", food.getName(), getCurrentUser().getEmail());
        } catch (Exception e) {
            log.error("Error deleting food: {}", e.getMessage());
            throw new UnprocessableEntityException("Erro interno do servidor");
        }
    }

    // Métodos auxiliares

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

    private Food.FoodCategory parseFoodCategory(String category) {
        try {
            return Food.FoodCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoria inválida: " + category);
        }
    }

    private UserFoodPreference.PreferenceType parsePreferenceType(String preferenceType) {
        try {
            return UserFoodPreference.PreferenceType.valueOf(preferenceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de preferência inválido: " + preferenceType);
        }
    }

    private boolean hasSearchCriteria(FoodSearchRequest request) {
        return request.getName() != null ||
                request.getCategory() != null ||
                request.getBrand() != null ||
                request.getMinCalories() != null ||
                request.getMaxCalories() != null ||
                request.getMinProtein() != null ||
                request.getMaxCarbs() != null ||
                request.getMaxFat() != null ||
                request.getMinFiber() != null ||
                request.getMaxSodium() != null ||
                Boolean.TRUE.equals(request.getVerifiedOnly()) ||
                Boolean.TRUE.equals(request.getHighProtein()) ||
                Boolean.TRUE.equals(request.getLowCarb()) ||
                Boolean.TRUE.equals(request.getHighFiber()) ||
                (request.getUserPreference() != null && !request.getUserPreference().isEmpty());
    }

    private FoodResponse buildFoodResponse(Food food, User currentUser) {
        // Buscar preferência do usuário se logado
        String userPreference = null;
        if (currentUser != null) {
            UserFoodPreference preference = preferenceRepository.findByUserAndFood(currentUser, food).orElse(null);
            if (preference != null) {
                userPreference = preference.getPreferenceType().name();
            }
        }

        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .brand(food.getBrand())
                .category(food.getCategory().name())
                .categoryDisplay(food.getCategory().getDisplayName())
                .barcode(food.getBarcode())
                .caloriesPer100g(food.getCaloriesPer100g())
                .carbsPer100g(food.getCarbsPer100g())
                .proteinPer100g(food.getProteinPer100g())
                .fatPer100g(food.getFatPer100g())
                .fiberPer100g(food.getFiberPer100g())
                .sugarPer100g(food.getSugarPer100g())
                .sodiumPer100g(food.getSodiumPer100g())
                .saturatedFatPer100g(food.getSaturatedFatPer100g())
                .quantityEquivalence(food.getQuantityEquivalence())
                .servingSize(food.getServingSize())
                .servingDescription(food.getServingDescription())
                .caloriesPerServing(food.getCaloriesPerServing())
                .carbsPerServing(food.getCarbsPerServing())
                .proteinPerServing(food.getProteinPerServing())
                .fatPerServing(food.getFatPerServing())
                .source(food.getSource())
                .displayName(food.getDisplayName())
                .isHighProtein(food.isHighProtein())
                .isHighFiber(food.isHighFiber())
                .isLowSodium(food.isLowSodium())
                .userPreference(userPreference)
                .createdAt(food.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .updatedAt(food.getUpdatedAt() != null ?
                        food.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }
}