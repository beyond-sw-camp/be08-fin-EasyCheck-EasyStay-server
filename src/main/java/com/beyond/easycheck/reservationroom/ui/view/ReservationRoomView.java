package com.beyond.easycheck.reservationroom.ui.view;

import com.beyond.easycheck.reservationroom.infrastructure.entity.PaymentStatus;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationRoomView {

    private Long id;

    private LocalDateTime reservationDate;

    private LocalDate checkinDate;

    private LocalDate checkoutDate;

    private ReservationStatus reservationStatus;

    private Integer totalPrice;

    private PaymentStatus paymentStatus;

    public static ReservationRoomView of(ReservationRoomEntity reservationRoomEntity) {

        return new ReservationRoomView(

                reservationRoomEntity.getId(),
                reservationRoomEntity.getReservationDate(),
                reservationRoomEntity.getCheckinDate(),
                reservationRoomEntity.getCheckoutDate(),
                reservationRoomEntity.getReservationStatus(),
                reservationRoomEntity.getTotalPrice(),
                reservationRoomEntity.getPaymentStatus()
        );
    }
}
