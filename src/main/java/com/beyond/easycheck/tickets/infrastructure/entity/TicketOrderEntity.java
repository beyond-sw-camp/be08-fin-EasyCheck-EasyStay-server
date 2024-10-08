package com.beyond.easycheck.tickets.infrastructure.entity;

import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    private Long guestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus OrderStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceiptMethodType receiptMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollectionAgreementType collectionAgreement;

    private BigDecimal totalPrice;

    @Column(nullable = false)
    private LocalDateTime purchaseTimestamp;

    public TicketOrderEntity(TicketEntity ticket, int quantity, UserEntity userEntity, Long guestId, ReceiptMethodType receiptMethod, CollectionAgreementType collectionAgreement) {
        this.ticket = ticket;
        this.quantity = quantity;
        this.userEntity = userEntity;
        this.guestId = guestId;
        this.receiptMethod = receiptMethod;
        this.collectionAgreement = collectionAgreement;
        this.totalPrice = ticket.getPrice().multiply(BigDecimal.valueOf(quantity));
        this.purchaseTimestamp = LocalDateTime.now();
        this.OrderStatus = OrderStatus.PENDING;
    }

    public void cancelOrder() {
        this.OrderStatus = OrderStatus.CANCELLED;
    }

    public void confirmOrder() { this.OrderStatus = OrderStatus.CONFIRMED; }

    public void completeOrder() { this.OrderStatus = OrderStatus.COMPLETED; }

    public void failOrder() { this.OrderStatus = OrderStatus.FAILED; }
}
