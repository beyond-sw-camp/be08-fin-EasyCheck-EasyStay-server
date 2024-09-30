package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketOrderEntity;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketOrderRepository;
import com.beyond.easycheck.tickets.ui.requestbody.TicketOrderRequest;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketEntity;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.beyond.easycheck.tickets.exception.TicketMessageType.TICKET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TicketOrderService {

    private final TicketOrderRepository ticketOrderRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public TicketOrderEntity createTicketOrder(TicketOrderRequest request) {

        TicketEntity ticketType = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new EasyCheckException(TICKET_NOT_FOUND));

        TicketOrderEntity ticketOrder = TicketOrderEntity.createTicketOrder(
                ticketType,
                request.getQuantity(),
                request.getUserId(),
                request.getGuestId(),
                request.getReceiptMethod(),
                request.getCollectionAgreement()
        );

        return ticketOrderRepository.save(ticketOrder);
    }
}