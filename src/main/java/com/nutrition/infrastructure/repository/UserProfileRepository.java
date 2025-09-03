package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.profile.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser(User user);

    Optional<UserProfile> findByUserId(Long userId);

    boolean existsByUser(User user);

    @Query("SELECT up FROM UserProfile up WHERE up.user.email = :email")
    Optional<UserProfile> findByUserEmail(@Param("email") String email);

    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.basalMetabolicRate IS NOT NULL AND up.totalDailyEnergyExpenditure IS NOT NULL")
    long countProfilesWithCalculatedMetrics();

    @Query("SELECT AVG(up.currentWeight) FROM UserProfile up WHERE up.currentWeight IS NOT NULL")
    Double getAverageWeight();

    @Query("SELECT AVG(up.height) FROM UserProfile up WHERE up.height IS NOT NULL")
    Double getAverageHeight();
}

