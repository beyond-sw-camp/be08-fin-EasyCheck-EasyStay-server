package com.beyond.easycheck.reservationrooms.infrastructure.repository;

import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoomEntity, Long> {

}
