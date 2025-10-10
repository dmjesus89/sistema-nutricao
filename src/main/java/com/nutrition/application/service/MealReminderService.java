package com.nutrition.application.service;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.meal.Meal;
import com.nutrition.domain.entity.meal.MealFood;
import com.nutrition.infrastructure.repository.MealRepository;
import com.nutrition.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MealReminderService {

    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Scheduled task that runs every 5 minutes to check for meal reminders
     * Cron expression: At second 0, every 5 minutes
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional(readOnly = true)
    public void checkMealReminders() {
        log.debug("Running meal reminder check...");

        LocalTime now = LocalTime.now();
        LocalTime reminderWindow = now.plusMinutes(5); // Look ahead 5 minutes

        try {
            // Get all active users who have confirmed their email
            List<User> activeUsers = userRepository.findAll().stream()
                .filter(User::isEnabled)
                .collect(Collectors.toList());

            for (User user : activeUsers) {
                checkUserMealReminders(user, now, reminderWindow);
            }

            log.debug("Meal reminder check completed");
        } catch (Exception e) {
            log.error("Error during meal reminder check: {}", e.getMessage(), e);
        }
    }

    /**
     * Check meal reminders for a specific user
     */
    private void checkUserMealReminders(User user, LocalTime currentTime, LocalTime reminderWindow) {
        try {
            // Get all template meals for this user (not one-time meals)
            List<Meal> userMeals = mealRepository.findByUserOrderByMealTimeAsc(user).stream()
                .filter(Meal::getIsTemplate)
                .collect(Collectors.toList());

            for (Meal meal : userMeals) {
                LocalTime mealTime = meal.getMealTime();

                // Check if meal time is within the next 5 minutes
                if (isMealTimeInWindow(mealTime, currentTime, reminderWindow)) {
                    // Check if meal was already consumed today
                    boolean alreadyConsumed = meal.getConsumptions().stream()
                        .anyMatch(consumption -> consumption.getConsumptionDate().equals(LocalDate.now()));

                    if (!alreadyConsumed) {
                        sendMealReminder(user, meal);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error checking meal reminders for user {}: {}", user.getEmail(), e.getMessage());
        }
    }

    /**
     * Check if meal time is within the reminder window
     */
    private boolean isMealTimeInWindow(LocalTime mealTime, LocalTime currentTime, LocalTime reminderWindow) {
        // Check if mealTime is between currentTime and reminderWindow
        return !mealTime.isBefore(currentTime) && mealTime.isBefore(reminderWindow);
    }

    /**
     * Send meal reminder email to user
     */
    private void sendMealReminder(User user, Meal meal) {
        try {
            String mealName = meal.getName();
            String mealTime = meal.getMealTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String mealDetails = buildMealDetails(meal);

            emailService.sendMealReminderEmail(
                user.getEmail(),
                user.getFirstName(),
                mealName,
                mealTime,
                mealDetails
            );

            log.info("Meal reminder sent to user {} for meal: {}", user.getEmail(), mealName);
        } catch (Exception e) {
            log.error("Error sending meal reminder for user {}: {}", user.getEmail(), e.getMessage());
        }
    }

    /**
     * Build HTML formatted meal details
     */
    private String buildMealDetails(Meal meal) {
        if (meal.getFoods() == null || meal.getFoods().isEmpty()) {
            return "<p style='color: #999;'>Nenhum alimento cadastrado</p>";
        }

        StringBuilder details = new StringBuilder();
        details.append("<p style='margin: 5px 0;'><strong>Alimentos:</strong></p>");
        details.append("<ul style='margin: 5px 0; padding-left: 20px;'>");

        for (MealFood mealFood : meal.getFoods()) {
            String foodName = mealFood.getFood().getName();
            BigDecimal quantity = mealFood.getQuantity();

            details.append(String.format("<li>%s - %.0fg</li>", foodName, quantity));
        }

        details.append("</ul>");

        // Add nutritional summary
        details.append(String.format(
            "<p style='margin: 10px 0 0 0; font-size: 12px; color: #666;'>" +
            "<strong>Total:</strong> %.0f kcal | Proteína: %.1fg | Carbs: %.1fg | Gordura: %.1fg</p>",
            meal.getTotalCalories().doubleValue(),
            meal.getTotalProtein().doubleValue(),
            meal.getTotalCarbs().doubleValue(),
            meal.getTotalFat().doubleValue()
        ));

        return details.toString();
    }

    /**
     * Manual method to send immediate reminder for a specific meal
     * Can be called from a controller endpoint if needed
     */
    public void sendImmediateMealReminder(Long mealId, User user) {
        try {
            Meal meal = mealRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

            sendMealReminder(user, meal);
        } catch (Exception e) {
            log.error("Error sending immediate meal reminder: {}", e.getMessage());
            throw new RuntimeException("Failed to send meal reminder", e);
        }
    }
}
