package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.tickets.ui.requestbody.TicketOrderRequest;
import com.beyond.easycheck.tickets.ui.view.TicketOrderDTO;

public interface TicketOrderOperationUseCase {

    TicketOrderDTO createTicketOrder(Long userId, TicketOrderRequest request);

    void cancelTicketOrder(Long userId, Long orderId);

    void completeOrder(Long userId, Long orderId);
}
