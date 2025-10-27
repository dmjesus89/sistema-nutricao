package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.tracking.WaterIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Long> {

    List<WaterIntake> findByUserAndIntakeDateOrderByIntakeTimeDesc(User user, LocalDate date);

    @Query("SELECT COALESCE(SUM(w.amountMl), 0) FROM WaterIntake w WHERE w.user = :user AND w.intakeDate = :date")
    BigDecimal getTotalIntakeByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT COUNT(w) FROM WaterIntake w WHERE w.user = :user AND w.intakeDate = :date")
    int countByUserAndIntakeDate(@Param("user") User user, @Param("date") LocalDate date);

}