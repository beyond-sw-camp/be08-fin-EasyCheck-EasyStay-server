package com.beyond.easycheck.tickets.ui.requestbody;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class TicketPaymentRequest {
    private String paymentMethod;
    private BigDecimal paymentAmount;
}