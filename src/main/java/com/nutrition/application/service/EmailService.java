package com.nutrition.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontEndUrl;

    private static final String RESEND_URL = "https://api.resend.com/emails";
    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public CompletableFuture<Void> sendConfirmationEmail(String to, String firstName, String confirmationToken) {
        try {
            String subject = "Confirme sua conta - Sistema de Nutrição";
            String confirmationUrl = frontEndUrl + "/auth/email-confirmed?token=" + confirmationToken;
            String htmlContent = buildConfirmationEmailTemplate(firstName, confirmationUrl);

            sendEmail(to, subject, htmlContent);
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
            String passwordResetConfirmationUrl = frontEndUrl + "/auth/password-reset-confirmed?token=" + resetToken;
            String htmlContent = buildPasswordResetEmailTemplate(firstName, passwordResetConfirmationUrl);

            sendEmail(to, subject, htmlContent);
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
            String loginUrl = frontEndUrl + "/auth/login";
            String htmlContent = buildWelcomeEmailTemplate(firstName, loginUrl);

            sendEmail(to, subject, htmlContent);
            log.info("Welcome email sent successfully to: {}", to);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error sending welcome email to {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    @Async
    public CompletableFuture<Void> sendMealReminderEmail(String to, String firstName, String mealName, String mealTime, String mealDetails) {
        try {
            String subject = "Lembrete de Refeição - " + mealName;
            String mealsUrl = frontEndUrl + "/meals";
            String htmlContent = buildMealReminderEmailTemplate(firstName, mealName, mealTime, mealDetails, mealsUrl);

            sendEmail(to, subject, htmlContent);
            log.info("Meal reminder email sent successfully to: {} for meal: {}", to, mealName);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error sending meal reminder email to {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    @Async
    public CompletableFuture<Void> sendMealConsumptionEmail(String to, String firstName, String mealName) {
        try {
            String subject = "Refeição Consumida - " + mealName;
            String mealsUrl = frontEndUrl + "/meals";
            String htmlContent = buildMealConsumptionEmailTemplate(firstName, mealName, mealsUrl);

            sendEmail(to, subject, htmlContent);
            log.info("Meal consumption email sent successfully to: {} for meal: {}", to, mealName);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error sending meal consumption email to {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    @Async
    public CompletableFuture<Void> sendWeeklySummaryEmail(String to, String firstName, int mealsConsumed, String totalCalories) {
        try {
            String subject = "Resumo Semanal de Nutrição";
            String dashboardUrl = frontEndUrl + "/dashboard";
            String htmlContent = buildWeeklySummaryEmailTemplate(firstName, mealsConsumed, totalCalories, dashboardUrl);

            sendEmail(to, subject, htmlContent);
            log.info("Weekly summary email sent successfully to: {}", to);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error sending weekly summary email to {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    // Synchronous methods for email queue processing
    public boolean sendConfirmationEmailSync(String to, String firstName, String confirmationToken) {
        try {
            String subject = "Confirme sua conta - Sistema de Nutrição";
            String confirmationUrl = frontEndUrl + "/auth/email-confirmed?token=" + confirmationToken;
            String htmlContent = buildConfirmationEmailTemplate(firstName, confirmationUrl);

            return sendEmailSync(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("Error sending confirmation email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    public boolean sendWelcomeEmailSync(String to, String firstName) {
        try {
            String subject = "Bem-vindo ao Sistema de Nutrição!";
            String loginUrl = frontEndUrl + "/auth/login";
            String htmlContent = buildWelcomeEmailTemplate(firstName, loginUrl);

            return sendEmailSync(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("Error sending welcome email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    public boolean sendPasswordResetEmailSync(String to, String firstName, String resetToken) {
        try {
            String subject = "Redefinição de senha - Sistema de Nutrição";
            String passwordResetConfirmationUrl = frontEndUrl + "/auth/password-reset-confirmed?token=" + resetToken;
            String htmlContent = buildPasswordResetEmailTemplate(firstName, passwordResetConfirmationUrl);

            return sendEmailSync(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("Error sending password reset email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        boolean success = sendEmailSync(to, subject, htmlContent);
        if (!success) {
            throw new RuntimeException("Failed to send email");
        }
    }

    private boolean sendEmailSync(String to, String subject, String htmlContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + resendApiKey);

        Map<String, String> emailData = new HashMap<>();
        emailData.put("from", fromEmail);
        emailData.put("to", to);
        emailData.put("subject", subject);
        emailData.put("html", htmlContent);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(emailData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(RESEND_URL, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to send email: {}", response.getStatusCode());
                return false;
            }

            log.debug("Email sent successfully via Resend: {}", response.getBody());
            return true;
        } catch (Exception e) {
            log.error("Error calling Resend API: {}", e.getMessage());
            return false;
        }
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

    private String buildMealReminderEmailTemplate(String firstName, String mealName, String mealTime, String mealDetails, String mealsUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Lembrete de Refeição</title>
                </head>
                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
                        <div style="text-align: center; margin-bottom: 20px;">
                            <h1 style="color: #FF9800; margin: 0;">🍽️ Hora da Refeição!</h1>
                        </div>
                        <p>Olá <strong>%s</strong>,</p>
                        <p>Este é um lembrete de que está na hora da sua refeição:</p>
                        <div style="background-color: #fff3e0; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #FF9800;">
                            <h2 style="margin: 0 0 10px 0; color: #F57C00;">%s</h2>
                            <p style="margin: 5px 0; color: #666;"><strong>Horário:</strong> %s</p>
                            <div style="margin-top: 10px;">
                                %s
                            </div>
                        </div>
                        <p>Não se esqueça de marcar esta refeição como consumida após se alimentar!</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #FF9800; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Ver Minhas Refeições</a>
                        </div>
                        <p style="margin-top: 30px; color: #666; font-size: 12px; text-align: center;">
                            💡 Dica: Manter uma alimentação regular ajuda a alcançar seus objetivos de saúde!
                        </p>
                    </div>
                </body>
                </html>
                """, firstName, mealName, mealTime, mealDetails, mealsUrl);
    }

    private String buildMealConsumptionEmailTemplate(String firstName, String mealName, String mealsUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Refeição Consumida</title>
                </head>
                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
                        <div style="text-align: center; margin-bottom: 20px;">
                            <h1 style="color: #4CAF50; margin: 0;">✅ Refeição Registrada!</h1>
                        </div>
                        <p>Olá <strong>%s</strong>,</p>
                        <p>Você acabou de marcar a refeição <strong>"%s"</strong> como consumida.</p>
                        <div style="background-color: #e8f5e9; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #4CAF50;">
                            <p style="margin: 0; color: #2e7d32;">🎯 Continue assim para manter seus objetivos nutricionais em dia!</p>
                        </div>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Ver Todas as Refeições</a>
                        </div>
                        <p style="margin-top: 30px; color: #666; font-size: 12px; text-align: center;">
                            💪 Cada refeição registrada é um passo em direção aos seus objetivos!
                        </p>
                    </div>
                </body>
                </html>
                """, firstName, mealName, mealsUrl);
    }

    private String buildWeeklySummaryEmailTemplate(String firstName, int mealsConsumed, String totalCalories, String dashboardUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Resumo Semanal</title>
                </head>
                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
                        <div style="text-align: center; margin-bottom: 20px;">
                            <h1 style="color: #2196F3; margin: 0;">📊 Resumo Semanal</h1>
                        </div>
                        <p>Olá <strong>%s</strong>,</p>
                        <p>Aqui está o resumo da sua semana:</p>
                        <div style="background-color: #e3f2fd; padding: 20px; border-radius: 8px; margin: 20px 0;">
                            <div style="margin: 15px 0; padding: 10px; background-color: white; border-radius: 4px;">
                                <p style="margin: 0; color: #666;">🍽️ <strong>Refeições consumidas:</strong></p>
                                <p style="margin: 5px 0 0 0; font-size: 24px; color: #1976D2; font-weight: bold;">%d</p>
                            </div>
                            <div style="margin: 15px 0; padding: 10px; background-color: white; border-radius: 4px;">
                                <p style="margin: 0; color: #666;">🔥 <strong>Total de calorias:</strong></p>
                                <p style="margin: 5px 0 0 0; font-size: 24px; color: #1976D2; font-weight: bold;">%s</p>
                            </div>
                        </div>
                        <p style="color: #4CAF50; font-weight: bold; text-align: center;">Continue com o ótimo trabalho! 💪</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #2196F3; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Ver Dashboard</a>
                        </div>
                        <p style="margin-top: 30px; color: #666; font-size: 12px; text-align: center;">
                            📈 Acompanhe seu progresso e mantenha-se motivado!
                        </p>
                    </div>
                </body>
                </html>
                """, firstName, mealsConsumed, totalCalories, dashboardUrl);
    }
}