package com.beyond.easycheck.tickets.ui.controller;

import com.beyond.easycheck.common.ui.view.ApiResponseView;
import com.beyond.easycheck.tickets.application.service.TicketOrderService;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketOrderEntity;
import com.beyond.easycheck.tickets.ui.requestbody.TicketOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TicketOrder", description = "입장권 주문 정보 관리 API")
@RestController
@RequestMapping("/api/v1/parks/{themeParkId}/tickets/order")
@RequiredArgsConstructor
public class TicketOrderController {

    private final TicketOrderService ticketOrderService;

    @Operation(summary = "입장권 주문 추가하는 API")
    @PostMapping("")
    public ResponseEntity<ApiResponseView<TicketOrderEntity>> createTicketOrder(
            @PathVariable Long themeParkId,
            @RequestBody TicketOrderRequest request) {

        TicketOrderEntity ticketOrder = ticketOrderService.createTicketOrder(themeParkId,request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(ticketOrder));
    }
}
