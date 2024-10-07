package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.tickets.ui.requestbody.TicketOrderRequest;
import com.beyond.easycheck.tickets.ui.view.TicketOrderDTO;

public interface TicketOrderOperationUseCase {

    TicketOrderDTO createTicketOrder(Long themeParkId, TicketOrderRequest request);

    void cancelTicketOrder(Long orderId);
}
