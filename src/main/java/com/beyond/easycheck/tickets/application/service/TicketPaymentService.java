package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.tickets.infrastructure.entity.OrderStatus;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketOrderEntity;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketPaymentEntity;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketOrderRepository;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketPaymentRepository;
import com.beyond.easycheck.tickets.ui.requestbody.TicketPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.beyond.easycheck.tickets.exception.TicketOrderMessageType.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketPaymentService {

    private final TicketOrderRepository ticketOrderRepository;
    private final TicketPaymentRepository ticketPaymentRepository;

    @Transactional
    public TicketPaymentEntity processPayment(Long orderId, TicketPaymentRequest request) {
        TicketOrderEntity order = ticketOrderRepository.findById(orderId)
                .orElseThrow(() -> new EasyCheckException(ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new EasyCheckException(INVALID_ORDER_STATUS_FOR_PAYMENT);
        }


        TicketPaymentEntity payment = TicketPaymentEntity.createPayment(order, request.getPaymentAmount(), request.getPaymentMethod());
        order.confirmOrder();
        ticketOrderRepository.save(order);

        return ticketPaymentRepository.save(payment);
    }

    @Transactional
    public TicketPaymentEntity cancelPayment(Long orderId, String reason) {
        TicketOrderEntity order = ticketOrderRepository.findById(orderId)
                .orElseThrow(() -> new EasyCheckException(ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new EasyCheckException(ORDER_ALREADY_CANCELLED);
        }

        order.cancelOrder();
        ticketOrderRepository.save(order);

        TicketPaymentEntity payment = ticketPaymentRepository.findByTicketOrderId(orderId)
                .orElseThrow(() -> new EasyCheckException(PAYMENT_NOT_FOUND));

        payment.cancelPayment(reason);
        return ticketPaymentRepository.save(payment);
    }
}
