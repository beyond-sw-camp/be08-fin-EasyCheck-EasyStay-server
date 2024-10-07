package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketOrderEntity;
import com.beyond.easycheck.tickets.infrastructure.entity.OrderStatus;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketOrderRepository;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketEntity;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketRepository;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkRepository;
import com.beyond.easycheck.tickets.ui.requestbody.TicketOrderRequest;
import com.beyond.easycheck.tickets.ui.view.TicketOrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.beyond.easycheck.themeparks.exception.ThemeParkMessageType.THEME_PARK_NOT_FOUND;
import static com.beyond.easycheck.tickets.exception.TicketMessageType.*;
import static com.beyond.easycheck.tickets.exception.TicketOrderMessageType.ORDER_ALREADY_CANCELLED;
import static com.beyond.easycheck.tickets.exception.TicketOrderMessageType.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketOrderService implements TicketOrderOperationUseCase, TicketOrderReadUseCase {

    private final TicketOrderRepository ticketOrderRepository;
    private final TicketRepository ticketRepository;
    private final ThemeParkRepository themeParkRepository;

    @Override
    @Transactional
    public TicketOrderDTO createTicketOrder(Long themeParkId, TicketOrderRequest request) {

        if (!themeParkRepository.existsById(themeParkId)) {
            throw new EasyCheckException(THEME_PARK_NOT_FOUND);
        }

        TicketEntity ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new EasyCheckException(TICKET_NOT_FOUND));

        if (!ticket.getThemePark().getId().equals(themeParkId)) {
            throw new EasyCheckException(TICKET_NOT_BELONG_TO_THEME_PARK);
        }

        if (request.getUserId() == null && request.getGuestId() == null) {
            throw new EasyCheckException(INVALID_USER_OR_GUEST);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(ticket.getSaleStartDate()) || now.isAfter(ticket.getSaleEndDate())) {
            throw new EasyCheckException(TICKET_SALE_PERIOD_INVALID);
        }

        if (request.getQuantity() <= 0) {
            throw new EasyCheckException(INVALID_QUANTITY);
        }

        TicketOrderEntity ticketOrder = new TicketOrderEntity(
                ticket,
                request.getQuantity(),
                request.getUserId(),
                request.getGuestId(),
                request.getReceiptMethod(),
                request.getCollectionAgreement()
        );

        ticketOrderRepository.save(ticketOrder);

        return new TicketOrderDTO(
                ticketOrder.getId(),
                ticketOrder.getTicket().getTicketName(),
                ticketOrder.getQuantity(),
                ticketOrder.getTotalPrice(),
                ticketOrder.getUserId(),
                ticketOrder.getPurchaseTimestamp()
        );
    }

    @Override
    @Transactional
    public void cancelTicketOrder(Long orderId) {
        TicketOrderEntity ticketOrder = ticketOrderRepository.findById(orderId)
                .orElseThrow(() -> new EasyCheckException(ORDER_NOT_FOUND));

        if (ticketOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new EasyCheckException(ORDER_ALREADY_CANCELLED);
        }

        ticketOrder.cancelOrder();
        ticketOrderRepository.save(ticketOrder);
    }

    @Override
    public TicketOrderDTO getTicketOrder(Long orderId) {
        TicketOrderEntity ticketOrder = ticketOrderRepository.findById(orderId)
                .orElseThrow(() -> new EasyCheckException(ORDER_NOT_FOUND));

        return new TicketOrderDTO(
                ticketOrder.getId(),
                ticketOrder.getTicket().getTicketName(),
                ticketOrder.getQuantity(),
                ticketOrder.getTotalPrice(),
                ticketOrder.getUserId(),
                ticketOrder.getPurchaseTimestamp()
        );
    }

    @Override
    public List<TicketOrderDTO> getAllOrdersByUserId(Long userId) {
        List<TicketOrderEntity> orders = ticketOrderRepository.findByUserId(userId);

        return orders.stream().map(order -> new TicketOrderDTO(
                order.getId(),
                order.getTicket().getTicketName(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getUserId(),
                order.getPurchaseTimestamp()
        )).collect(Collectors.toList());
    }

}
