package com.beyond.easycheck.payments.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.payments.infrastructure.entity.PaymentEntity;
import com.beyond.easycheck.payments.infrastructure.repository.PaymentRepository;
import com.beyond.easycheck.payments.ui.requestbody.PaymentCreateRequest;
import com.beyond.easycheck.reservationroom.exception.ReservationRoomMessageType;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationroom.infrastructure.repository.ReservationRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRoomRepository reservationRoomRepository;

    @Transactional
    public PaymentEntity createPayment(PaymentCreateRequest paymentCreateRequest) {

        ReservationRoomEntity reservationRoomEntity = reservationRoomRepository.findById(paymentCreateRequest.getReservationId()).orElseThrow(
                () -> new EasyCheckException(ReservationRoomMessageType.RESERVATION_NOT_FOUND)
        );

        PaymentEntity paymentEntity = PaymentEntity.builder()
                .reservationRoomEntity(reservationRoomEntity)
                .method(paymentCreateRequest.getMethod())
                .amount(paymentCreateRequest.getAmount())
                .paymentDate(paymentCreateRequest.getPaymentDate())
                .completionStatus(paymentCreateRequest.getCompletionStatus())
                .build();

        return paymentRepository.save(paymentEntity);
    }
}
