package com.beyond.easycheck.payments.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.mail.application.service.MailService;
import com.beyond.easycheck.payments.exception.PaymentMessageType;
import com.beyond.easycheck.payments.infrastructure.entity.PaymentEntity;
import com.beyond.easycheck.payments.infrastructure.repository.PaymentRepository;
import com.beyond.easycheck.payments.ui.requestbody.PaymentCreateRequest;
import com.beyond.easycheck.payments.ui.requestbody.PaymentUpdateRequest;
import com.beyond.easycheck.payments.ui.view.PaymentView;
import com.beyond.easycheck.reservationrooms.exception.ReservationRoomMessageType;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.PaymentStatus;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationrooms.infrastructure.repository.ReservationRoomRepository;
import com.beyond.easycheck.reservationrooms.ui.view.ReservationRoomView;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRoomRepository reservationRoomRepository;

    private final MailService mailService;

    private IamportClient iamportClient;

    @Value("${portone.api-key}")
    private String apiKey;

    @Value("${portone.api-secret}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    @Transactional
    public void processReservationPayment(Long reservationId, PaymentCreateRequest paymentCreateRequest) {

        ReservationRoomEntity reservationRoomEntity = reservationRoomRepository.findById(reservationId)
                .orElseThrow(() -> new EasyCheckException(ReservationRoomMessageType.RESERVATION_NOT_FOUND));

        IamportResponse<Payment> paymentResponse = validatePortOnePayment(paymentCreateRequest.getImpUid());

        if (paymentResponse != null && paymentResponse.getResponse().getAmount().intValue() == paymentCreateRequest.getAmount()) {

            PaymentEntity paymentEntity = createPayment(paymentCreateRequest, reservationRoomEntity);
            paymentRepository.save(paymentEntity);

            reservationRoomEntity.updatePaymentStatus(PaymentStatus.PAID);
            reservationRoomRepository.save(reservationRoomEntity);

            ReservationRoomView reservationRoomView = ReservationRoomView.of(reservationRoomEntity);
            mailService.sendReservationConfirmationEmail(reservationRoomEntity.getUserEntity().getEmail(), reservationRoomView);

        } else {
            throw new EasyCheckException(PaymentMessageType.PAYMENT_VERIFICATION_FAILED);
        }
    }

    public IamportResponse<Payment> validatePortOnePayment(String impUid) {

        try {
            IamportResponse<Payment> paymentResponse = iamportClient.paymentByImpUid(impUid);

            if (paymentResponse == null || paymentResponse.getResponse() == null) {
                throw new EasyCheckException(PaymentMessageType.PORTONE_VERIFICATION_ERROR);
            }

            return paymentResponse;
        } catch (IamportResponseException | IOException e) {
            log.error("PortOne 결제 검증 오류: impUid={}, message={}", impUid, e.getMessage());
            throw new EasyCheckException(PaymentMessageType.PORTONE_VERIFICATION_ERROR);
        }
    }

    @Transactional
    public PaymentEntity createPayment(PaymentCreateRequest paymentCreateRequest, ReservationRoomEntity reservationRoomEntity) {

        return PaymentEntity.builder()
                .reservationRoomEntity(reservationRoomEntity)
                .method(paymentCreateRequest.getMethod())
                .amount(paymentCreateRequest.getAmount())
                .paymentDate(paymentCreateRequest.getPaymentDate())
                .completionStatus(paymentCreateRequest.getCompletionStatus())
                .build();
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
