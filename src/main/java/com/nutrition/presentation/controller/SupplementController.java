package com.nutrition.presentation.controller;

import com.nutrition.application.dto.auth.ApiResponse;
import com.nutrition.application.dto.food.CreateSupplementRequest;
import com.nutrition.application.dto.food.SupplementResponse;
import com.nutrition.application.dto.food.UserPreferenceRequest;
import com.nutrition.application.service.SupplementService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/supplements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Supplements", description = "Endpoints para gestão de suplementos")
@SecurityRequirement(name = "Bearer Authentication")
public class SupplementController {

    private final SupplementService supplementService;

    @PostMapping
    @Operation(summary = "Criar suplemento", description = "Cria um novo suplemento (apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SupplementResponse>> createSupplement(
            @Valid @RequestBody CreateSupplementRequest request) {
        log.info("Supplement creation request received");
        ApiResponse<SupplementResponse> response = supplementService.createSupplement(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar suplementos", description = "Busca suplementos com filtros opcionais")
    public ResponseEntity<ApiResponse<Page<SupplementResponse>>> searchSupplements(
            @Parameter(description = "Termo de busca") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Categoria") @RequestParam(required = false) String category,
            @Parameter(description = "Forma") @RequestParam(required = false) String form,
            @Parameter(description = "Marca") @RequestParam(required = false) String brand,
            @Parameter(description = "Apenas verificados") @RequestParam(required = false) Boolean verified,
            @Parameter(description = "Número da página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Supplement search request received");
        ApiResponse<Page<SupplementResponse>> response = supplementService.searchSupplements(
                searchTerm, category, form, brand, verified, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter suplemento por ID", description = "Retorna detalhes de um suplemento específico")
    public ResponseEntity<ApiResponse<SupplementResponse>> getSupplementById(
            @Parameter(description = "ID do suplemento") @PathVariable Long id) {
        log.info("Get supplement by ID request: {}", id);
        ApiResponse<SupplementResponse> response = supplementService.getSupplementById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Suplementos por categoria", description = "Lista suplementos de uma categoria específica")
    public ResponseEntity<ApiResponse<Page<SupplementResponse>>> getSupplementsByCategory(
            @Parameter(description = "Nome da categoria") @PathVariable String category,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Get supplements by category request: {}", category);
        ApiResponse<Page<SupplementResponse>> response = supplementService.getSupplementsByCategory(category, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/preference")
    @Operation(summary = "Definir preferência", description = "Define preferência do usuário para um suplemento")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> setSupplementPreference(
            @Parameter(description = "ID do suplemento") @PathVariable Long id,
            @Valid @RequestBody UserPreferenceRequest request) {
        log.info("Set supplement preference request for supplement ID: {}", id);
        ApiResponse<String> response = supplementService.setSupplementPreference(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/preference")
    @Operation(summary = "Remover preferência", description = "Remove preferência do usuário para um suplemento")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> removeSupplementPreference(
            @Parameter(description = "ID do suplemento") @PathVariable Long id) {
        log.info("Remove supplement preference request for supplement ID: {}", id);
        ApiResponse<String> response = supplementService.removeSupplementPreference(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/favorites")
    @Operation(summary = "Suplementos favoritos", description = "Lista suplementos favoritos do usuário atual")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SupplementResponse>>> getUserFavorites() {
        log.info("Get user favorites request");
        ApiResponse<List<SupplementResponse>> response = supplementService.getUserFavorites();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current")
    @Operation(summary = "Suplementos atuais", description = "Lista suplementos em uso atual pelo usuário")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SupplementResponse>>> getCurrentSupplements() {
        log.info("Get current supplements request");
        ApiResponse<List<SupplementResponse>> response = supplementService.getCurrentSupplements();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommended")
    @Operation(summary = "Suplementos recomendados", description = "Lista suplementos recomendados baseados nas preferências do usuário")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<SupplementResponse>>> getRecommendedSupplements(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Get recommended supplements request");
        ApiResponse<Page<SupplementResponse>> response = supplementService.getRecommendedSupplements(page, size);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/verify")
    @Operation(summary = "Verificar suplemento", description = "Marca um suplemento como verificado (apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> verifySupplement(
            @Parameter(description = "ID do suplemento") @PathVariable Long id) {
        log.info("Verify supplement request for ID: {}", id);
        ApiResponse<String> response = supplementService.verifySupplement(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover suplemento", description = "Remove um suplemento (soft delete - apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteSupplement(
            @Parameter(description = "ID do suplemento") @PathVariable Long id) {
        log.info("Delete supplement request for ID: {}", id);
        ApiResponse<String> response = supplementService.deleteSupplement(id);
        return ResponseEntity.ok(response);
    }
}