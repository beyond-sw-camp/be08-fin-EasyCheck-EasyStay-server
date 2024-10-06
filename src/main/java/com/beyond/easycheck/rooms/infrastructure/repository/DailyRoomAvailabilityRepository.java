package com.beyond.easycheck.rooms.infrastructure.repository;

import com.beyond.easycheck.rooms.infrastructure.entity.DailyRoomAvailabilityEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DailyRoomAvailabilityRepository extends JpaRepository<DailyRoomAvailabilityEntity, Integer> {

    Optional<DailyRoomAvailabilityEntity> findByRoomEntityAndDate(RoomEntity roomEntity, LocalDateTime date);
}
