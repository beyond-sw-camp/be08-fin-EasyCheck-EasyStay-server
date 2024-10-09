package com.beyond.easycheck.reservationroom.infrastructure.repository;

import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoomEntity, Long> {

    @Query("SELECT r.roomEntity FROM ReservationRoomEntity r " +
            "WHERE r.checkinDate < :checkoutDate AND r.checkoutDate > :checkinDate")
    List<RoomEntity> findReservedRoomsBetweenDates(@Param("checkinDate") LocalDateTime checkinDate,
                                                   @Param("checkoutDate") LocalDateTime checkoutDate);
}
