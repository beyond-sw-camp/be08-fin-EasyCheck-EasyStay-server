package com.beyond.easycheck.room.infrastructure.persistence.repository;

import com.beyond.easycheck.room.infrastructure.persistence.entity.SeasonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeasonRepository extends JpaRepository<SeasonEntity, Long> {
}
