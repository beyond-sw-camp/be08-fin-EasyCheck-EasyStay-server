package com.beyond.easycheck.reservationservices.ui.requestbody;

import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
public class ReservationServiceCreateRequest {

    @NotNull
    private Long reservationRoomId;

    @NotNull
    private Long additionalServiceId;

    @NotNull
    @Min(value = 0, message = "price must be greater than or equal to 0")
    private Integer quantity;

    @NotNull
    @Min(value = 0, message = "price must be greater than or equal to 0")
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    private ReservationServiceStatus reservationServiceStatus;
}
