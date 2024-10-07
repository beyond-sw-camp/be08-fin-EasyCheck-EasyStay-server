package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.tickets.ui.requestbody.TicketOrderRequest;
import com.beyond.easycheck.tickets.ui.view.TicketOrderDTO;

public interface TicketOrderOperationUseCase {

    TicketOrderDTO createTicketOrder(Long userId, Long themeParkId, TicketOrderRequest request);

    void cancelTicketOrder(Long userId, Long themeParkId, Long orderId);

    void completeOrder(Long userId, Long themeParkId, Long orderId);
}
