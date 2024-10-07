package com.beyond.easycheck.tickets.ui.view;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TicketOrderDTO {
    private final Long orderId;
    private final String ticketName;
    private final int quantity;
    private final BigDecimal totalPrice;
    private final Long userId;
    private final LocalDateTime purchaseTimestamp;

    public TicketOrderDTO(Long orderId, String ticketName, int quantity, BigDecimal totalPrice, Long userId, LocalDateTime purchaseTimestamp) {
        this.orderId = orderId;
        this.ticketName = ticketName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.purchaseTimestamp = purchaseTimestamp;
    }
}
