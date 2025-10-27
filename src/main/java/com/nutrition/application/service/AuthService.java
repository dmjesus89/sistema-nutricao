package com.nutrition.application.service;

import com.nutrition.application.dto.auth.AuthResponse;
import com.nutrition.application.dto.auth.ForgotPasswordRequest;
import com.nutrition.application.dto.auth.LoginRequest;
import com.nutrition.application.dto.auth.RefreshTokenRequest;
import com.nutrition.application.dto.auth.RegisterRequest;
import com.nutrition.application.dto.auth.ResetPasswordRequest;
import com.nutrition.application.dto.auth.UserResponse;
import com.nutrition.domain.entity.auth.EmailConfirmationToken;
import com.nutrition.domain.entity.auth.EmailQueue;
import com.nutrition.domain.entity.auth.PasswordResetToken;
import com.nutrition.domain.entity.auth.RefreshToken;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.exception.UnprocessableEntityException;
import com.nutrition.infrastructure.repository.EmailConfirmationTokenRepository;
import com.nutrition.infrastructure.repository.PasswordResetTokenRepository;
import com.nutrition.infrastructure.repository.RefreshTokenRepository;
import com.nutrition.infrastructure.repository.UserRepository;
import com.nutrition.infrastructure.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final EmailQueueService emailQueueService;

    @Value("${app.email.confirmation.expiration}")
    private Long confirmationTokenExpiration;

    @Value("${app.email.reset-password.expiration}")
    private Long resetPasswordTokenExpiration;

    @Transactional
    public void register(RegisterRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.error("email already registered : {}", request.getEmail());
                throw new UnprocessableEntityException("Email já está em uso");
            }

            User user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName()).email(request.getEmail().toLowerCase()).password(passwordEncoder.encode(request.getPassword())).role(User.Role.USER).emailConfirmed(false).enabled(false).preferredLocale(request.getPreferredLocale() != null ? request.getPreferredLocale() : "en").build();
            user = userRepository.save(user);

            String token = UUID.randomUUID().toString();
            EmailConfirmationToken confirmationToken = EmailConfirmationToken.builder().token(token).user(user).expiresAt(LocalDateTime.now().plusSeconds(confirmationTokenExpiration / 1000)).build();
            confirmationTokenRepository.save(confirmationToken);

            // Queue confirmation email instead of sending directly
            emailQueueService.queueEmail(
                EmailQueue.EmailType.CONFIRMATION,
                user.getEmail(),
                user.getFirstName(),
                token,
                null
            );
            log.info("User registered successfully: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void confirmEmail(String token) {
        try {
            EmailConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).orElse(null);

            if (confirmationToken == null) {
                throw new UnprocessableEntityException("Token inválido");
            }

            if (confirmationToken.isExpired()) {
                throw new UnprocessableEntityException("Token expirado");
            }

            if (confirmationToken.isConfirmed()) {
                throw new UnprocessableEntityException("Email já confirmado");
            }

            confirmationToken.confirm();
            confirmationTokenRepository.save(confirmationToken);

            User user = confirmationToken.getUser();
            user.setEmailConfirmed(true);
            user.setEnabled(true);
            userRepository.save(user);

            // Queue welcome email instead of sending directly
            emailQueueService.queueEmail(
                EmailQueue.EmailType.WELCOME,
                user.getEmail(),
                user.getFirstName(),
                null,
                null
            );

            log.info("Email confirmed successfully for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error confirming email: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase(), request.getPassword()));
            User user = userRepository.findByEmail(request.getEmail().toLowerCase()).orElseThrow(() -> new UnprocessableEntityException("Usuário não encontrado"));

            if (!user.isEnabled()) {
                throw new UnprocessableEntityException("Conta não ativada. Verifique seu email.");
            }

            refreshTokenRepository.revokeAllUserTokens(user);

            String refreshToken = jwtService.generateRefreshToken(user);
            RefreshToken refreshTokenEntity = RefreshToken.builder().user(user).token(refreshToken).expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpirationTime() / 1000)).build();
            refreshTokenRepository.save(refreshTokenEntity);

            UserResponse userResponse = buildUserResponse(user);
            AuthResponse authResponse = AuthResponse.builder().accessToken(getNewAccessToken(user)).refreshToken(refreshToken).expiresIn(jwtService.getExpirationTime() / 1000).user(userResponse).build();

            log.info("User logged in successfully: {}", user.getEmail());
            return authResponse;
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            throw e;
        }
    }


    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail().toLowerCase()).orElse(null);
            if (user == null) {
                return;
            }

            passwordResetTokenRepository.deletePendingTokensByUser(user);

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder().token(token).user(user).expiresAt(LocalDateTime.now().plusSeconds(resetPasswordTokenExpiration / 1000)).build();
            passwordResetTokenRepository.save(resetToken);

            // Queue password reset email instead of sending directly
            emailQueueService.queueEmail(
                EmailQueue.EmailType.PASSWORD_RESET,
                user.getEmail(),
                user.getFirstName(),
                token,
                null
            );
            log.info("Password reset requested for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error processing forgot password: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        try {
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken()).orElse(null);

            if (resetToken == null) {
                throw new UnprocessableEntityException("Token inválido");
            }

            if (resetToken.isExpired()) {
                throw new UnprocessableEntityException("Token expirado");
            }

            if (resetToken.isUsed()) {
                throw new UnprocessableEntityException("Token já utilizado");
            }

            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            resetToken.markAsUsed();
            passwordResetTokenRepository.save(resetToken);

            refreshTokenRepository.revokeAllUserTokens(user);
            log.info("Password reset successfully for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken()).orElse(null);

            if (refreshToken == null || !refreshToken.isValid()) {
                throw new UnprocessableEntityException("Refresh token inválido");
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
            return authResponse;
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        try {
            refreshTokenRepository.revokeRefreshToken(refreshToken);
            log.info("Refresh token revoked successfully: {}", refreshToken);
        } catch (Exception e) {
            log.error("Error during revoking refresh token: {}", e.getMessage());
            throw e;
        }
    }

    private String getNewAccessToken(User user) {
        return jwtService.generateToken(user);
    }

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName()).fullName(user.getFullName()).email(user.getEmail()).role(user.getRole().name()).emailConfirmed(user.getEmailConfirmed()).preferredLocale(user.getPreferredLocale()).createdAt(user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build();
    }
}