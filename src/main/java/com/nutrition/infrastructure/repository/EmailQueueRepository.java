package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.EmailQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailQueueRepository extends JpaRepository<EmailQueue, Long> {

    @Query("SELECT e FROM EmailQueue e WHERE e.status = 'PENDING' " +
           "AND (e.nextRetryAt IS NULL OR e.nextRetryAt <= :now) " +
           "AND e.retryCount < e.maxRetries " +
           "ORDER BY e.createdAt ASC")
    List<EmailQueue> findPendingEmails(LocalDateTime now);

    @Query("SELECT e FROM EmailQueue e WHERE e.status = 'FAILED' " +
           "ORDER BY e.updatedAt DESC")
    List<EmailQueue> findFailedEmails();

    @Query("SELECT COUNT(e) FROM EmailQueue e WHERE e.status = 'PENDING'")
    Long countPendingEmails();

    @Query("SELECT COUNT(e) FROM EmailQueue e WHERE e.status = 'FAILED'")
    Long countFailedEmails();

    List<EmailQueue> findByRecipientEmailAndEmailTypeAndStatus(
        String recipientEmail,
        EmailQueue.EmailType emailType,
        EmailQueue.Status status
    );
}
