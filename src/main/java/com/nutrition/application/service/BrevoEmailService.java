package com.nutrition.application.service;


import io.restassured.RestAssured;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
public class BrevoEmailService {

    //@Value("${app.email.key}")
    private String brevoApiKeys;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontEndUrl;


    private boolean sendEmailViaBrevo(String to, String subject, String htmlContent) {
        try {
            String payload = String.format("""
                {
                    "sender": {
                        "email": "%s",
                        "name": "Sistema de Nutrição"
                    },
                    "to": [
                        {
                            "email": "%s"
                        }
                    ],
                    "subject": "%s",
                    "htmlContent": "%s"
                }
                """,
                    fromEmail,
                    to,
                    escapeJson(subject),
                    escapeJson(htmlContent)
            );

            int statusCode = RestAssured
                    .given()
                  // .header("api-key", brevoApiKey)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .when()
                    .post("https://api.brevo.com/v3/smtp/email")
                    .getStatusCode();

            if (statusCode >= 200 && statusCode < 300) {
                log.debug("Email sent successfully via Brevo");
                return true;
            } else {
                log.error("Failed to send email via Brevo. Status code: {}", statusCode);
                return false;
            }
        } catch (Exception e) {
            log.error("Error calling Brevo API: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Escape JSON special characters
     */
    private String escapeJson(String text) {
        if (text == null) return "";
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ==================== CONFIRMATION EMAIL ====================

    @Async
    public CompletableFuture<Void> sendConfirmationEmail(String to, String firstName, String confirmationToken) {
        try {
            String subject = "Confirme sua conta - Sistema de Nutrição";
            String confirmationUrl = frontEndUrl + "/auth/email-confirmed?token=" + confirmationToken;
            String htmlContent = buildConfirmationEmailTemplate(firstName, confirmationUrl);

            boolean success = sendEmailViaBrevo(to, subject, htmlContent);
            if (success) {
                log.info("✅ Confirmation email sent successfully to: {}", to);
                return CompletableFuture.completedFuture(null);
            } else {
                throw new RuntimeException("Failed to send confirmation email");
            }
        } catch (Exception e) {
            log.error("❌ Error sending confirmation email to {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    public boolean sendConfirmationEmailSync(String to, String firstName, String confirmationToken) {
        try {
            String subject = "Confirme sua conta - Sistema de Nutrição";
            String confirmationUrl = frontEndUrl + "/auth/email-confirmed?token=" + confirmationToken;
            String htmlContent = buildConfirmationEmailTemplate(firstName, confirmationUrl);

            boolean success = sendEmailViaBrevo(to, subject, htmlContent);
            if (success) {
                log.info("✅ Confirmation email sent successfully (sync) to: {}", to);
            }
            return success;
        } catch (Exception e) {
            log.error("❌ Error sending confirmation email (sync) to {}: {}", to, e.getMessage());
            return false;
        }
    }

    // ==================== PASSWORD RESET EMAIL ====================

    @Async
    public CompletableFuture<Void> sendPasswordResetEmail(String to, String firstName, String resetToken) {
        try {
            String subject = "Redefinição de senha - Sistema de Nutrição";
            String passwordResetUrl = frontEndUrl + "/auth/password-reset-confirmed?token=" + resetToken;
            String htmlContent = buildPasswordResetEmailTemplate(firstName, passwordResetUrl);

            boolean success = sendEmailViaBrevo(to, subject, htmlContent);
            if (success) {
                log.info("✅ Password reset email sent successfully to: {}", to);
                return CompletableFuture.completedFuture(null);
            } else {
                throw new RuntimeException("Failed to send password reset email");
            }
        } catch (Exception e) {
            log.error("❌ Error sending password reset email to {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    public boolean sendPasswordResetEmailSync(String to, String firstName, String resetToken) {
        try {
            String subject = "Redefinição de senha - Sistema de Nutrição";
            String passwordResetUrl = frontEndUrl + "/auth/password-reset-confirmed?token=" + resetToken;
            String htmlContent = buildPasswordResetEmailTemplate(firstName, passwordResetUrl);

            boolean success = sendEmailViaBrevo(to, subject, htmlContent);
            if (success) {
                log.info("✅ Password reset email sent successfully (sync) to: {}", to);
            }
            return success;
        } catch (Exception e) {
            log.error("❌ Error sending password reset email (sync) to {}: {}", to, e.getMessage());
            return false;
        }
    }

    // ==================== WELCOME EMAIL ====================

    @Async
    public CompletableFuture<Void> sendWelcomeEmail(String to, String firstName) {
        try {
            String subject = "Bem-vindo ao Sistema de Nutrição!";
            String loginUrl = frontEndUrl + "/auth/login";
            String htmlContent = buildWelcomeEmailTemplate(firstName, loginUrl);

            boolean success = sendEmailViaBrevo(to, subject, htmlContent);
            if (success) {
                log.info("✅ Welcome email sent successfully to: {}", to);
                return CompletableFuture.completedFuture(null);
            } else {
                throw new RuntimeException("Failed to send welcome email");
            }
        } catch (Exception e) {
            log.error("❌ Error sending welcome email to {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    public boolean sendWelcomeEmailSync(String to, String firstName) {
        try {
            String subject = "Bem-vindo ao Sistema de Nutrição!";
            String loginUrl = frontEndUrl + "/auth/login";
            String htmlContent = buildWelcomeEmailTemplate(firstName, loginUrl);

            return sendEmailViaBrevo(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("❌ Error sending welcome email (sync) to {}: {}", to, e.getMessage());
            return false;
        }
    }

    // ==================== MEAL REMINDER EMAIL ====================

    @Async
    public CompletableFuture<Void> sendMealReminderEmail(String to, String firstName, String mealName,
                                                         String mealTime, String mealDetails) {
        try {
            String subject = "Lembrete de Refeição - " + mealName;
            String mealsUrl = frontEndUrl + "/meals";
            String htmlContent = buildMealReminderEmailTemplate(firstName, mealName, mealTime, mealDetails, mealsUrl);

            boolean success = sendEmailViaBrevo(to, subject, htmlContent);
            if (success) {
                log.info("✅ Meal reminder email sent successfully to: {} for meal: {}", to, mealName);
                return CompletableFuture.completedFuture(null);
            } else {
                throw new RuntimeException("Failed to send meal reminder email");
            }
        } catch (Exception e) {
            log.error("❌ Error sending meal reminder email to {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    // ==================== MEAL CONSUMPTION EMAIL ====================

    @Async
    public CompletableFuture<Void> sendMealConsumptionEmail(String to, String firstName, String mealName) {
        try {
            String subject = "Refeição Consumida - " + mealName;
            String mealsUrl = frontEndUrl + "/meals";
            String htmlContent = buildMealConsumptionEmailTemplate(firstName, mealName, mealsUrl);

            boolean success = sendEmailViaBrevo(to, subject, htmlContent);
            if (success) {
                log.info("✅ Meal consumption email sent successfully to: {} for meal: {}", to, mealName);
                return CompletableFuture.completedFuture(null);
            } else {
                throw new RuntimeException("Failed to send meal consumption email");
            }
        } catch (Exception e) {
            log.error("❌ Error sending meal consumption email to {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    // ==================== WEEKLY SUMMARY EMAIL ====================

    @Async
    public CompletableFuture<Void> sendWeeklySummaryEmail(String to, String firstName, int mealsConsumed,
                                                          String totalCalories) {
        try {
            String subject = "Resumo Semanal de Nutrição";
            String dashboardUrl = frontEndUrl + "/dashboard";
            String htmlContent = buildWeeklySummaryEmailTemplate(firstName, mealsConsumed, totalCalories, dashboardUrl);

            boolean success = sendEmailViaBrevo(to, subject, htmlContent);
            if (success) {
                log.info("✅ Weekly summary email sent successfully to: {}", to);
                return CompletableFuture.completedFuture(null);
            } else {
                throw new RuntimeException("Failed to send weekly summary email");
            }
        } catch (Exception e) {
            log.error("❌ Error sending weekly summary email to {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    // ==================== SUPPLEMENT REMINDER EMAIL ====================

    @Async
    public CompletableFuture<Void> sendSupplementReminderEmail(String toEmail, String toName, String supplementName,
                                                               String dosageTime, String recommendedDosage) {
        try {
            String subject = "⏰ Lembrete: Hora do seu suplemento - " + supplementName;
            String htmlBody = buildSupplementReminderEmailBody(toName, supplementName, dosageTime, recommendedDosage);

            boolean success = sendEmailViaBrevo(toEmail, subject, htmlBody);
            if (success) {
                log.info("✅ Supplement reminder email sent successfully to: {} for supplement: {}", toEmail, supplementName);
                return CompletableFuture.completedFuture(null);
            } else {
                throw new RuntimeException("Failed to send supplement reminder email");
            }
        } catch (Exception e) {
            log.error("❌ Error sending supplement reminder email to {}: {}", toEmail, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    public boolean sendSupplementReminderEmailSync(String toEmail, String toName, String supplementName,
                                                   String dosageTime, String recommendedDosage) {
        try {
            String subject = "⏰ Lembrete: Hora do seu suplemento - " + supplementName;
            String htmlBody = buildSupplementReminderEmailBody(toName, supplementName, dosageTime, recommendedDosage);

            return sendEmailViaBrevo(toEmail, subject, htmlBody);
        } catch (Exception e) {
            log.error("❌ Error sending supplement reminder email (sync) to {}: {}", toEmail, e.getMessage());
            return false;
        }
    }

    // ==================== EMAIL TEMPLATES ====================

    private String buildConfirmationEmailTemplate(String firstName, String confirmationUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Confirme sua Conta</title>
                </head>
                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
                        <h1 style="color: #2196F3;">Confirme sua Conta</h1>
                        <p>Olá <strong>%s</strong>,</p>
                        <p>Obrigado por se registrar! Clique no botão abaixo para confirmar seu email:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #2196F3; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Confirmar Email</a>
                        </div>
                        <p>Ou copie e cole este link em seu navegador:</p>
                        <p style="word-break: break-all; color: #666;">%s</p>
                        <p style="margin-top: 30px; color: #666; font-size: 12px;">
                            Este link expirará em 24 horas. Se você não se registrou, pode ignorar este email.
                        </p>
                    </div>
                </body>
                </html>
                """, firstName, confirmationUrl, confirmationUrl);
    }

    private String buildPasswordResetEmailTemplate(String firstName, String resetUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Redefinição de Senha</title>
                </head>
                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
                        <h1 style="color: #f44336;">Redefinição de Senha</h1>
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
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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

    private String buildMealReminderEmailTemplate(String firstName, String mealName, String mealTime,
                                                  String mealDetails, String mealsUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
                            <div style="margin-top: 10px;">%s</div>
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
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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

    private String buildWeeklySummaryEmailTemplate(String firstName, int mealsConsumed, String totalCalories,
                                                   String dashboardUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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

    private String buildSupplementReminderEmailBody(String firstName, String supplementName,
                                                    String dosageTime, String recommendedDosage) {
        String supplementsUrl = frontEndUrl + "/supplements";

        return String.format("""
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Lembrete de Suplemento</title>
                </head>
                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
                        <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; border-radius: 8px 8px 0 0; color: white; text-align: center;">
                            <h1 style="margin: 0; font-size: 28px;">⏰ Lembrete de Suplemento</h1>
                        </div>
                        <div style="padding: 30px;">
                            <p style="font-size: 16px;">Olá <strong>%s</strong>! 👋</p>
                            <div style="background-color: white; padding: 20px; border-radius: 8px; border-left: 4px solid #667eea; margin: 20px 0;">
                                <h2 style="color: #667eea; margin-top: 0;">💊 %s</h2>
                                <p style="margin: 10px 0;"><strong>⏰ Horário:</strong> %s</p>
                                <p style="margin: 10px 0;"><strong>📋 Dosagem recomendada:</strong> %s</p>
                            </div>
                            <p style="color: #666; font-size: 14px; margin: 20px 0;">
                                💡 <strong>Dica:</strong> Não se esqueça de tomar seu suplemento com água e de acordo com as instruções.
                            </p>
                            <div style="text-align: center; margin: 30px 0;">
                                <a href="%s" style="background-color: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Ver Meus Suplementos</a>
                            </div>
                            <p style="margin-top: 30px; color: #666; font-size: 12px; text-align: center;">
                                ✅ Mantenha sua rotina de suplementação em dia!
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """, firstName, supplementName, dosageTime, recommendedDosage, supplementsUrl);
    }
}