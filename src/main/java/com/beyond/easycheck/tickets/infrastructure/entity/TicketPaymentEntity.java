package com.beyond.easycheck.tickets.infrastructure.entity;

import com.beyond.easycheck.common.exception.EasyCheckException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.beyond.easycheck.tickets.exception.TicketOrderMessageType.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ticket_payment")
public class TicketPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_order_id", nullable = false)
    private TicketOrderEntity ticketOrder;

    @Column(name = "amount", nullable = false)
    private BigDecimal paymentAmount;

    @Column(nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    private String cancelReason;

    private LocalDateTime cancelDate;

    private LocalDateTime paymentDate;

    public TicketPaymentEntity(TicketOrderEntity order, BigDecimal amount, String method) {
        if (order == null) {
            throw new EasyCheckException(TICKET_ORDER_CANNOT_BE_NULL);
        }
        this.ticketOrder = order;
        this.paymentAmount = amount;
        this.paymentMethod = method;
        this.paymentStatus = PaymentStatus.PENDING;
        this.paymentDate = LocalDateTime.now();
    }

    public void completePayment() {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new EasyCheckException(INVALID_PAYMENT_STATUS_FOR_COMPLETION);
        }
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public void failPayment() {
        if (this.paymentStatus == PaymentStatus.COMPLETED || this.paymentStatus == PaymentStatus.CANCELLED) {
            throw new EasyCheckException(INVALID_PAYMENT_STATUS_FOR_FAILURE);
        }
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void cancelPayment(String reason) {
        if (this.paymentStatus != PaymentStatus.PENDING && this.paymentStatus != PaymentStatus.COMPLETED) {
            throw new EasyCheckException(INVALID_PAYMENT_STATUS_FOR_CANCELLATION);
        }
        this.paymentStatus = PaymentStatus.CANCELLED;
        this.cancelReason = reason;
        this.cancelDate = LocalDateTime.now();
    }

    public void markAsRefunded(String reason) {
        if (this.paymentStatus != PaymentStatus.COMPLETED) {
            throw new EasyCheckException(INVALID_STATUS_FOR_REFUND);
        }
        this.paymentStatus = PaymentStatus.REFUNDED;
        this.cancelReason = reason;
        this.cancelDate = LocalDateTime.now();
    }

}
