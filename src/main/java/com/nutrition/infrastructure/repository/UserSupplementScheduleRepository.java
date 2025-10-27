package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.food.UserSupplementSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSupplementScheduleRepository extends JpaRepository<UserSupplementSchedule, Long> {

    List<UserSupplementSchedule> findByUserSupplementId(Long userSupplementId);

    Optional<UserSupplementSchedule> findByIdAndUserSupplementId(Long id, Long userSupplementId);

}
