package com.beyond.easycheck.payments.ui.view;

import com.beyond.easycheck.payments.infrastructure.entity.CompletionStatus;
import com.beyond.easycheck.payments.infrastructure.entity.PaymentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PaymentView {

    private Long id;

    private Long reservationRoomId;

    private LocalDate checkinDate;

    private LocalDate checkoutDate;

    private String method;

    private Integer amount;

    private CompletionStatus completionStatus;

    public static PaymentView of(PaymentEntity paymentEntity) {

        return new PaymentView(

                paymentEntity.getId(),
                paymentEntity.getReservationRoomEntity().getId(),
                paymentEntity.getReservationRoomEntity().getCheckinDate(),
                paymentEntity.getReservationRoomEntity().getCheckoutDate(),
                paymentEntity.getMethod(),
                paymentEntity.getAmount(),
                paymentEntity.getCompletionStatus()
        );
    }
}
