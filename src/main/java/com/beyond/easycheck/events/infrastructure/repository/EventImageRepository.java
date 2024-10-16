package com.beyond.easycheck.events.infrastructure.repository;

import com.beyond.easycheck.events.infrastructure.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventImageRepository extends JpaRepository<EventEntity.ImageEntity, Long> {
}
