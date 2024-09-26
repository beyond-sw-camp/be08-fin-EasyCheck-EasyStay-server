package com.beyond.easycheck.room.infrastructure.persistence.repository;

import com.beyond.easycheck.room.infrastructure.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
}
