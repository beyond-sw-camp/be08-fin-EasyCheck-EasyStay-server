package com.beyond.easycheck.reservationservices.ui.requestbody;

import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ReservationServiceUpdateRequest {

    @NotNull
    private ReservationServiceStatus reservationServiceStatus;
}
