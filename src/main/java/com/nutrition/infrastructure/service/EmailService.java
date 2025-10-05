package com.nutrition.infrastructure.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontEndUrl;

    @Async
    public CompletableFuture<Void> sendConfirmationEmail(String to, String firstName, String confirmationToken) {
        try {
            String subject = "Confirme sua conta - Sistema de Nutrição";
            String confirmationUrl = frontEndUrl + "/auth/email-confirmed?token=" + confirmationToken;

            String htmlContent = buildConfirmationEmailTemplate(firstName, confirmationUrl);

            sendHtmlEmail(to, subject, htmlContent);
            log.info("Confirmation email sent successfully to: {}", to);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error sending confirmation email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send confirmation email", e);
        }
    }

    @Async
    public CompletableFuture<Void> sendPasswordResetEmail(String to, String firstName, String resetToken) {
        try {
            String subject = "Redefinição de senha - Sistema de Nutrição";
            String passwordResetConfirmationUrl  = frontEndUrl + "/auth/password-reset-confirmed?token=" + resetToken;
            String htmlContent = buildPasswordResetEmailTemplate(firstName, passwordResetConfirmationUrl);

            sendHtmlEmail(to, subject, htmlContent);
            log.info("Password reset email sent successfully to: {}", to);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error sending password reset email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Async
    public CompletableFuture<Void> sendWelcomeEmail(String to, String firstName) {
        try {
            String subject = "Bem-vindo ao Sistema de Nutrição!";
            String loginUrl  = frontEndUrl + "/auth/login";
            String htmlContent = buildWelcomeEmailTemplate(firstName,loginUrl);

            sendHtmlEmail(to, subject, htmlContent);
            log.info("Welcome email sent successfully to: {}", to);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error sending welcome email to {}: {}", to, e.getMessage());
            // Don't throw exception for welcome email as it's not critical
            return CompletableFuture.completedFuture(null);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String buildConfirmationEmailTemplate(String firstName, String confirmationUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Confirme sua conta</title>
                </head>
                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
                        <h1 style="color: #2c5530;">Bem-vindo ao Sistema de Nutrição!</h1>
                        <p>Olá <strong>%s</strong>,</p>
                        <p>Obrigado por se registrar! Para ativar sua conta, clique no botão abaixo:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Confirmar Conta</a>
                        </div>
                        <p>Ou copie e cole este link em seu navegador:</p>
                        <p style="word-break: break-all; color: #666;">%s</p>
                        <p style="margin-top: 30px; color: #666; font-size: 12px;">
                            Este link expirará em 24 horas. Se você não solicitou este registro, pode ignorar este email.
                        </p>
                    </div>
                </body>
                </html>
                """, firstName, confirmationUrl, confirmationUrl);
    }

    private String buildPasswordResetEmailTemplate(String firstName, String resetUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Redefinir senha</title>
                </head>
                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
                        <h1 style="color: #d32f2f;">Redefinição de Senha</h1>
                        <p>Olá <strong>%s</strong>,</p>
                        <p>Recebemos uma solicitação para redefinir sua senha. Clique no botão abaixo para criar uma nova senha:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #f44336; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Redefinir Senha</a>
                        </div>
                        <p>Ou copie e cole este link em seu navegador:</p>
                        <p style="word-break: break-all; color: #666;">%s</p>
                        <p style="margin-top: 30px; color: #666; font-size: 12px;">
                            Este link expirará em 1 hora. Se você não solicitou esta redefinição, pode ignorar este email.
                        </p>
                    </div>
                </body>
                </html>
                """, firstName, resetUrl, resetUrl);
    }

    private String buildWelcomeEmailTemplate(String firstName, String loginUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Bem-vindo!</title>
                </head>
                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
                        <h1 style="color: #4CAF50;">Conta ativada com sucesso!</h1>
                        <p>Olá <strong>%s</strong>,</p>
                        <p>Sua conta foi ativada com sucesso! Agora você pode aproveitar todos os recursos do Sistema de Nutrição:</p>
                        <ul style="color: #333;">
                            <li>Controle seu peso e objetivos</li>
                            <li>Planejamento de dietas personalizadas</li>
                            <li>Acompanhamento de hidratação</li>
                            <li>Relatórios de evolução</li>
                        </ul>
                        <p>Estamos aqui para ajudá-lo em sua jornada para uma vida mais saudável!</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Fazer Login</a>
                        </div>
                    </div>
                </body>
                </html>
                """, firstName, loginUrl);
    }

}