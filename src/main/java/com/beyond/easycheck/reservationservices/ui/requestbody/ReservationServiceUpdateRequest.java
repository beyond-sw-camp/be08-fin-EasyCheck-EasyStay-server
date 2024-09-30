package com.beyond.easycheck.reservationservices.ui.requestbody;

import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ReservationServiceUpdateRequest {

    @Enumerated(EnumType.STRING)
    private ReservationServiceStatus reservationServiceStatus;
}
