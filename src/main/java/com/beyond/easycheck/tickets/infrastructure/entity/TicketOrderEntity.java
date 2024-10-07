package com.beyond.easycheck.tickets.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ticket_order")
public class TicketOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @Column(nullable = false)
    private int quantity;

    private Long userId;

    private Long guestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceiptMethodType receiptMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollectionAgreementType collectionAgreement;

    private BigDecimal totalPrice;

    @Column(nullable = false)
    private LocalDateTime purchaseTimestamp;

    public TicketOrderEntity(TicketEntity ticket, int quantity, Long userId, Long guestId, ReceiptMethodType receiptMethod, CollectionAgreementType collectionAgreement) {
        this.ticket = ticket;
        this.quantity = quantity;
        this.userId = userId;
        this.guestId = guestId;
        this.receiptMethod = receiptMethod;
        this.collectionAgreement = collectionAgreement;
        this.totalPrice = ticket.getPrice().multiply(BigDecimal.valueOf(quantity));
        this.purchaseTimestamp = LocalDateTime.now();
    }
}
