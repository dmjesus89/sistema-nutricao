package com.nutrition.presentation.controller;

import com.nutrition.application.dto.auth.ApiResponse;
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
    public ResponseEntity<ApiResponse<FoodResponse>> createFood(
            @Valid @RequestBody CreateFoodRequest request) {
        log.info("Food creation request received");
        ApiResponse<FoodResponse> response = foodService.createFood(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar alimento", description = "Atualiza um alimento existente (apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FoodResponse>> updateFood(
            @Parameter(description = "ID do alimento") @PathVariable Long id,
            @Valid @RequestBody UpdateFoodRequest request) {
        log.info("Food update request received for ID: {}", id);
        ApiResponse<FoodResponse> response = foodService.updateFood(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar alimentos", description = "Busca alimentos com filtros opcionais")
    public ResponseEntity<ApiResponse<Page<FoodResponse>>> searchFoods(
            @ModelAttribute FoodSearchRequest searchRequest,
            @Parameter(description = "Número da página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Food search request received");
        ApiResponse<Page<FoodResponse>> response = foodService.searchFoods(searchRequest, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter alimento por ID", description = "Retorna detalhes de um alimento específico")
    public ResponseEntity<ApiResponse<FoodResponse>> getFoodById(
            @Parameter(description = "ID do alimento") @PathVariable Long id) {
        log.info("Get food by ID request: {}", id);
        ApiResponse<FoodResponse> response = foodService.getFoodById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Alimentos por categoria", description = "Lista alimentos de uma categoria específica")
    public ResponseEntity<ApiResponse<Page<FoodResponse>>> getFoodsByCategory(
            @Parameter(description = "Nome da categoria") @PathVariable String category,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Get foods by category request: {}", category);
        ApiResponse<Page<FoodResponse>> response = foodService.getFoodsByCategory(category, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/preference")
    @Operation(summary = "Definir preferência", description = "Define preferência do usuário para um alimento")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> setFoodPreference(
            @Parameter(description = "ID do alimento") @PathVariable Long id,
            @Valid @RequestBody UserPreferenceRequest request) {
        log.info("Set food preference request for food ID: {}", id);
        ApiResponse<String> response = foodService.setFoodPreference(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/preference")
    @Operation(summary = "Remover preferência", description = "Remove preferência do usuário para um alimento")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> removeFoodPreference(
            @Parameter(description = "ID do alimento") @PathVariable Long id) {
        log.info("Remove food preference request for food ID: {}", id);
        ApiResponse<String> response = foodService.removeFoodPreference(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/favorites")
    @Operation(summary = "Alimentos favoritos", description = "Lista alimentos favoritos do usuário atual")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FoodResponse>>> getUserFavorites() {
        log.info("Get user favorites request");
        ApiResponse<List<FoodResponse>> response = foodService.getUserFavorites();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommended")
    @Operation(summary = "Alimentos recomendados", description = "Lista alimentos recomendados baseados nas preferências do usuário")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<FoodResponse>>> getRecommendedFoods(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Get recommended foods request");
        ApiResponse<Page<FoodResponse>> response = foodService.getRecommendedFoods(page, size);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/verify")
    @Operation(summary = "Verificar alimento", description = "Marca um alimento como verificado (apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> verifyFood(
            @Parameter(description = "ID do alimento") @PathVariable Long id) {
        log.info("Verify food request for ID: {}", id);
        ApiResponse<String> response = foodService.verifyFood(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover alimento", description = "Remove um alimento (soft delete - apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteFood(
            @Parameter(description = "ID do alimento") @PathVariable Long id) {
        log.info("Delete food request for ID: {}", id);
        ApiResponse<String> response = foodService.deleteFood(id);
        return ResponseEntity.ok(response);
    }
}