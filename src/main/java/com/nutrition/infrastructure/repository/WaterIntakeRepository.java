package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.tracking.WaterIntake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Long> {

    // Buscar por usuário e data específica
    List<WaterIntake> findByUserAndIntakeDateOrderByIntakeTimeDesc(User user, LocalDate date);

    // Total consumido em uma data
    @Query("SELECT COALESCE(SUM(w.amountMl), 0) FROM WaterIntake w WHERE w.user = :user AND w.intakeDate = :date")
    BigDecimal getTotalIntakeByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT COUNT(w) FROM WaterIntake w WHERE w.user = :user AND w.intakeDate = :date")
    int countByUserAndIntakeDate(@Param("user") User user, @Param("date") LocalDate date);

    // Estatísticas dos últimos 7 dias
    @Query("SELECT AVG(daily.total) FROM (" +
            "SELECT DATE(w.intakeDate) as date, SUM(w.amountMl) as total " +
            "FROM WaterIntake w WHERE w.user = :user AND w.intakeDate >= :startDate " +
            "GROUP BY DATE(w.intakeDate)) daily")
    BigDecimal getAverageWeeklyIntake(@Param("user") User user, @Param("startDate") LocalDate startDate);

    @Query("SELECT DISTINCT w.intakeDate FROM WaterIntake w " +
            "WHERE w.user = :user " +
            "ORDER BY w.intakeDate DESC")
    List<LocalDate> findDistinctIntakeDatesByUserOrderByIntakeDateDesc(
            @Param("user") User user,
            Pageable pageable
    );
}