package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.tickets.infrastructure.entity.TicketPaymentEntity;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketOrderRepository;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketPaymentRepository;
import com.beyond.easycheck.tickets.ui.requestbody.TicketPaymentRequest;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketOrderEntity;
import com.beyond.easycheck.common.exception.EasyCheckException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.beyond.easycheck.tickets.exception.TicketOrderMessageType.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TicketPaymentService {

    private final TicketOrderRepository ticketOrderRepository;
    private final TicketPaymentRepository ticketpaymentRepository;

    @Transactional
    public TicketPaymentEntity processPayment(Long orderId, TicketPaymentRequest request) {

        TicketOrderEntity ticketOrder = ticketOrderRepository.findById(orderId)
                .orElseThrow(() -> new EasyCheckException(ORDER_NOT_FOUND));

        TicketPaymentEntity payment = TicketPaymentEntity.createPayment(ticketOrder, request.getPaymentMethod(), request.getAmount());

        return ticketpaymentRepository.save(payment);
    }
}