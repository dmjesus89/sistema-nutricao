package com.nutrition.application.service;

import com.nutrition.application.dto.auth.ApiResponse;
import com.nutrition.application.dto.auth.AuthResponse;
import com.nutrition.application.dto.auth.ForgotPasswordRequest;
import com.nutrition.application.dto.auth.LoginRequest;
import com.nutrition.application.dto.auth.RefreshTokenRequest;
import com.nutrition.application.dto.auth.RegisterRequest;
import com.nutrition.application.dto.auth.ResetPasswordRequest;
import com.nutrition.application.dto.auth.UserResponse;
import com.nutrition.domain.entity.auth.EmailConfirmationToken;
import com.nutrition.domain.entity.auth.PasswordResetToken;
import com.nutrition.domain.entity.auth.RefreshToken;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.repository.EmailConfirmationTokenRepository;
import com.nutrition.infrastructure.repository.PasswordResetTokenRepository;
import com.nutrition.infrastructure.repository.RefreshTokenRepository;
import com.nutrition.infrastructure.repository.UserRepository;
import com.nutrition.infrastructure.security.service.JwtService;
import com.nutrition.infrastructure.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${app.email.confirmation.expiration}")
    private Long confirmationTokenExpiration;

    @Value("${app.email.reset-password.expiration}")
    private Long resetPasswordTokenExpiration;

    @Transactional
    public ApiResponse<String> register(RegisterRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.error("email already registered : {}", request.getEmail());
                return ApiResponse.error("Email já está em uso");
            }

            User user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName()).email(request.getEmail().toLowerCase()).password(passwordEncoder.encode(request.getPassword())).role(User.Role.USER).emailConfirmed(false).enabled(false).build();

            user = userRepository.save(user);

            String token = UUID.randomUUID().toString();
            EmailConfirmationToken confirmationToken = EmailConfirmationToken.builder().token(token).user(user).expiresAt(LocalDateTime.now().plusSeconds(confirmationTokenExpiration / 1000)).build();

            confirmationTokenRepository.save(confirmationToken);
            //TODO deve ser assync
            emailService.sendConfirmationEmail(user.getEmail(), user.getFirstName(), token);

            log.info("User registered successfully: {}", user.getEmail());
            return ApiResponse.success("Usuário registrado com sucesso. Verifique seu email para confirmar a conta.");

        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    @Transactional
    public ApiResponse<String> confirmEmail(String token) {
        try {
            EmailConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).orElse(null);

            if (confirmationToken == null) {
                return ApiResponse.error("Token inválido");
            }

            if (confirmationToken.isExpired()) {
                return ApiResponse.error("Token expirado");
            }

            if (confirmationToken.isConfirmed()) {
                return ApiResponse.error("Email já confirmado");
            }

            confirmationToken.confirm();
            confirmationTokenRepository.save(confirmationToken);

            User user = confirmationToken.getUser();
            user.setEmailConfirmed(true);
            user.setEnabled(true);
            userRepository.save(user);

            //TODO deve ser assync
            emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

            log.info("Email confirmed successfully for user: {}", user.getEmail());
            return ApiResponse.success("Email confirmado com sucesso! Sua conta está ativa.");

        } catch (Exception e) {
            log.error("Error confirming email: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    @Transactional
    public ApiResponse<AuthResponse> login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase(), request.getPassword()));
            User user = userRepository.findByEmail(request.getEmail().toLowerCase()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (!user.isEnabled()) {
                return ApiResponse.error("Conta não ativada. Verifique seu email.");
            }

            refreshTokenRepository.revokeAllUserTokens(user);

            String refreshToken = jwtService.generateRefreshToken(user);
            RefreshToken refreshTokenEntity = RefreshToken.builder().user(user).token(refreshToken).expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpirationTime() / 1000)).build();
            refreshTokenRepository.save(refreshTokenEntity);

            UserResponse userResponse = buildUserResponse(user);
            AuthResponse authResponse = AuthResponse.builder().accessToken(getNewAccessToken(user)).refreshToken(refreshToken).expiresIn(jwtService.getExpirationTime() / 1000).user(userResponse).build();

            log.info("User logged in successfully: {}", user.getEmail());
            return ApiResponse.success("Login realizado com sucesso", authResponse);
        } catch (AuthenticationException e) {
            log.warn("Login failed for email: {}", request.getEmail());
            return ApiResponse.error("Email ou senha inválidos");
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }


    @Transactional
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail().toLowerCase()).orElse(null);

            if (user == null) {
                return ApiResponse.success("Se o email existir, você receberá instruções para redefinir sua senha.");
            }

            passwordResetTokenRepository.deletePendingTokensByUser(user);

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder().token(token).user(user).expiresAt(LocalDateTime.now().plusSeconds(resetPasswordTokenExpiration / 1000)).build();

            passwordResetTokenRepository.save(resetToken);

            //TODO tem que ser assync
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), token);

            log.info("Password reset requested for user: {}", user.getEmail());
            return ApiResponse.success("Se o email existir, você receberá instruções para redefinir sua senha.");

        } catch (Exception e) {
            log.error("Error processing forgot password: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    @Transactional
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        try {
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken()).orElse(null);

            if (resetToken == null) {
                return ApiResponse.error("Token inválido");
            }

            if (resetToken.isExpired()) {
                return ApiResponse.error("Token expirado");
            }

            if (resetToken.isUsed()) {
                return ApiResponse.error("Token já utilizado");
            }

            // Atualizar senha
            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            // Marcar token como usado
            resetToken.markAsUsed();
            passwordResetTokenRepository.save(resetToken);

            // Revogar todos os refresh tokens do usuário
            refreshTokenRepository.revokeAllUserTokens(user);

            log.info("Password reset successfully for user: {}", user.getEmail());
            return ApiResponse.success("Senha redefinida com sucesso");

        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    @Transactional
    public ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken()).orElse(null);

            if (refreshToken == null || !refreshToken.isValid()) {
                return ApiResponse.error("Refresh token inválido");
            }

            User user = refreshToken.getUser();

            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);

            String newRefreshToken = jwtService.generateRefreshToken(user);
            RefreshToken newRefreshTokenEntity = RefreshToken.builder().user(user).token(newRefreshToken).expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpirationTime() / 1000)).build();

            refreshTokenRepository.save(newRefreshTokenEntity);

            UserResponse userResponse = buildUserResponse(user);
            AuthResponse authResponse = AuthResponse.builder().accessToken(getNewAccessToken(user)).refreshToken(newRefreshToken).expiresIn(jwtService.getExpirationTime() / 1000).user(userResponse).build();

            log.info("Token refreshed successfully for user: {}", user.getEmail());
            return ApiResponse.success(authResponse);

        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    @Transactional
    public ApiResponse<String> revokeRefreshToken(String refreshToken) {
        try {
            refreshTokenRepository.revokeRefreshToken(refreshToken);

            log.info("Refresh token revoked successfully: {}", refreshToken);
            return ApiResponse.success("Refresh token revoked com sucesso ", refreshToken);

        } catch (Exception e) {
            log.error("Error during revoking refresh token: {}", e.getMessage());
            return ApiResponse.error("Erro interno do servidor");
        }
    }

    private String getNewAccessToken(User user) {
        return jwtService.generateToken(user);
    }

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName()).fullName(user.getFullName()).email(user.getEmail()).role(user.getRole().name()).emailConfirmed(user.getEmailConfirmed()).createdAt(user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build();
    }
}