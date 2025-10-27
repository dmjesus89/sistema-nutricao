package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.config.GoalConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalConfigRepository extends JpaRepository<GoalConfig, Long> {

    List<GoalConfig> findByActiveOrderByDisplayOrder(Boolean active);

    Optional<GoalConfig> findByCodeAndActive(String code, Boolean active);
}
