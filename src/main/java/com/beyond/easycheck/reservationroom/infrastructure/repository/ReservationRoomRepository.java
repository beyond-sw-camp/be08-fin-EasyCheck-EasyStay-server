package com.beyond.easycheck.reservationroom.infrastructure.repository;

import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoomEntity, Long> {

    boolean existsByRoomEntityAndCheckinDateLessThanEqualAndCheckoutDateGreaterThanEqual(
            RoomEntity roomEntity, LocalDateTime checkoutDate, LocalDateTime checkinDate);
}
