package com.beyond.easycheck.payments.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.payments.application.validator.PaymentValidator;
import com.beyond.easycheck.payments.exception.PaymentMessageType;
import com.beyond.easycheck.payments.infrastructure.entity.PaymentEntity;
import com.beyond.easycheck.payments.infrastructure.repository.PaymentRepository;
import com.beyond.easycheck.payments.ui.requestbody.PaymentCreateRequest;
import com.beyond.easycheck.payments.ui.requestbody.PaymentUpdateRequest;
import com.beyond.easycheck.payments.ui.view.PaymentView;
import com.beyond.easycheck.reservationroom.exception.ReservationRoomMessageType;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationroom.infrastructure.repository.ReservationRoomRepository;
import com.beyond.easycheck.reservationroom.ui.requestbody.ReservationRoomUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRoomRepository reservationRoomRepository;

    @Transactional
    public void processReservationPayment(Long reservationId, PaymentCreateRequest paymentCreateRequest) {

        ReservationRoomEntity reservationRoomEntity = reservationRoomRepository.findById(reservationId)
                .orElseThrow(() -> new EasyCheckException(ReservationRoomMessageType.RESERVATION_NOT_FOUND));

        PaymentEntity paymentEntity = createPayment(paymentCreateRequest);

        PaymentValidator.validatePayment(paymentEntity, reservationRoomEntity);

        reservationRoomEntity.updateReservationRoomAndProcessPayment(new ReservationRoomUpdateRequest(), paymentEntity);

        reservationRoomRepository.save(reservationRoomEntity);
    }

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

    @Transactional(readOnly = true)
    public List<PaymentView> getAllPayments(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentEntity> paymentEntityPage = paymentRepository.findAll(pageable);

        return paymentEntityPage.getContent().stream()
                .map(PaymentView::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentView getPaymentById(Long id) {

        PaymentEntity paymentEntity = paymentRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(PaymentMessageType.PAYMENT_NOT_FOUND)
        );

        return PaymentView.of(paymentEntity);
    }

    @Transactional
    public void cancelPayment(Long id, PaymentUpdateRequest paymentUpdateRequest) {

        PaymentEntity paymentEntity = paymentRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(PaymentMessageType.PAYMENT_NOT_FOUND)
        );

        paymentEntity.updatePayment(paymentUpdateRequest);

        paymentRepository.save(paymentEntity);
    }
}
