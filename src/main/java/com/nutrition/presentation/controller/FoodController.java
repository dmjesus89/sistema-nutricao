package com.nutrition.presentation.controller;

import com.nutrition.application.dto.food.CreateFoodRequest;
import com.nutrition.application.dto.food.FoodResponse;
import com.nutrition.application.dto.food.FoodSearchRequest;
import com.nutrition.application.dto.food.UpdateFoodRequest;
import com.nutrition.application.dto.food.UserPreferenceRequest;
import com.nutrition.application.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Foods", description = "Endpoints para gestão de alimentos")
@SecurityRequirement(name = "Bearer Authentication")
public class FoodController {

    private final FoodService foodService;

    @PostMapping
    @Operation(summary = "Criar alimento", description = "Cria um novo alimento (apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FoodResponse> createFood(
            @Valid @RequestBody CreateFoodRequest request) {
        log.info("Food creation request received");
        FoodResponse response = foodService.createFood(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar alimento", description = "Atualiza um alimento existente (apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FoodResponse> updateFood(@Parameter(description = "ID do alimento") @PathVariable Long id, @Valid @RequestBody UpdateFoodRequest request) {
        log.info("Food update request received for ID: {}", id);
        FoodResponse response = foodService.updateFood(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar alimentos", description = "Busca alimentos com filtros opcionais")
    public ResponseEntity<Page<FoodResponse>> searchFoods(
            @ModelAttribute FoodSearchRequest searchRequest,
            @Parameter(description = "Número da página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Food search request received");
        Page<FoodResponse> response = foodService.searchFoods(searchRequest, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter alimento por ID", description = "Retorna detalhes de um alimento específico")
    public ResponseEntity<FoodResponse> getFoodById(@Parameter(description = "ID do alimento") @PathVariable Long id) {
        log.info("Get food by ID request: {}", id);
        FoodResponse response = foodService.getFoodById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Alimentos por categoria", description = "Lista alimentos de uma categoria específica")
    public ResponseEntity<Page<FoodResponse>> getFoodsByCategory(
            @Parameter(description = "Nome da categoria") @PathVariable String category,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Get foods by category request: {}", category);
        Page<FoodResponse> response = foodService.getFoodsByCategory(category, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/preference")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Definir preferência", description = "Define preferência do usuário para um alimento")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void setFoodPreference(
            @Parameter(description = "ID do alimento") @PathVariable Long id,
            @Valid @RequestBody UserPreferenceRequest request) {
        log.info("Set food preference request for food ID: {}", id);
        foodService.setFoodPreference(id, request);
    }

    @DeleteMapping("/{id}/preference")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover preferência", description = "Remove preferência do usuário para um alimento")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void removeFoodPreference(@Parameter(description = "ID do alimento") @PathVariable Long id) {
        log.info("Remove food preference request for food ID: {}", id);
        foodService.removeFoodPreference(id);
    }

    @GetMapping("/favorites")
    @Operation(summary = "Alimentos favoritos", description = "Lista alimentos favoritos do usuário atual")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FoodResponse>> getUserFavorites() {
        log.info("Get user favorites request");
        List<FoodResponse> response = foodService.getUserFavorites();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/preferences")
    @Operation(summary = "Alimentos com preferencias ou não", description = "Lista alimentos com preferencias do usuário atual")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FoodResponse>> getUserPreferences() {
        log.info("Get user preferences request");
        List<FoodResponse> response = foodService.getUserPreferences();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommended")
    @Operation(summary = "Alimentos recomendados", description = "Lista alimentos recomendados baseados nas preferências do usuário")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<FoodResponse>> getRecommendedFoods(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Get recommended foods request");
        Page<FoodResponse> response = foodService.getRecommendedFoods(page, size);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Verificar alimento", description = "Marca um alimento como verificado (apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public void verifyFood(@Parameter(description = "ID do alimento") @PathVariable Long id) {
        log.info("Verify food request for ID: {}", id);
        foodService.verifyFood(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover alimento", description = "Remove um alimento (soft delete - apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFood(@Parameter(description = "ID do alimento") @PathVariable Long id) {
        log.info("Delete food request for ID: {}", id);
        foodService.deleteFood(id);
    }
}