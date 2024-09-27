package com.beyond.easycheck.reservationroom.infrastructure.repository;

import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoomEntity, Long> {
}
