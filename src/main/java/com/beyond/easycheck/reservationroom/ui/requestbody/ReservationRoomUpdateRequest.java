package com.beyond.easycheck.reservationroom.ui.requestbody;

import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class ReservationRoomUpdateRequest {

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;
}
