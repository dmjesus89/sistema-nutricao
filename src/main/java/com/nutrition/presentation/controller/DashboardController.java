package com.nutrition.presentation.controller;

import com.nutrition.application.service.AdminService;
import com.nutrition.application.service.DashboardService;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Endpoints administrativos")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AdminService adminService;


    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Dashboard administrativo", description = "Retorna estatísticas gerais do sistema")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@CurrentUser User user) {
        log.info("Admin dashboard request received");
        Map<String, Object> response = dashboardService.getDashboardStats(user);
        response.putAll(adminService.getDashboardStats());
        return ResponseEntity.ok(response);
    }

}
