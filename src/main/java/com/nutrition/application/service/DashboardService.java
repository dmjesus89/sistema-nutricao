package com.nutrition.application.service;


import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.UserFoodPreference;
import com.nutrition.domain.entity.food.UserSupplementPreference;
import com.nutrition.domain.entity.profile.UserProfile;
import com.nutrition.infrastructure.repository.EmailConfirmationTokenRepository;
import com.nutrition.infrastructure.repository.FoodRepository;
import com.nutrition.infrastructure.repository.PasswordResetTokenRepository;
import com.nutrition.infrastructure.repository.RefreshTokenRepository;
import com.nutrition.infrastructure.repository.SupplementRepository;
import com.nutrition.infrastructure.repository.UserFoodPreferenceRepository;
import com.nutrition.infrastructure.repository.UserProfileRepository;
import com.nutrition.infrastructure.repository.UserRepository;
import com.nutrition.infrastructure.repository.UserSupplementPreferenceRepository;
import com.nutrition.infrastructure.repository.WeightHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final UserProfileRepository profileRepository;
    private final SupplementRepository supplementRepository;
    private final UserFoodPreferenceRepository foodPreferenceRepository;
    private final WeightHistoryRepository weightHistoryRepository;

    public Map<String, Object> getDashboardStats(User user) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalFoodPreferences", foodPreferenceRepository.findByUserAndPreferenceTypes(user, Arrays.stream(UserFoodPreference.PreferenceType.values()).toList()).size());
            stats.put("totalSupplementPreferences", supplementRepository.findByUserAndPreferenceTypes(user, Arrays.stream(UserSupplementPreference.PreferenceType.values()).toList()).size());
            stats.put("favoriteFoods", foodPreferenceRepository.findByUserAndPreferenceTypeFavorite(user).size());
            stats.put("favoriteSupplements", supplementRepository.findByUserAndPreferenceTypeFavorite(user).size());
            stats.put("currentSupplements", supplementRepository.findUserCurrentSupplements(user).size());


            stats.put("weightRecords", weightHistoryRepository.countByUser(user));
            Optional<UserProfile> profile = profileRepository.findByUser(user);

            if (profile.isPresent()) {
                LocalDate profileCreatedDate = profile.get().getCreatedAt().toLocalDate();
                LocalDate today = LocalDate.now();

                long daysActive = ChronoUnit.DAYS.between(profileCreatedDate, today);
                stats.put("daysActive", (int) daysActive);
            } else {
                stats.put("daysActive", 0);
            }


            log.info("Dashboard stats generated successfully");
            return stats;

        } catch (Exception e) {
            log.error("Error generating dashboard stats: {}", e.getMessage());
            throw e;
        }
    }

}