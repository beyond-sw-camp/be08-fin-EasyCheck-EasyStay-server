package com.beyond.easycheck.tickets.ui.view;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TicketOrderDTO {
    private Long orderId;
    private String ticketName;
    private int quantity;
    private Long userId;
    private LocalDateTime purchaseTimestamp;

    public TicketOrderDTO(Long orderId, String ticketName, int quantity, Long userId, LocalDateTime purchaseTimestamp) {
        this.orderId = orderId;
        this.ticketName = ticketName;
        this.quantity = quantity;
        this.userId = userId;
        this.purchaseTimestamp = purchaseTimestamp;
    }
}
