package com.nutrition.application.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailService2 {

    @Value("${resend.api.key}")
    private String resendApiKey;

    private static final String RESEND_URL = "https://api.resend.com/emails";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public void sendMealConsumptionEmail(String userEmail, String userName, String mealName) {
        try {
            Map<String, String> emailData = new HashMap<>();
            emailData.put("from", "onboarding@resend.dev"); // Change this after domain verification
            emailData.put("to", userEmail);
            emailData.put("subject", "Refeição Consumida: " + mealName);
            emailData.put("html", buildMealConsumptionEmailHtml(userName, mealName));

            sendEmail(emailData);
            log.info("Meal consumption email sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send meal consumption email to: {}", userEmail, e);
            // Don't throw exception - email failure shouldn't break the main flow
        }
    }

    public void sendWeeklySummaryEmail(String userEmail, String userName, int mealsConsumed, String totalCalories) {
        try {
            Map<String, String> emailData = new HashMap<>();
            emailData.put("from", "onboarding@resend.dev");
            emailData.put("to", userEmail);
            emailData.put("subject", "Resumo Semanal de Nutrição");
            emailData.put("html", buildWeeklySummaryHtml(userName, mealsConsumed, totalCalories));

            sendEmail(emailData);
            log.info("Weekly summary email sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send weekly summary email to: {}", userEmail, e);
        }
    }

    private void sendEmail(Map<String, String> emailData) throws Exception {
        String json = gson.toJson(emailData);

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(RESEND_URL)
                .addHeader("Authorization", "Bearer " + resendApiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Email API error: {} - {}", response.code(), response.body().string());
                throw new RuntimeException("Failed to send email: " + response.code());
            }
            log.debug("Email sent successfully: {}", response.body().string());
        }
    }

    private String buildMealConsumptionEmailHtml(String userName, String mealName) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🍽️ Refeição Registrada!</h1>
                        </div>
                        <div class="content">
                            <p>Olá, <strong>%s</strong>!</p>
                            <p>Você acabou de marcar a refeição <strong>"%s"</strong> como consumida.</p>
                            <p>Continue assim para manter seus objetivos nutricionais em dia! 💪</p>
                        </div>
                        <div class="footer">
                            <p>Nutrition Tracker App</p>
                        </div>
                    </div>
                </body>
                </html>
                """, userName, mealName);
    }

    private String buildWeeklySummaryHtml(String userName, int mealsConsumed, String totalCalories) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }
                        .stats { background-color: #f0f0f0; padding: 15px; margin: 15px 0; border-radius: 5px; }
                        .stat-item { margin: 10px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>📊 Resumo Semanal</h1>
                        </div>
                        <div class="content">
                            <p>Olá, <strong>%s</strong>!</p>
                            <p>Aqui está o resumo da sua semana:</p>
                            <div class="stats">
                                <div class="stat-item">🍽️ <strong>Refeições consumidas:</strong> %d</div>
                                <div class="stat-item">🔥 <strong>Total de calorias:</strong> %s</div>
                            </div>
                            <p>Continue com o ótimo trabalho!</p>
                        </div>
                    </div>
                </body>
                </html>
                """, userName, mealsConsumed, totalCalories);
    }
}