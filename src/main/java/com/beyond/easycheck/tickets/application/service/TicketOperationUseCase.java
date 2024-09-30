package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.tickets.infrastructure.entity.TicketEntity;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TicketOperationUseCase {

    TicketEntity createTicket(TicketCreateCommand command);

    TicketEntity updateTicket(Long ticketId, TicketUpdateCommand command);

    void deleteTicket(Long ticketId);

    @Getter
    @Builder
    class TicketCreateCommand {
        private Long themeParkId;
        private String ticketName;
        private BigDecimal price;
        private LocalDateTime saleStartDate;
        private LocalDateTime saleEndDate;
        private LocalDateTime validFromDate;
        private LocalDateTime validToDate;
    }

    @Getter
    @Builder
    class TicketUpdateCommand {
        private String ticketName;
        private BigDecimal price;
        private LocalDateTime saleStartDate;
        private LocalDateTime saleEndDate;
        private LocalDateTime validFromDate;
        private LocalDateTime validToDate;
    }
}
