package com.beyond.easycheck.reservationroom.infrastructure.repository;

import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationStatus;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoomEntity, Long> {

    int countByRoomEntityAndReservationStatus(RoomEntity roomEntity, ReservationStatus reservationStatus);
}
