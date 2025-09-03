package com.nutrition.presentation.controller;

import com.nutrition.application.dto.auth.ApiResponse;
import com.nutrition.application.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Endpoints administrativos")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard administrativo", description = "Retorna estatísticas gerais do sistema")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        log.info("Admin dashboard request received");
        ApiResponse<Map<String, Object>> response = adminService.getDashboardStats();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/foods")
    @Operation(summary = "Estatísticas de alimentos", description = "Retorna estatísticas detalhadas sobre alimentos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFoodStats() {
        log.info("Admin food stats request received");
        ApiResponse<Map<String, Object>> response = adminService.getFoodStatistics();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/supplements")
    @Operation(summary = "Estatísticas de suplementos", description = "Retorna estatísticas detalhadas sobre suplementos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSupplementStats() {
        log.info("Admin supplement stats request received");
        ApiResponse<Map<String, Object>> response = adminService.getSupplementStatistics();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/users")
    @Operation(summary = "Estatísticas de usuários", description = "Retorna estatísticas sobre usuários e suas preferências")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        log.info("Admin user stats request received");
        ApiResponse<Map<String, Object>> response = adminService.getUserStatistics();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/data/seed")
    @Operation(summary = "Popular dados", description = "Popula o banco com dados de exemplo (desenvolvimento)")
    public ResponseEntity<ApiResponse<String>> seedDatabase() {
        log.info("Database seed request received");
        ApiResponse<String> response = adminService.seedDatabase();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/maintenance/cleanup")
    @Operation(summary = "Limpeza de dados", description = "Remove dados não utilizados e otimiza o banco")
    public ResponseEntity<ApiResponse<String>> performMaintenance() {
        log.info("Database maintenance request received");
        ApiResponse<String> response = adminService.performMaintenance();
        return ResponseEntity.ok(response);
    }
}
