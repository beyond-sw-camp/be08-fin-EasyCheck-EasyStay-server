package com.beyond.easycheck.rooms.infrastructure.repository;

import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<RoomEntity.ImageEntity, Long> {
}
