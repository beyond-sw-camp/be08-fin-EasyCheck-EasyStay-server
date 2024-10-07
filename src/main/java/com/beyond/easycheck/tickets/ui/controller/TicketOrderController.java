package com.beyond.easycheck.tickets.ui.controller;

import com.beyond.easycheck.common.ui.view.ApiResponseView;
import com.beyond.easycheck.tickets.application.service.TicketOrderOperationUseCase;
import com.beyond.easycheck.tickets.application.service.TicketOrderReadUseCase;
import com.beyond.easycheck.tickets.application.service.TicketOrderService;
import com.beyond.easycheck.tickets.ui.requestbody.TicketOrderRequest;
import com.beyond.easycheck.tickets.ui.view.TicketOrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "TicketOrder", description = "입장권 주문 정보 관리 API")
@RestController
@RequestMapping("/api/v1/parks/{themeParkId}/tickets/orders")
@RequiredArgsConstructor
public class TicketOrderController {

    private final TicketOrderOperationUseCase ticketOrderOperationUseCase;
    private final TicketOrderReadUseCase ticketOrderReadUseCase;

    @Operation(summary = "입장권 주문 추가하는 API")
    @PostMapping("")
    public ResponseEntity<ApiResponseView<TicketOrderDTO>> createTicketOrder(
            @PathVariable Long themeParkId,
            @RequestBody @Validated TicketOrderRequest request) {

        TicketOrderDTO ticketOrder = ticketOrderOperationUseCase.createTicketOrder(themeParkId,request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(ticketOrder));
    }

    @Operation(summary = "입장권 주문 조회하는 API")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseView<TicketOrderDTO>> getOrder(@PathVariable Long orderId) {
        TicketOrderDTO orderDTO = ticketOrderReadUseCase.getTicketOrder(orderId);
        return ResponseEntity.ok(new ApiResponseView<>(orderDTO));
    }

    @Operation(summary = "사용자의 입장권 주문 조회하는 API")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseView<List<TicketOrderDTO>>> getOrdersByUser(@PathVariable Long userId) {
        List<TicketOrderDTO> orderDTOList = ticketOrderReadUseCase.getAllOrdersByUserId(userId);
        return ResponseEntity.ok(new ApiResponseView<>(orderDTOList));
    }

    @Operation(summary = "입장권 주문 취소하는 API")
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        ticketOrderOperationUseCase.cancelTicketOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
