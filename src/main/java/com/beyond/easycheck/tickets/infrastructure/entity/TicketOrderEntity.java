package com.beyond.easycheck.tickets.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_order_ticket")
public class TicketOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketEntity ticketType;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "guest_id")
    private Long guestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceiptMethodType receiptMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollectionAggrementType collectionAgreement;

    @Column(nullable = false)
    private String paymentStatus;

    public static TicketOrderEntity createTicketOrder(TicketEntity ticketType, int quantity, Long userId, Long guestId,
                                                      ReceiptMethodType receiptMethod, CollectionAggrementType collectionAgreement) {
        TicketOrderEntity ticketOrder = new TicketOrderEntity();
        ticketOrder.ticketType = ticketType;
        ticketOrder.quantity = quantity;
        ticketOrder.userId = userId;
        ticketOrder.guestId = guestId;
        ticketOrder.receiptMethod = receiptMethod;
        ticketOrder.collectionAgreement = collectionAgreement;
        return ticketOrder;
    }

}