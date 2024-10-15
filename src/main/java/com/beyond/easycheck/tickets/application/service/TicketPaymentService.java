package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.tickets.infrastructure.entity.OrderStatus;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketOrderEntity;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketPaymentEntity;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketOrderRepository;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketPaymentRepository;
import com.beyond.easycheck.tickets.ui.requestbody.TicketPaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.beyond.easycheck.tickets.exception.TicketOrderMessageType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketPaymentService {

    private final TicketOrderRepository ticketOrderRepository;
    private final TicketPaymentRepository ticketPaymentRepository;

    @Transactional
    public TicketPaymentEntity processPayment(Long orderId, Long userId, TicketPaymentRequest request) {
        TicketOrderEntity order = getOrderById(orderId);
        validateUserAccess(order, userId);
        validateOrderStatusForPayment(order);

        TicketPaymentEntity payment = handlePayment(order, request);
        order.completeOrder();
        ticketOrderRepository.save(order);

        return payment;
    }

    @Transactional
    public TicketPaymentEntity cancelPayment(Long orderId, Long userId, String reason) {
        TicketOrderEntity order = getOrderById(orderId);
        validateUserAccess(order, userId);
        validateOrderStatusForCancellation(order);

        TicketPaymentEntity payment = ticketPaymentRepository.findByTicketOrderId(orderId)
                .orElseThrow(() -> new EasyCheckException(PAYMENT_NOT_FOUND));

        payment.cancelPayment(reason);
        order.cancelOrder();
        ticketOrderRepository.save(order);

        log.info("주문 ID: {} 결제 취소, 취소 사유: {}", order.getId(), reason);

        return ticketPaymentRepository.save(payment);
    }

    @Transactional
    public TicketPaymentEntity refundPayment(Long orderId, Long userId, String reason) {
        TicketOrderEntity order = getOrderById(orderId);
        validateUserAccess(order, userId);
        validateOrderStatusForRefund(order);

        TicketPaymentEntity payment = ticketPaymentRepository.findByTicketOrderId(orderId)
                .orElseThrow(() -> new EasyCheckException(PAYMENT_NOT_FOUND));

        payment.markAsRefunded(reason);
        order.markAsRefunded();
        ticketOrderRepository.save(order);

        log.info("주문 ID: {} 환불 완료, 환불 사유: {}", order.getId(), reason);

        return ticketPaymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public TicketPaymentEntity getPaymentStatus(Long orderId) {
        return ticketPaymentRepository.findByTicketOrderId(orderId)
                .orElseThrow(() -> new EasyCheckException(PAYMENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<TicketPaymentEntity> getPaymentHistory(Long userId) {
        return ticketPaymentRepository.findAllByTicketOrder_UserEntity_Id(userId);
    }

    @Transactional
    public TicketPaymentEntity retryPayment(Long orderId, Long userId, TicketPaymentRequest request) {
        TicketOrderEntity order = getOrderById(orderId);
        validateUserAccess(order, userId);
        validateOrderStatusForRetry(order);

        return handlePayment(order, request);
    }

    private TicketOrderEntity getOrderById(Long orderId) {
        return ticketOrderRepository.findById(orderId)
                .orElseThrow(() -> new EasyCheckException(ORDER_NOT_FOUND));
    }

    private void validateUserAccess(TicketOrderEntity order, Long userId) {
        if (!order.getUserEntity().getId().equals(userId)) {
            throw new EasyCheckException(UNAUTHORIZED_ACCESS);
        }
    }

    private void validateOrderStatusForPayment(TicketOrderEntity order) {
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new EasyCheckException(INVALID_ORDER_STATUS_FOR_PAYMENT);
        }
    }

    private void validateOrderStatusForCancellation(TicketOrderEntity order) {
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new EasyCheckException(ORDER_ALREADY_CANCELLED);
        }
        if (order.getOrderStatus() == OrderStatus.COMPLETED) {
            throw new EasyCheckException(ORDER_ALREADY_COMPLETED);
        }
    }

    private void validateOrderStatusForRefund(TicketOrderEntity order) {
        if (order.getOrderStatus() != OrderStatus.COMPLETED) {
            throw new EasyCheckException(INVALID_STATUS_FOR_REFUND);
        }
    }

    private void validateOrderStatusForRetry(TicketOrderEntity order) {
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new EasyCheckException(INVALID_ORDER_STATUS_FOR_RETRY);
        }
    }

    private TicketPaymentEntity handlePayment(TicketOrderEntity order, TicketPaymentRequest request) {
        TicketPaymentEntity payment = new TicketPaymentEntity(order, request.getPaymentAmount(), request.getPaymentMethod());
        try {
            payment.completePayment();
            ticketPaymentRepository.save(payment);

            log.info("주문 ID: {} 결제 성공, 결제 금액: {}", order.getId(), request.getPaymentAmount());
        } catch (Exception e) {
            payment.failPayment();
            ticketPaymentRepository.save(payment);
            log.error("주문 ID: {} 결제 실패, 사유: {}", order.getId(), e.getMessage());
            throw new EasyCheckException(PAYMENT_FAILED);
        }
        return payment;
    }
}