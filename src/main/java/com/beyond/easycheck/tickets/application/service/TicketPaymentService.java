package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketOrderEntity;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketPaymentEntity;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketOrderRepository;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketPaymentRepository;
import com.beyond.easycheck.tickets.ui.requestbody.TicketPaymentRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.beyond.easycheck.payments.exception.PaymentMessageType.PAYMENT_NOT_FOUND;
import static com.beyond.easycheck.tickets.exception.TicketOrderMessageType.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TicketPaymentService {

    private final TicketOrderRepository ticketorderRepository;
    private final TicketPaymentRepository ticketpaymentRepository;

    @Transactional
    public TicketPaymentEntity processPayment(Long orderId, TicketPaymentRequest request) {
        TicketOrderEntity order = ticketorderRepository.findById(orderId)
                .orElseThrow(() -> new EasyCheckException(ORDER_NOT_FOUND));

        TicketPaymentEntity payment = TicketPaymentEntity.createPayment(
                order,
                request.getPaymentAmount(),
                request.getPaymentMethod()
        );

        return ticketpaymentRepository.save(payment);
    }

    @Transactional
    public void cancelPayment(Long orderId, String reason) {
        TicketPaymentEntity payment = ticketpaymentRepository.findByTicketOrderId(orderId)
                .orElseThrow(() -> new EasyCheckException(PAYMENT_NOT_FOUND));

        payment.cancelPayment(reason);
        ticketpaymentRepository.save(payment);
    }
}
