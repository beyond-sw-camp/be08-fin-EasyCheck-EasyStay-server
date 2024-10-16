package com.beyond.easycheck.reservationrooms.ui.requestbody;

import com.beyond.easycheck.reservationrooms.infrastructure.entity.PaymentStatus;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor(access = lombok.AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
public class ReservationRoomCreateRequest {

    @NotNull
    private Long roomId;

    @NotNull
    private LocalDateTime reservationDate;

    @NotNull(message = "체크인 날짜를 지정해야 합니다.")
    private LocalDate checkinDate;

    @NotNull(message = "체크아웃 날짜를 지정해야 합니다.")
    private LocalDate checkoutDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @NotNull
    @Min(value = 0, message = "price must be greater than or equal to 0")
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public void setRoom(long id, LocalDateTime date, LocalDate checkinDate, LocalDate checkoutDate, ReservationStatus reservationStatus, Integer totalPrice, PaymentStatus paymentStatus) {

        this.roomId = id;
        this.reservationDate = date;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.reservationStatus = reservationStatus;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
    }
}
