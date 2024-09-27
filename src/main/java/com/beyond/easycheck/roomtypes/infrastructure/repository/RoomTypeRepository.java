package com.beyond.easycheck.roomtypes.infrastructure.repository;

import com.beyond.easycheck.roomtypes.infrastructure.entity.RoomTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomTypeRepository extends JpaRepository<RoomTypeEntity, Long> {
}
