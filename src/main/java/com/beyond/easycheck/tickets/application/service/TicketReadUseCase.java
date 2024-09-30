package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.tickets.infrastructure.entity.TicketEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TicketReadUseCase {
    List<FindTicketResult> getTicketsByThemePark(Long themeParkId);

    List<FindTicketResult> getTicketsByThemeParkOnSale(Long themeParkId);

    FindTicketResult getTicketById(Long ticketId);

    @Getter
    @Builder
    @EqualsAndHashCode
    class FindTicketResult {
        private final Long id;
        private final String ticketName;
        private final BigDecimal price;
        private final LocalDateTime saleStartDate;
        private final LocalDateTime saleEndDate;
        private final LocalDateTime validFromDate;
        private final LocalDateTime validToDate;

        public static FindTicketResult fromEntity(TicketEntity ticket) {
            return FindTicketResult.builder()
                    .id(ticket.getId())
                    .ticketName(ticket.getTicketName())
                    .price(ticket.getPrice())
                    .saleStartDate(ticket.getSaleStartDate())
                    .saleEndDate(ticket.getSaleEndDate())
                    .validFromDate(ticket.getValidFromDate())
                    .validToDate(ticket.getValidToDate())
                    .build();
        }
    }
}
