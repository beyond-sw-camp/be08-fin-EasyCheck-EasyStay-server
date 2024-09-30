package com.beyond.easycheck.tickets.ui.requestbody;

import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TicketRequest {
    private String ticketName;
    private BigDecimal price;
    private LocalDateTime saleStartDate;
    private LocalDateTime saleEndDate;
    private LocalDateTime validFromDate;
    private LocalDateTime validToDate;
}