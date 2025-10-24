package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.food.UserSupplementSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSupplementScheduleRepository extends JpaRepository<UserSupplementSchedule, Long> {

    /**
     * Find all schedules for a specific user supplement
     */
    List<UserSupplementSchedule> findByUserSupplementId(Long userSupplementId);

    /**
     * Find a specific schedule by ID and user supplement ID
     */
    Optional<UserSupplementSchedule> findByIdAndUserSupplementId(Long id, Long userSupplementId);

    /**
     * Delete all schedules for a specific user supplement
     */
    void deleteByUserSupplementId(Long userSupplementId);
}
