package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.tickets.ui.view.TicketOrderDTO;

import java.util.List;

public interface TicketOrderReadUseCase {
    TicketOrderDTO getTicketOrder(Long orderId);

    List<TicketOrderDTO> getAllOrdersByUserId(Long userId);
}
