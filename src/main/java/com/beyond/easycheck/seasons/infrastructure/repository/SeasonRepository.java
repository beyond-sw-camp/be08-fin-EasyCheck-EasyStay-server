package com.beyond.easycheck.seasons.infrastructure.repository;

import com.beyond.easycheck.seasons.infrastructure.entity.SeasonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeasonRepository extends JpaRepository<SeasonEntity, Long> {
}
