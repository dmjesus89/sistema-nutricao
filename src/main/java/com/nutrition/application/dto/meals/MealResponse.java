package com.nutrition.application.dto.meals;

import com.nutrition.domain.entity.Meal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealResponse {

    private Long id;
    private Long mealPlanId;
    private Meal.MealType mealType;
    private String mealTypeDisplay;
    private String name;
    private String description;

    // Target macros
    private BigDecimal targetCalories;
    private BigDecimal targetCarbs;
    private BigDecimal targetProtein;
    private BigDecimal targetFat;

    // Consumed macros
    private BigDecimal consumedCalories;
    private BigDecimal consumedCarbs;
    private BigDecimal consumedProtein;
    private BigDecimal consumedFat;

    // Status
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private LocalTime scheduledTime;
    private Integer orderIndex;

    // Check-in info
    private Boolean isCheckedIn;
    private Double completionPercentage;
    private Integer satisfactionRating;

    // Related data
    private List<MealFoodResponse> foods;
    private MealCheckInResponse checkIn;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
