package com.nutrition.application.service;


import com.nutrition.infrastructure.repository.EmailConfirmationTokenRepository;
import com.nutrition.infrastructure.repository.FoodRepository;
import com.nutrition.infrastructure.repository.PasswordResetTokenRepository;
import com.nutrition.infrastructure.repository.RefreshTokenRepository;
import com.nutrition.infrastructure.repository.SupplementRepository;
import com.nutrition.infrastructure.repository.UserFoodPreferenceRepository;
import com.nutrition.infrastructure.repository.UserProfileRepository;
import com.nutrition.infrastructure.repository.UserRepository;
import com.nutrition.infrastructure.repository.UserSupplementRepository;
import com.nutrition.infrastructure.repository.WeightHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final FoodRepository foodRepository;
    private final SupplementRepository supplementRepository;
    private final UserFoodPreferenceRepository foodPreferenceRepository;
    private final UserSupplementRepository userSupplementRepository;
    private final WeightHistoryRepository weightHistoryRepository;
    private final EmailConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public Map<String, Object> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Estatísticas gerais
            stats.put("total_users", userRepository.count());
            stats.put("active_users", userRepository.countRegularUsers());
            stats.put("admin_users", userRepository.countAdminUsers());
            stats.put("total_profiles", profileRepository.count());
            stats.put("profiles_with_calculations", profileRepository.countProfilesWithCalculatedMetrics());

            // Estatísticas de alimentos
            stats.put("total_foods", foodRepository.countActiveFoods());

            // Estatísticas de suplementos
            stats.put("total_supplements", supplementRepository.countActiveSupplements());

            // Estatísticas de preferências
            stats.put("food_preferences", foodPreferenceRepository.count());
            stats.put("supplement_preferences", userSupplementRepository.count()); // Updated to use frequency-based tracking

            // Estatísticas de peso
            stats.put("weight_records", weightHistoryRepository.count());

            log.info("Dashboard stats generated successfully");
            return stats;

        } catch (Exception e) {
            log.error("Error generating dashboard stats: {}", e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> getFoodStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("total_active", foodRepository.countActiveFoods());

            List<Object[]> categoryStats = foodRepository.countFoodsByCategory();
            Map<String, Long> categoryCounts = new HashMap<>();
            for (Object[] stat : categoryStats) {
                categoryCounts.put(stat[0].toString(), ((Number) stat[1]).longValue());
            }
            stats.put("by_category", categoryCounts);

            log.info("Food statistics generated successfully");
            return stats;
        } catch (Exception e) {
            log.error("Error generating food statistics: {}", e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> getSupplementStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("total_active", supplementRepository.countActiveSupplements());

            List<Object[]> categoryStats = supplementRepository.countSupplementsByCategory();
            Map<String, Long> categoryCounts = new HashMap<>();
            for (Object[] stat : categoryStats) {
                categoryCounts.put(stat[0].toString(), ((Number) stat[1]).longValue());
            }
            stats.put("by_category", categoryCounts);

            log.info("Supplement statistics generated successfully");
            return stats;
        } catch (Exception e) {
            log.error("Error generating supplement statistics: {}", e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> getUserStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("total_users", userRepository.count());
            stats.put("regular_users", userRepository.countRegularUsers());
            stats.put("admin_users", userRepository.countAdminUsers());

            stats.put("users_with_profile", profileRepository.count());
            stats.put("profiles_with_calculations", profileRepository.countProfilesWithCalculatedMetrics());

            Double avgWeight = profileRepository.getAverageWeight();
            Double avgHeight = profileRepository.getAverageHeight();
            stats.put("average_weight", avgWeight != null ? avgWeight : 0.0);
            stats.put("average_height", avgHeight != null ? avgHeight : 0.0);

            log.info("User statistics generated successfully");
            return stats;
        } catch (Exception e) {
            log.error("Error generating user statistics: {}", e.getMessage());
            throw e;
        }
    }


    @Transactional
    public void performMaintenance() {
        try {
            LocalDateTime now = LocalDateTime.now();

            confirmationTokenRepository.deleteExpiredTokens(now);
            passwordResetTokenRepository.deleteExpiredTokens(now);
            refreshTokenRepository.cleanupTokens(now);
            // apagar confirmação de tokens

            log.info("Database maintenance completed successfully");
        } catch (Exception e) {
            log.error("Error performing database maintenance: {}", e.getMessage());
            throw e;
        }
    }
}