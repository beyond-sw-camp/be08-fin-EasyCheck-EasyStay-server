package com.beyond.easycheck.payments.application.validator;

import com.beyond.easycheck.payments.infrastructure.entity.CompletionStatus;
import com.beyond.easycheck.payments.infrastructure.entity.PaymentEntity;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.PaymentStatus;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationStatus;

public class PaymentValidator {

    public static void validatePayment(PaymentEntity paymentEntity, ReservationRoomEntity reservationRoomEntity) {

        if (!paymentEntity.getAmount().equals(reservationRoomEntity.getTotalPrice())) {
            throw new IllegalArgumentException("결제 금액이 예약 금액과 일치하지 않습니다.");
        }

        if (paymentEntity.getCompletionStatus() != CompletionStatus.COMPLETE) {
            throw new IllegalArgumentException("결제가 완료되지 않았습니다.");
        }

        if (reservationRoomEntity.getReservationStatus() != ReservationStatus.RESERVATION) {
            throw new IllegalArgumentException("현재 예약 상태에서는 결제가 불가능합니다.");
        }

        if (reservationRoomEntity.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalArgumentException("이미 결제가 완료된 예약입니다.");
        }
    }
}
