package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.config.ActivityLevelConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityLevelConfigRepository extends JpaRepository<ActivityLevelConfig, Long> {

    List<ActivityLevelConfig> findByActiveOrderByDisplayOrder(Boolean active);

    Optional<ActivityLevelConfig> findByCodeAndActive(String code, Boolean active);
}
