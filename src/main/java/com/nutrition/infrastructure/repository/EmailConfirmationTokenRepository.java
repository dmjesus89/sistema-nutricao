package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.EmailConfirmationToken;
import com.nutrition.domain.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {

    Optional<EmailConfirmationToken> findByToken(String token);

    @Query("SELECT t FROM EmailConfirmationToken t WHERE t.user = :user AND t.confirmedAt IS NULL ORDER BY t.createdAt DESC")
    List<EmailConfirmationToken> findPendingTokensByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM EmailConfirmationToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
}

