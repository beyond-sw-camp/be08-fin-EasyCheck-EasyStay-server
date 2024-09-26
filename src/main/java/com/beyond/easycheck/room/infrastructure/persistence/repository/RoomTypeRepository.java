package com.beyond.easycheck.room.infrastructure.persistence.repository;

import com.beyond.easycheck.room.infrastructure.persistence.entity.RoomTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomTypeRepository extends JpaRepository<RoomTypeEntity, Long> {
}
