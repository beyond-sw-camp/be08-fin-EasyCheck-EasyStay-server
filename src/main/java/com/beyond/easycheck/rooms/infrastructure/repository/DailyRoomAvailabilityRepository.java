package com.beyond.easycheck.rooms.infrastructure.repository;

import com.beyond.easycheck.rooms.infrastructure.entity.DailyRoomAvailabilityEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyRoomAvailabilityRepository extends JpaRepository<DailyRoomAvailabilityEntity, Integer> {

    @EntityGraph(attributePaths = {"roomEntity", "roomEntity.roomTypeEntity"})
    Optional<DailyRoomAvailabilityEntity> findByRoomEntityAndDate(RoomEntity roomEntity, LocalDateTime date);

    @Query("SELECT d FROM DailyRoomAvailabilityEntity d JOIN FETCH d.roomEntity r JOIN FETCH r.roomTypeEntity WHERE d.date BETWEEN :startDate AND :endDate")
    List<DailyRoomAvailabilityEntity> findAvailabilityByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
