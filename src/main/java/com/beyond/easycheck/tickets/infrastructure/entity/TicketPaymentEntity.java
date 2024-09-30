package com.beyond.easycheck.tickets.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ticket_payment")
public class TicketPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_order_id", nullable = false)
    private TicketOrderEntity ticketOrder;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    public static TicketPaymentEntity createPayment(TicketOrderEntity ticketOrder, String paymentMethod, BigDecimal amount) {
        TicketPaymentEntity payment = new TicketPaymentEntity();
        payment.ticketOrder = ticketOrder;
        payment.paymentMethod = paymentMethod;
        payment.amount = amount;
        return payment;
    }
}