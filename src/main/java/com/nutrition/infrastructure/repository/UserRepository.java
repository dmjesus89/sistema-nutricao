package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.enabled = true, u.emailConfirmed = true WHERE u.id = :userId")
    void confirmEmail(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    void updatePassword(@Param("userId") Long userId, @Param("password") String password);

    @Query("SELECT u FROM User u WHERE u.emailConfirmed = true AND u.enabled = true")
    Optional<User> findActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'USER'")
    long countRegularUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ADMIN'")
    long countAdminUsers();
}