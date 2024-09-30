package com.beyond.easycheck.reservationservices.ui.requestbody;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ReservationServiceUpdateRequest {

    @NotNull
    @Min(value = 0, message = "price must be greater than or equal to 0")
    private Integer quantity;

    @NotNull
    @Min(value = 0, message = "price must be greater than or equal to 0")
    private Integer totalPrice;
}
