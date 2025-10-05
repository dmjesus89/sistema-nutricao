package com.nutrition.presentation.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints para autenticação e gestão de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário")
    public void register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        authService.register(request);
    }

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Confirmar email", description = "Confirma o email do usuário usando token")
    public void confirmEmail(@RequestParam("token") String token) {
        log.info("Email confirmation attempt with token");
        authService.confirmEmail(token);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login do usuário", description = "Autentica um usuário e retorna tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Esqueceu a senha", description = "Envia email para redefinição de senha")
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Redefinir senha", description = "Redefine a senha usando token")
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        log.info("Token {} Password reset attempt", request.getToken());
        authService.resetPassword(request);
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Renovar token", description = "Gera novo access token usando refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @CurrentUser User user, @RequestBody RefreshTokenRequest request) {
        log.info("User {} Token refresh attempt", user.getEmail());
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Logout do usuário", description = "Revoga o refresh token do usuário")
    public void logout(@Valid @CurrentUser User user, @RequestBody RefreshTokenRequest request) {
        log.info("User {} Logout attempt", user.getEmail());
        authService.revokeRefreshToken(request.getRefreshToken());
    }
}