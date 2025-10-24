package com.nutrition.presentation.controller;

import com.nutrition.application.dto.food.AddSupplementRequest;
import com.nutrition.application.dto.food.CreateSupplementRequest;
import com.nutrition.application.dto.food.FoodResponse;
import com.nutrition.application.dto.food.SupplementResponse;
import com.nutrition.application.dto.food.TimeRoutineRequest;
import com.nutrition.application.dto.food.UpdateSupplementFrequencyRequest;
import com.nutrition.application.dto.food.UserPreferenceRequest;
import com.nutrition.application.dto.food.UserSupplementResponse;
import com.nutrition.application.service.SupplementService;
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
    public ResponseEntity<SupplementResponse> createSupplement(
            @Valid @RequestBody CreateSupplementRequest request) {
        log.info("Supplement creation request received");
        SupplementResponse response = supplementService.createSupplement(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar suplementos", description = "Busca suplementos com filtros opcionais")
    public ResponseEntity<Page<SupplementResponse>> searchSupplements(
            @Parameter(description = "Termo de busca") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Categoria") @RequestParam(required = false) String category,
            @Parameter(description = "Forma") @RequestParam(required = false) String form,
            @Parameter(description = "Marca") @RequestParam(required = false) String brand,
            @Parameter(description = "Apenas verificados") @RequestParam(required = false) Boolean verified,
            @Parameter(description = "Número da página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Supplement search request received");
        Page<SupplementResponse> response = supplementService.searchSupplements(
                searchTerm, category, form, brand, verified, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter suplemento por ID", description = "Retorna detalhes de um suplemento específico")
    public ResponseEntity<SupplementResponse> getSupplementById(
            @Parameter(description = "ID do suplemento") @PathVariable Long id) {
        log.info("Get supplement by ID request: {}", id);
        SupplementResponse response = supplementService.getSupplementById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Suplementos por categoria", description = "Lista suplementos de uma categoria específica")
    public ResponseEntity<Page<SupplementResponse>> getSupplementsByCategory(
            @Parameter(description = "Nome da categoria") @PathVariable String category,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Get supplements by category request: {}", category);
        Page<SupplementResponse> response = supplementService.getSupplementsByCategory(category, page, size);
        return ResponseEntity.ok(response);
    }

    // ========== DEPRECATED ENDPOINTS - Removed in favor of frequency-based tracking ==========
    // Old preference-based endpoints removed:
    // - POST /{id}/preference (use POST /{id}/track instead)
    // - DELETE /{id}/preference (use DELETE /{id}/track instead)
    // - PUT /{id}/time-routine (use PUT /{id}/frequency instead)
    // - GET /favorites (no direct replacement - preferences concept removed)
    // - GET /preferences (use GET /my-supplements instead)
    // - GET /current (use GET /my-supplements instead)

    @GetMapping("/recommended")
    @Operation(summary = "Suplementos recomendados", description = "Lista suplementos recomendados baseados nas preferências do usuário")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<SupplementResponse>> getRecommendedSupplements(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        log.info("Get recommended supplements request");
        Page<SupplementResponse> response = supplementService.getRecommendedSupplements(page, size);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Verificar suplemento", description = "Marca um suplemento como verificado (apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public void verifySupplement(
            @Parameter(description = "ID do suplemento") @PathVariable Long id) {
        log.info("Verify supplement request for ID: {}", id);
        supplementService.verifySupplement(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover suplemento", description = "Remove um suplemento (soft delete - apenas admins)")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSupplement(@Parameter(description = "ID do suplemento") @PathVariable Long id) {
        log.info("Delete supplement request for ID: {}", id);
        supplementService.deleteSupplement(id);
    }

    // ========== NEW SUPPLEMENT TRACKING ENDPOINTS (Frequency-based) ==========

    @PostMapping("/{id}/track")
    @Operation(summary = "Adicionar suplemento ao acompanhamento",
               description = "Adiciona um suplemento à lista de acompanhamento do usuário com configurações de frequência")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserSupplementResponse> addSupplementTracking(
            @Parameter(description = "ID do suplemento") @PathVariable Long id,
            @Valid @RequestBody AddSupplementRequest request) {
        log.info("Add supplement tracking request for supplement ID: {}", id);
        UserSupplementResponse response = supplementService.addSupplement(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/frequency")
    @Operation(summary = "Atualizar frequência do suplemento",
               description = "Atualiza a frequência e configurações de lembretes de um suplemento")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserSupplementResponse> updateSupplementFrequency(
            @Parameter(description = "ID do suplemento") @PathVariable Long id,
            @Valid @RequestBody UpdateSupplementFrequencyRequest request) {
        log.info("Update supplement frequency request for supplement ID: {}", id);
        UserSupplementResponse response = supplementService.updateSupplementFrequency(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/track")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover suplemento do acompanhamento",
               description = "Remove um suplemento da lista de acompanhamento do usuário")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void removeSupplementTracking(
            @Parameter(description = "ID do suplemento") @PathVariable Long id) {
        log.info("Remove supplement tracking request for supplement ID: {}", id);
        supplementService.removeSupplement(id);
    }

    @PostMapping("/{id}/mark-taken")
    @Operation(summary = "Marcar suplemento como tomado",
               description = "Registra que o usuário tomou o suplemento agora")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserSupplementResponse> markSupplementAsTaken(
            @Parameter(description = "ID do suplemento") @PathVariable Long id) {
        log.info("Mark supplement as taken request for supplement ID: {}", id);
        UserSupplementResponse response = supplementService.markSupplementAsTaken(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-supplements")
    @Operation(summary = "Meus suplementos",
               description = "Lista todos os suplementos que o usuário está acompanhando")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserSupplementResponse>> getUserSupplements() {
        log.info("Get user supplements request");
        List<UserSupplementResponse> response = supplementService.getUserSupplements();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-supplements/by-frequency/{frequency}")
    @Operation(summary = "Meus suplementos por frequência",
               description = "Lista suplementos do usuário filtrados por frequência (DAILY, WEEKLY, TWICE_WEEKLY, THREE_TIMES_WEEKLY, MONTHLY)")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserSupplementResponse>> getUserSupplementsByFrequency(
            @Parameter(description = "Frequência (DAILY, WEEKLY, TWICE_WEEKLY, THREE_TIMES_WEEKLY, MONTHLY)")
            @PathVariable String frequency) {
        log.info("Get user supplements by frequency request: {}", frequency);
        List<UserSupplementResponse> response = supplementService.getUserSupplementsByFrequency(frequency);
        return ResponseEntity.ok(response);
    }
}