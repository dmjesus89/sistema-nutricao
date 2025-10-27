package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.Supplement;
import com.nutrition.domain.entity.food.UserSupplement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSupplementRepository extends JpaRepository<UserSupplement, Long> {

    Optional<UserSupplement> findByUserAndSupplement(User user, Supplement supplement);

    boolean existsByUserAndSupplement(User user, Supplement supplement);

    @Modifying
    void deleteByUserAndSupplement(User user, Supplement supplement);

    long countByUser(User user);

    @Query("SELECT us FROM UserSupplement us JOIN FETCH us.supplement s WHERE us.user = :user AND " +
            "s.active = true ORDER BY us.createdAt DESC")
    List<UserSupplement> findByUserWithSupplementDetails(@Param("user") User user);

    @Query("SELECT us FROM UserSupplement us JOIN FETCH us.supplement s WHERE us.user = :user AND " +
            "us.frequency = :frequency AND s.active = true ORDER BY us.createdAt DESC")
    List<UserSupplement> findByUserAndFrequencyWithDetails(@Param("user") User user,
                                                           @Param("frequency") UserSupplement.Frequency frequency);

    @Query("SELECT us FROM UserSupplement us JOIN FETCH us.supplement s JOIN FETCH us.user u " +
            "WHERE us.emailReminderEnabled = true AND s.active = true")
    List<UserSupplement> findByEmailReminderEnabledTrue();

}
