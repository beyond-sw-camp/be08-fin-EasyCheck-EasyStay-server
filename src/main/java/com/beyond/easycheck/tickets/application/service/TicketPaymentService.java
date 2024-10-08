package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.tickets.infrastructure.entity.OrderStatus;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketOrderEntity;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketPaymentEntity;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketOrderRepository;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketPaymentRepository;
import com.beyond.easycheck.tickets.ui.requestbody.TicketPaymentRequest;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.beyond.easycheck.tickets.exception.TicketOrderMessageType.*;
import static com.beyond.easycheck.user.exception.UserMessageType.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TicketPaymentService {

    private final TicketOrderRepository ticketOrderRepository;
    private final TicketPaymentRepository ticketPaymentRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional
    public TicketPaymentEntity processPayment(Long orderId, Long userId, TicketPaymentRequest request) {

        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new EasyCheckException(USER_NOT_FOUND));

        TicketOrderEntity order = ticketOrderRepository.findById(orderId)
                .orElseThrow(() -> new EasyCheckException(ORDER_NOT_FOUND));

        if (!order.getUserEntity().getId().equals(userId)) {
            throw new EasyCheckException(UNAUTHORIZED_ACCESS);
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new EasyCheckException(INVALID_ORDER_STATUS_FOR_PAYMENT);
        }

        try {
            TicketPaymentEntity payment = TicketPaymentEntity.createPayment(order, request.getPaymentAmount(), request.getPaymentMethod());

            order.confirmOrder();
            ticketOrderRepository.save(order);
            log.info("주문 ID: {} 결제 성공, 결제 금액: {}", order.getId(), request.getPaymentAmount());
            return ticketPaymentRepository.save(payment);

        } catch (Exception e) {
            order.failOrder();
            ticketOrderRepository.save(order);

            log.error("주문 ID: {} 결제 실패, 사유: {}", order.getId(), e.getMessage());
            throw new EasyCheckException(PAYMENT_FAILED);
        }
    }

    @Transactional
    public TicketPaymentEntity cancelPayment(Long orderId, Long userId, String reason) {
        Optional<UserEntity> user = userJpaRepository.findById(userId);
        if(user.isEmpty()){
            throw new EasyCheckException(USER_NOT_FOUND);
        }

        TicketOrderEntity order = ticketOrderRepository.findById(orderId)
                .orElseThrow(() -> new EasyCheckException(ORDER_NOT_FOUND));

        if (!order.getUserEntity().getId().equals(userId)) {
            throw new EasyCheckException(UNAUTHORIZED_ACCESS);
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new EasyCheckException(ORDER_ALREADY_CANCELLED);
        }

        if (order.getOrderStatus() == OrderStatus.COMPLETED) {
            throw new EasyCheckException(ORDER_ALREADY_COMPLETED);
        }

        order.cancelOrder();
        ticketOrderRepository.save(order);

        TicketPaymentEntity payment = ticketPaymentRepository.findByTicketOrderId(orderId)
                .orElseThrow(() -> new EasyCheckException(PAYMENT_NOT_FOUND));

        payment.cancelPayment(reason);
        log.info("주문 ID: {} 결제 취소, 취소 사유: {}", order.getId(), reason);

        return ticketPaymentRepository.save(payment);
    }
}
