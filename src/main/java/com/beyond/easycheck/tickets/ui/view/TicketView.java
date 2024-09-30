package com.beyond.easycheck.tickets.ui.view;

import com.beyond.easycheck.tickets.infrastructure.entity.TicketEntity;
import com.beyond.easycheck.tickets.application.service.TicketReadUseCase.FindTicketResult;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TicketView {
    private final Long id;
    private final String ticketName;
    private final BigDecimal price;
    private final LocalDateTime saleStartDate;
    private final LocalDateTime saleEndDate;
    private final LocalDateTime validFromDate;
    private final LocalDateTime validToDate;

    public TicketView(TicketEntity entity) {
        this.id = entity.getId();
        this.ticketName = entity.getTicketName();
        this.price = entity.getPrice();
        this.saleStartDate = entity.getSaleStartDate();
        this.saleEndDate = entity.getSaleEndDate();
        this.validFromDate = entity.getValidFromDate();
        this.validToDate = entity.getValidToDate();
    }

    public TicketView(FindTicketResult result) {
        this.id = result.getId();
        this.ticketName = result.getTicketName();
        this.price = result.getPrice();
        this.saleStartDate = result.getSaleStartDate();
        this.saleEndDate = result.getSaleEndDate();
        this.validFromDate = result.getValidFromDate();
        this.validToDate = result.getValidToDate();
    }
}
