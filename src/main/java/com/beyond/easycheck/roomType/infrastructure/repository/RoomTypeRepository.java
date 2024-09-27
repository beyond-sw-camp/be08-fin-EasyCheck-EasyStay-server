package com.beyond.easycheck.roomType.infrastructure.repository;

import com.beyond.easycheck.roomType.infrastructure.entity.RoomTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomTypeRepository extends JpaRepository<RoomTypeEntity, Long> {
}
