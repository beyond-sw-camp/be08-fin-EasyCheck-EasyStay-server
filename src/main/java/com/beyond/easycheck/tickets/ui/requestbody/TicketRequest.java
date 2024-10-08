package com.beyond.easycheck.tickets.ui.requestbody;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TicketRequest {

    @NotNull(message = "티켓명은 필수입니다.")
    private final String ticketName;

    @NotNull(message = "티켓가격은 필수입니다.")
    private final BigDecimal price;

    private final LocalDateTime saleStartDate;
    private final LocalDateTime saleEndDate;
    private final LocalDateTime validFromDate;
    private final LocalDateTime validToDate;

    // 생성자 추가
    public TicketRequest(String ticketName, BigDecimal price, LocalDateTime saleStartDate, LocalDateTime saleEndDate, LocalDateTime validFromDate, LocalDateTime validToDate) {
        this.ticketName = ticketName;
        this.price = price;
        this.saleStartDate = saleStartDate;
        this.saleEndDate = saleEndDate;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
    }
}
