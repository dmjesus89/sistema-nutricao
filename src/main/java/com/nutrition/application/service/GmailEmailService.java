package com.nutrition.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GmailEmailService {

//    private final JavaMailSender mailSender;
//
//    //@Value("${app.gmail.from}")
//    private String fromEmail;
//
//    //@Value("${app.frontend.url}")
//    private String frontEndUrl;
//
//    @Async
//    public CompletableFuture<Void> sendConfirmationEmail(String to, String firstName, String confirmationToken) {
//        try {
//            String subject = "Confirme sua conta - Sistema de Nutrição";
//            String confirmationUrl = frontEndUrl + "/auth/email-confirmed?token=" + confirmationToken;
//            String htmlContent = buildConfirmationEmailTemplate(firstName, confirmationUrl);
//
//            sendHtmlEmail(to, subject, htmlContent);
//            log.info("Confirmation email sent successfully to: {}", to);
//
//            return CompletableFuture.completedFuture(null);
//        } catch (Exception e) {
//            log.error("Error sending confirmation email to {}: {}", to, e.getMessage());
//            throw new RuntimeException("Failed to send confirmation email", e);
//        }
//    }
//
//    @Async
//    public CompletableFuture<Void> sendPasswordResetEmail(String to, String firstName, String resetToken) {
//        try {
//            String subject = "Redefinição de senha - Sistema de Nutrição";
//            String passwordResetConfirmationUrl = frontEndUrl + "/auth/password-reset-confirmed?token=" + resetToken;
//            String htmlContent = buildPasswordResetEmailTemplate(firstName, passwordResetConfirmationUrl);
//
//            sendHtmlEmail(to, subject, htmlContent);
//            log.info("Password reset email sent successfully to: {}", to);
//
//            return CompletableFuture.completedFuture(null);
//        } catch (Exception e) {
//            log.error("Error sending password reset email to {}: {}", to, e.getMessage());
//            throw new RuntimeException("Failed to send password reset email", e);
//        }
//    }
//
//    @Async
//    public CompletableFuture<Void> sendWelcomeEmail(String to, String firstName) {
//        try {
//            String subject = "Bem-vindo ao Sistema de Nutrição!";
//            String loginUrl = frontEndUrl + "/auth/login";
//            String htmlContent = buildWelcomeEmailTemplate(firstName, loginUrl);
//
//            sendHtmlEmail(to, subject, htmlContent);
//            log.info("Welcome email sent successfully to: {}", to);
//
//            return CompletableFuture.completedFuture(null);
//        } catch (Exception e) {
//            log.error("Error sending welcome email to {}: {}", to, e.getMessage());
//            return CompletableFuture.completedFuture(null);
//        }
//    }
//
//    @Async
//    public CompletableFuture<Void> sendMealReminderEmail(String to, String firstName, String mealName, String mealTime, String mealDetails) {
//        try {
//            String subject = "Lembrete de Refeição - " + mealName;
//            String mealsUrl = frontEndUrl + "/meals";
//            String htmlContent = buildMealReminderEmailTemplate(firstName, mealName, mealTime, mealDetails, mealsUrl);
//
//            sendHtmlEmail(to, subject, htmlContent);
//            log.info("Meal reminder email sent successfully to: {} for meal: {}", to, mealName);
//
//            return CompletableFuture.completedFuture(null);
//        } catch (Exception e) {
//            log.error("Error sending meal reminder email to {}: {}", to, e.getMessage());
//            return CompletableFuture.completedFuture(null);
//        }
//    }
//
//    @Async
//    public CompletableFuture<Void> sendMealConsumptionEmail(String to, String firstName, String mealName) {
//        try {
//            String subject = "Refeição Consumida - " + mealName;
//            String mealsUrl = frontEndUrl + "/meals";
//            String htmlContent = buildMealConsumptionEmailTemplate(firstName, mealName, mealsUrl);
//
//            sendHtmlEmail(to, subject, htmlContent);
//            log.info("Meal consumption email sent successfully to: {} for meal: {}", to, mealName);
//
//            return CompletableFuture.completedFuture(null);
//        } catch (Exception e) {
//            log.error("Error sending meal consumption email to {}: {}", to, e.getMessage());
//            return CompletableFuture.completedFuture(null);
//        }
//    }
//
//    @Async
//    public CompletableFuture<Void> sendWeeklySummaryEmail(String to, String firstName, int mealsConsumed, String totalCalories) {
//        try {
//            String subject = "Resumo Semanal de Nutrição";
//            String dashboardUrl = frontEndUrl + "/dashboard";
//            String htmlContent = buildWeeklySummaryEmailTemplate(firstName, mealsConsumed, totalCalories, dashboardUrl);
//
//            sendHtmlEmail(to, subject, htmlContent);
//            log.info("Weekly summary email sent successfully to: {}", to);
//
//            return CompletableFuture.completedFuture(null);
//        } catch (Exception e) {
//            log.error("Error sending weekly summary email to {}: {}", to, e.getMessage());
//            return CompletableFuture.completedFuture(null);
//        }
//    }
//
//    // Synchronous methods for email queue processing
//    public boolean sendConfirmationEmailSync(String to, String firstName, String confirmationToken) {
//        try {
//            String subject = "Confirme sua conta - Sistema de Nutrição";
//            String confirmationUrl = frontEndUrl + "/auth/email-confirmed?token=" + confirmationToken;
//            String htmlContent = buildConfirmationEmailTemplate(firstName, confirmationUrl);
//
//            sendHtmlEmail(to, subject, htmlContent);
//            log.info("Confirmation email sent successfully (sync) to: {}", to);
//            return true;
//        } catch (Exception e) {
//            log.error("Error sending confirmation email (sync) to {}: {}", to, e.getMessage());
//            return false;
//        }
//    }
//
//    public boolean sendWelcomeEmailSync(String to, String firstName) {
//        try {
//            String subject = "Bem-vindo ao Sistema de Nutrição!";
//            String loginUrl = frontEndUrl + "/auth/login";
//            String htmlContent = buildWelcomeEmailTemplate(firstName, loginUrl);
//
//            sendHtmlEmail(to, subject, htmlContent);
//            log.info("Welcome email sent successfully (sync) to: {}", to);
//            return true;
//        } catch (Exception e) {
//            log.error("Error sending welcome email (sync) to {}: {}", to, e.getMessage());
//            return false;
//        }
//    }
//
//    public boolean sendPasswordResetEmailSync(String to, String firstName, String resetToken) {
//        try {
//            String subject = "Redefinição de senha - Sistema de Nutrição";
//            String passwordResetConfirmationUrl = frontEndUrl + "/auth/password-reset-confirmed?token=" + resetToken;
//            String htmlContent = buildPasswordResetEmailTemplate(firstName, passwordResetConfirmationUrl);
//
//            sendHtmlEmail(to, subject, htmlContent);
//            log.info("Password reset email sent successfully (sync) to: {}", to);
//            return true;
//        } catch (Exception e) {
//            log.error("Error sending password reset email (sync) to {}: {}", to, e.getMessage());
//            return false;
//        }
//    }
//
//    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//        helper.setFrom(fromEmail);
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(htmlContent, true);
//
//        mailSender.send(message);
//    }
//
//    private String buildConfirmationEmailTemplate(String firstName, String confirmationUrl) {
//        return String.format("""
//                <!DOCTYPE html>
//                <html>
//                <head>
//                    <meta charset="UTF-8">
//                    <title>Confirme sua conta</title>
//                </head>
//                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
//                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
//                        <h1 style="color: #2c5530;">Bem-vindo ao Sistema de Nutrição!</h1>
//                        <p>Olá <strong>%s</strong>,</p>
//                        <p>Obrigado por se registrar! Para ativar sua conta, clique no botão abaixo:</p>
//                        <div style="text-align: center; margin: 30px 0;">
//                            <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Confirmar Conta</a>
//                        </div>
//                        <p>Ou copie e cole este link em seu navegador:</p>
//                        <p style="word-break: break-all; color: #666;">%s</p>
//                        <p style="margin-top: 30px; color: #666; font-size: 12px;">
//                            Este link expirará em 24 horas. Se você não solicitou este registro, pode ignorar este email.
//                        </p>
//                    </div>
//                </body>
//                </html>
//                """, firstName, confirmationUrl, confirmationUrl);
//    }
//
//    private String buildPasswordResetEmailTemplate(String firstName, String resetUrl) {
//        return String.format("""
//                <!DOCTYPE html>
//                <html>
//                <head>
//                    <meta charset="UTF-8">
//                    <title>Redefinir senha</title>
//                </head>
//                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
//                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
//                        <h1 style="color: #d32f2f;">Redefinição de Senha</h1>
//                        <p>Olá <strong>%s</strong>,</p>
//                        <p>Recebemos uma solicitação para redefinir sua senha. Clique no botão abaixo para criar uma nova senha:</p>
//                        <div style="text-align: center; margin: 30px 0;">
//                            <a href="%s" style="background-color: #f44336; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Redefinir Senha</a>
//                        </div>
//                        <p>Ou copie e cole este link em seu navegador:</p>
//                        <p style="word-break: break-all; color: #666;">%s</p>
//                        <p style="margin-top: 30px; color: #666; font-size: 12px;">
//                            Este link expirará em 1 hora. Se você não solicitou esta redefinição, pode ignorar este email.
//                        </p>
//                    </div>
//                </body>
//                </html>
//                """, firstName, resetUrl, resetUrl);
//    }
//
//    private String buildWelcomeEmailTemplate(String firstName, String loginUrl) {
//        return String.format("""
//                <!DOCTYPE html>
//                <html>
//                <head>
//                    <meta charset="UTF-8">
//                    <title>Bem-vindo!</title>
//                </head>
//                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
//                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
//                        <h1 style="color: #4CAF50;">Conta ativada com sucesso!</h1>
//                        <p>Olá <strong>%s</strong>,</p>
//                        <p>Sua conta foi ativada com sucesso! Agora você pode aproveitar todos os recursos do Sistema de Nutrição:</p>
//                        <ul style="color: #333;">
//                            <li>Controle seu peso e objetivos</li>
//                            <li>Planejamento de dietas personalizadas</li>
//                            <li>Acompanhamento de hidratação</li>
//                            <li>Relatórios de evolução</li>
//                        </ul>
//                        <p>Estamos aqui para ajudá-lo em sua jornada para uma vida mais saudável!</p>
//                        <div style="text-align: center; margin: 30px 0;">
//                            <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Fazer Login</a>
//                        </div>
//                    </div>
//                </body>
//                </html>
//                """, firstName, loginUrl);
//    }
//
//    private String buildMealReminderEmailTemplate(String firstName, String mealName, String mealTime, String mealDetails, String mealsUrl) {
//        return String.format("""
//                <!DOCTYPE html>
//                <html>
//                <head>
//                    <meta charset="UTF-8">
//                    <title>Lembrete de Refeição</title>
//                </head>
//                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
//                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
//                        <div style="text-align: center; margin-bottom: 20px;">
//                            <h1 style="color: #FF9800; margin: 0;">🍽️ Hora da Refeição!</h1>
//                        </div>
//                        <p>Olá <strong>%s</strong>,</p>
//                        <p>Este é um lembrete de que está na hora da sua refeição:</p>
//                        <div style="background-color: #fff3e0; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #FF9800;">
//                            <h2 style="margin: 0 0 10px 0; color: #F57C00;">%s</h2>
//                            <p style="margin: 5px 0; color: #666;"><strong>Horário:</strong> %s</p>
//                            <div style="margin-top: 10px;">
//                                %s
//                            </div>
//                        </div>
//                        <p>Não se esqueça de marcar esta refeição como consumida após se alimentar!</p>
//                        <div style="text-align: center; margin: 30px 0;">
//                            <a href="%s" style="background-color: #FF9800; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Ver Minhas Refeições</a>
//                        </div>
//                        <p style="margin-top: 30px; color: #666; font-size: 12px; text-align: center;">
//                            💡 Dica: Manter uma alimentação regular ajuda a alcançar seus objetivos de saúde!
//                        </p>
//                    </div>
//                </body>
//                </html>
//                """, firstName, mealName, mealTime, mealDetails, mealsUrl);
//    }
//
//    private String buildMealConsumptionEmailTemplate(String firstName, String mealName, String mealsUrl) {
//        return String.format("""
//                <!DOCTYPE html>
//                <html>
//                <head>
//                    <meta charset="UTF-8">
//                    <title>Refeição Consumida</title>
//                </head>
//                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
//                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
//                        <div style="text-align: center; margin-bottom: 20px;">
//                            <h1 style="color: #4CAF50; margin: 0;">✅ Refeição Registrada!</h1>
//                        </div>
//                        <p>Olá <strong>%s</strong>,</p>
//                        <p>Você acabou de marcar a refeição <strong>"%s"</strong> como consumida.</p>
//                        <div style="background-color: #e8f5e9; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #4CAF50;">
//                            <p style="margin: 0; color: #2e7d32;">🎯 Continue assim para manter seus objetivos nutricionais em dia!</p>
//                        </div>
//                        <div style="text-align: center; margin: 30px 0;">
//                            <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Ver Todas as Refeições</a>
//                        </div>
//                        <p style="margin-top: 30px; color: #666; font-size: 12px; text-align: center;">
//                            💪 Cada refeição registrada é um passo em direção aos seus objetivos!
//                        </p>
//                    </div>
//                </body>
//                </html>
//                """, firstName, mealName, mealsUrl);
//    }
//
//    private String buildWeeklySummaryEmailTemplate(String firstName, int mealsConsumed, String totalCalories, String dashboardUrl) {
//        return String.format("""
//                <!DOCTYPE html>
//                <html>
//                <head>
//                    <meta charset="UTF-8">
//                    <title>Resumo Semanal</title>
//                </head>
//                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
//                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
//                        <div style="text-align: center; margin-bottom: 20px;">
//                            <h1 style="color: #2196F3; margin: 0;">📊 Resumo Semanal</h1>
//                        </div>
//                        <p>Olá <strong>%s</strong>,</p>
//                        <p>Aqui está o resumo da sua semana:</p>
//                        <div style="background-color: #e3f2fd; padding: 20px; border-radius: 8px; margin: 20px 0;">
//                            <div style="margin: 15px 0; padding: 10px; background-color: white; border-radius: 4px;">
//                                <p style="margin: 0; color: #666;">🍽️ <strong>Refeições consumidas:</strong></p>
//                                <p style="margin: 5px 0 0 0; font-size: 24px; color: #1976D2; font-weight: bold;">%d</p>
//                            </div>
//                            <div style="margin: 15px 0; padding: 10px; background-color: white; border-radius: 4px;">
//                                <p style="margin: 0; color: #666;">🔥 <strong>Total de calorias:</strong></p>
//                                <p style="margin: 5px 0 0 0; font-size: 24px; color: #1976D2; font-weight: bold;">%s</p>
//                            </div>
//                        </div>
//                        <p style="color: #4CAF50; font-weight: bold; text-align: center;">Continue com o ótimo trabalho! 💪</p>
//                        <div style="text-align: center; margin: 30px 0;">
//                            <a href="%s" style="background-color: #2196F3; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Ver Dashboard</a>
//                        </div>
//                        <p style="margin-top: 30px; color: #666; font-size: 12px; text-align: center;">
//                            📈 Acompanhe seu progresso e mantenha-se motivado!
//                        </p>
//                    </div>
//                </body>
//                </html>
//                """, firstName, mealsConsumed, totalCalories, dashboardUrl);
//    }
//
//    /**
//     * Send supplement reminder email (Async)
//     */
//    @Async
//    public CompletableFuture<Void> sendSupplementReminderEmail(String toEmail, String toName, String supplementName,
//                                                               String dosageTime, String recommendedDosage) {
//        try {
//            String subject = "⏰ Lembrete: Hora do seu suplemento - " + supplementName;
//            String htmlBody = buildSupplementReminderEmailBody(toName, supplementName, dosageTime, recommendedDosage);
//
//            sendHtmlEmail(toEmail, subject, htmlBody);
//            log.info("Supplement reminder email sent successfully to: {} for supplement: {}", toEmail, supplementName);
//
//            return CompletableFuture.completedFuture(null);
//        } catch (Exception e) {
//            log.error("Error sending supplement reminder email to {}: {}", toEmail, e.getMessage());
//            return CompletableFuture.completedFuture(null);
//        }
//    }
//
//    /**
//     * Send supplement reminder email (Sync) - for queue processing
//     */
//    public boolean sendSupplementReminderEmailSync(String toEmail, String toName, String supplementName,
//                                                   String dosageTime, String recommendedDosage) {
//        try {
//            String subject = "⏰ Lembrete: Hora do seu suplemento - " + supplementName;
//            String htmlBody = buildSupplementReminderEmailBody(toName, supplementName, dosageTime, recommendedDosage);
//
//            sendHtmlEmail(toEmail, subject, htmlBody);
//            log.info("Supplement reminder email sent successfully (sync) to: {} for supplement: {}", toEmail, supplementName);
//            return true;
//        } catch (Exception e) {
//            log.error("Error sending supplement reminder email (sync) to {}: {}", toEmail, e.getMessage());
//            return false;
//        }
//    }
//
//    /**
//     * Build HTML body for supplement reminder email
//     */
//    private String buildSupplementReminderEmailBody(String firstName, String supplementName,
//                                                    String dosageTime, String recommendedDosage) {
//        String supplementsUrl = frontEndUrl + "/supplements";
//
//        return String.format("""
//                <!DOCTYPE html>
//                <html lang="pt-BR">
//                <head>
//                    <meta charset="UTF-8">
//                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//                    <title>Lembrete de Suplemento</title>
//                    <style>
//                        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
//                        .container { max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
//                        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; border-radius: 8px 8px 0 0; color: white; text-align: center; }
//                        .header h1 { margin: 0; font-size: 28px; }
//                        .content { padding: 30px; }
//                        .supplement-card { background-color: white; padding: 20px; border-radius: 8px; border-left: 4px solid #667eea; margin: 20px 0; }
//                        .supplement-card h2 { color: #667eea; margin-top: 0; }
//                        .info-row { margin: 10px 0; font-size: 16px; }
//                        .info-label { font-weight: bold; color: #333; }
//                        .button { display: inline-block; background-color: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; margin: 20px 0; }
//                        .button:hover { background-color: #764ba2; }
//                        .tip { background-color: #f9f9f9; padding: 15px; border-radius: 4px; margin: 20px 0; border-left: 4px solid #667eea; color: #666; font-size: 14px; }
//                        .footer { color: #666; font-size: 12px; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; text-align: center; }
//                    </style>
//                </head>
//                <body>
//                    <div class="container">
//                        <div class="header">
//                            <h1>⏰ Lembrete de Suplemento</h1>
//                        </div>
//                        <div class="content">
//                            <p style="font-size: 16px;">Olá <strong>%s</strong>! 👋</p>
//                            <p>É hora de tomar seu suplemento! Não se esqueça de manter sua rotina em dia.</p>
//
//                            <div class="supplement-card">
//                                <h2>💊 %s</h2>
//                                <div class="info-row">
//                                    <span class="info-label">⏰ Horário:</span> %s
//                                </div>
//                                <div class="info-row">
//                                    <span class="info-label">📋 Dosagem recomendada:</span> %s
//                                </div>
//                            </div>
//
//                            <div class="tip">
//                                <strong>💡 Dica:</strong> Não se esqueça de tomar seu suplemento com água e de acordo com as instruções na embalagem.
//                            </div>
//
//                            <div style="text-align: center;">
//                                <a href="%s" class="button">Ver Meus Suplementos</a>
//                            </div>
//
//                            <div class="footer">
//                                <p>✅ Mantenha sua rotina de suplementação em dia para melhores resultados!</p>
//                                <p>© 2024 Sistema de Nutrição. Todos os direitos reservados.</p>
//                            </div>
//                        </div>
//                    </div>
//                </body>
//                </html>
//                """, firstName, supplementName, dosageTime, recommendedDosage, supplementsUrl);
//    }
//
//// OPTIONAL: Additional supplement-related methods
//
//    /**
//     * Send supplement intake confirmation email
//     */
//    @Async
//    public CompletableFuture<Void> sendSupplementIntakeConfirmationEmail(String toEmail, String firstName, String supplementName) {
//        try {
//            String subject = "✅ Suplemento Registrado - " + supplementName;
//            String htmlBody = buildSupplementIntakeConfirmationEmailBody(firstName, supplementName);
//
//            sendHtmlEmail(toEmail, subject, htmlBody);
//            log.info("Supplement intake confirmation email sent successfully to: {}", toEmail);
//
//            return CompletableFuture.completedFuture(null);
//        } catch (Exception e) {
//            log.error("Error sending supplement intake confirmation email to {}: {}", toEmail, e.getMessage());
//            return CompletableFuture.completedFuture(null);
//        }
//    }
//
//    /**
//     * Build HTML body for supplement intake confirmation
//     */
//    private String buildSupplementIntakeConfirmationEmailBody(String firstName, String supplementName) {
//        String supplementsUrl = frontEndUrl + "/supplements";
//
//        return String.format("""
//                <!DOCTYPE html>
//                <html lang="pt-BR">
//                <head>
//                    <meta charset="UTF-8">
//                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//                    <title>Suplemento Registrado</title>
//                </head>
//                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
//                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
//                        <div style="text-align: center; margin-bottom: 20px;">
//                            <h1 style="color: #667eea; margin: 0;">✅ Suplemento Registrado!</h1>
//                        </div>
//                        <p>Olá <strong>%s</strong>,</p>
//                        <p>Você acabou de registrar a ingestão do suplemento <strong>"%s"</strong>.</p>
//                        <div style="background-color: #f0f4ff; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #667eea;">
//                            <p style="margin: 0; color: #667eea; font-weight: bold;">🎯 Parabéns! Continue mantendo sua rotina de suplementação em dia!</p>
//                        </div>
//                        <div style="text-align: center; margin: 30px 0;">
//                            <a href="%s" style="background-color: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Ver Meus Suplementos</a>
//                        </div>
//                        <p style="margin-top: 30px; color: #666; font-size: 12px; text-align: center;">
//                            💪 Cada suplemento registrado é um passo em direção à sua melhor saúde!
//                        </p>
//                    </div>
//                </body>
//                </html>
//                """, firstName, supplementName, supplementsUrl);
//    }
//
//    /**
//     * Send weekly supplement summary email
//     */
//    @Async
//    public CompletableFuture<Void> sendWeeklySupplementSummaryEmail(String toEmail, String firstName,
//                                                                    int supplementsTaken, int supplementsScheduled) {
//        try {
//            String subject = "📊 Resumo Semanal de Suplementação";
//            String htmlBody = buildWeeklySupplementSummaryEmailBody(firstName, supplementsTaken, supplementsScheduled);
//
//            sendHtmlEmail(toEmail, subject, htmlBody);
//            log.info("Weekly supplement summary email sent successfully to: {}", toEmail);
//
//            return CompletableFuture.completedFuture(null);
//        } catch (Exception e) {
//            log.error("Error sending weekly supplement summary email to {}: {}", toEmail, e.getMessage());
//            return CompletableFuture.completedFuture(null);
//        }
//    }
//
//    /**
//     * Build HTML body for weekly supplement summary
//     */
//    private String buildWeeklySupplementSummaryEmailBody(String firstName, int supplementsTaken, int supplementsScheduled) {
//        double adherencePercentage = supplementsScheduled > 0 ? (supplementsTaken * 100.0 / supplementsScheduled) : 0;
//        String dashboardUrl = frontEndUrl + "/dashboard";
//
//        return String.format("""
//                <!DOCTYPE html>
//                <html lang="pt-BR">
//                <head>
//                    <meta charset="UTF-8">
//                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//                    <title>Resumo Semanal de Suplementação</title>
//                </head>
//                <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;">
//                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;">
//                        <div style="text-align: center; margin-bottom: 20px;">
//                            <h1 style="color: #667eea; margin: 0;">📊 Resumo Semanal de Suplementação</h1>
//                        </div>
//                        <p>Olá <strong>%s</strong>,</p>
//                        <p>Aqui está o resumo da sua semana de suplementação:</p>
//
//                        <div style="background-color: #f0f4ff; padding: 20px; border-radius: 8px; margin: 20px 0;">
//                            <div style="margin: 15px 0; padding: 10px; background-color: white; border-radius: 4px;">
//                                <p style="margin: 0; color: #666;">💊 <strong>Suplementos tomados:</strong></p>
//                                <p style="margin: 5px 0 0 0; font-size: 24px; color: #667eea; font-weight: bold;">%d</p>
//                            </div>
//                            <div style="margin: 15px 0; padding: 10px; background-color: white; border-radius: 4px;">
//                                <p style="margin: 0; color: #666;">📋 <strong>Suplementos agendados:</strong></p>
//                                <p style="margin: 5px 0 0 0; font-size: 24px; color: #667eea; font-weight: bold;">%d</p>
//                            </div>
//                            <div style="margin: 15px 0; padding: 10px; background-color: white; border-radius: 4px;">
//                                <p style="margin: 0; color: #666;">📈 <strong>Taxa de aderência:</strong></p>
//                                <p style="margin: 5px 0 0 0; font-size: 24px; color: #667eea; font-weight: bold;">%.1f%%</p>
//                            </div>
//                        </div>
//
//                        <p style="color: #667eea; font-weight: bold; text-align: center;">Continue com o excelente trabalho! 💪</p>
//
//                        <div style="text-align: center; margin: 30px 0;">
//                            <a href="%s" style="background-color: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Ver Dashboard</a>
//                        </div>
//
//                        <p style="margin-top: 30px; color: #666; font-size: 12px; text-align: center;">
//                            💡 Manter uma boa aderência à sua rotina de suplementação é importante para alcançar seus objetivos de saúde!
//                        </p>
//                    </div>
//                </body>
//                </html>
//                """, firstName, supplementsTaken, supplementsScheduled, adherencePercentage, dashboardUrl);
//    }
}