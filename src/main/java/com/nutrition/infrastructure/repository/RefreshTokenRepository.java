package com.nutrition.infrastructure.repository;


import com.nutrition.domain.entity.auth.RefreshToken;
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
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT t FROM RefreshToken t WHERE t.user = :user AND t.revoked = false")
    List<RefreshToken> findValidTokensByUser(@Param("user") User user);

    @Modifying
    @Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.user = :user")
    void revokeAllUserTokens(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :now OR t.revoked = true")
    void cleanupTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.token = :token")
    void revokeRefreshToken(@Param("token") String token);
}