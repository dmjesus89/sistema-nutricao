package com.nutrition.presentation.controller;

import com.nutrition.application.dto.auth.ApiResponse;
import com.nutrition.application.dto.auth.AuthResponse;
import com.nutrition.application.dto.auth.ForgotPasswordRequest;
import com.nutrition.application.dto.auth.LoginRequest;
import com.nutrition.application.dto.auth.RefreshTokenRequest;
import com.nutrition.application.dto.auth.RegisterRequest;
import com.nutrition.application.dto.auth.ResetPasswordRequest;
import com.nutrition.application.service.AuthService;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints para autenticação e gestão de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        ApiResponse<String> response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirm")
    @Operation(summary = "Confirmar email", description = "Confirma o email do usuário usando token")
    public ResponseEntity<ApiResponse<String>> confirmEmail(@RequestParam("token") String token) {
        log.info("Email confirmation attempt with token");
        ApiResponse<String> response = authService.confirmEmail(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login do usuário", description = "Autentica um usuário e retorna tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        ApiResponse<AuthResponse> response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Esqueceu a senha", description = "Envia email para redefinição de senha")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        ApiResponse<String> response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefinir senha", description = "Redefine a senha usando token")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @CurrentUser User user,
                                                             @RequestBody ResetPasswordRequest request) {
        log.info("User {} Password reset attempt", user.getEmail());
        ApiResponse<String> response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Renovar token", description = "Gera novo access token usando refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @CurrentUser User user,
                                                                  @RequestBody RefreshTokenRequest request) {
        log.info("User {} Token refresh attempt", user.getEmail());
        ApiResponse<AuthResponse> response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout do usuário", description = "Revoga o refresh token do usuário")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @CurrentUser User user,
                                                      @RequestBody RefreshTokenRequest request) {
        log.info("User {} Logout attempt", user.getEmail());

        ApiResponse<String> response = authService.revokeRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logout realizado com sucesso"));
    }
}