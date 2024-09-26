package com.beyond.easycheck.room.infrastructure.persistence.repository;

import com.beyond.easycheck.room.infrastructure.persistence.entity.RoomRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRateRepository extends JpaRepository<RoomRateEntity, Long> {
}
