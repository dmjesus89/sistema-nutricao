package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.profile.WeightHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface WeightHistoryRepository extends JpaRepository<WeightHistory, Long> {

    List<WeightHistory> findByUserOrderByRecordedDateDesc(User user);

    List<WeightHistory> findByUserOrderByRecordedDateAsc(User user);

    @Query("SELECT wh FROM WeightHistory wh WHERE wh.user = :user AND wh.recordedDate = :date")
    Optional<WeightHistory> findByUserAndRecordedDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT wh FROM WeightHistory wh WHERE wh.user = :user ORDER BY wh.recordedDate DESC LIMIT 1")
    Optional<WeightHistory> findLatestByUser(@Param("user") User user);

    @Query("SELECT wh FROM WeightHistory wh WHERE wh.user = :user " +
            "AND wh.recordedDate BETWEEN :startDate AND :endDate ORDER BY wh.recordedDate ASC")
    List<WeightHistory> findByUserAndDateRange(@Param("user") User user,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT wh FROM WeightHistory wh WHERE wh.user = :user " +
            "AND wh.recordedDate >= :fromDate ORDER BY wh.recordedDate DESC")
    List<WeightHistory> findRecentByUser(@Param("user") User user, @Param("fromDate") LocalDate fromDate);

    @Query("SELECT MIN(wh.weight) FROM WeightHistory wh WHERE wh.user = :user")
    Optional<BigDecimal> findMinWeightByUser(@Param("user") User user);

    @Query("SELECT MAX(wh.weight) FROM WeightHistory wh WHERE wh.user = :user")
    Optional<BigDecimal> findMaxWeightByUser(@Param("user") User user);

    @Query("SELECT COUNT(wh) FROM WeightHistory wh WHERE wh.user = :user")
    long countByUser(@Param("user") User user);

    @Query("SELECT wh FROM WeightHistory wh WHERE wh.user = :user AND wh.recordedDate >= (SELECT u.createdAt FROM User u WHERE u = :user) ORDER BY wh.recordedDate ASC LIMIT 1")
    Optional<WeightHistory> findFirstWeightByUser(@Param("user") User user);

    // Estatísticas mensais
    @Query("SELECT EXTRACT(YEAR FROM wh.recordedDate) as year, EXTRACT(MONTH FROM wh.recordedDate) as month, AVG(wh.weight) as avgWeight FROM WeightHistory wh WHERE wh.user = :user GROUP BY EXTRACT(YEAR FROM wh.recordedDate), EXTRACT(MONTH FROM wh.recordedDate) ORDER BY year DESC, month DESC")
    List<Object[]> findMonthlyAveragesByUser(@Param("user") User user);

    // Remover registros duplicados do mesmo dia (manter o mais recente)
    @Query("DELETE FROM WeightHistory wh WHERE wh.user = :user AND wh.recordedDate = :date AND wh.id != :keepId")
    void deleteDuplicatesByUserAndDate(@Param("user") User user,
                                       @Param("date") LocalDate date,
                                       @Param("keepId") Long keepId);
}
