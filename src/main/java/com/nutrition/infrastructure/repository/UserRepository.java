package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.enabled = true")
    java.util.List<User> findByEnabledTrue();

    @Query("SELECT u FROM User u WHERE u.enabled = true AND u.welcomeEmailSent = false")
    java.util.List<User> findEnabledUsersWithoutWelcomeEmail();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'USER'")
    long countRegularUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ADMIN'")
    long countAdminUsers();
}