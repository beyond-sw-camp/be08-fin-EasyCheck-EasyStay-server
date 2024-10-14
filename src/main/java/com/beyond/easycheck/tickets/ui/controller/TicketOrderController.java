package com.beyond.easycheck.tickets.ui.controller;

import com.beyond.easycheck.common.ui.view.ApiResponseView;
import com.beyond.easycheck.tickets.application.service.TicketOrderOperationUseCase;
import com.beyond.easycheck.tickets.application.service.TicketOrderReadUseCase;
import com.beyond.easycheck.tickets.ui.requestbody.TicketOrderRequest;
import com.beyond.easycheck.tickets.ui.view.TicketOrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "TicketOrder", description = "입장권 주문 정보 관리 API")
@RestController
@RequestMapping("/api/v1/tickets/orders")
@RequiredArgsConstructor
public class TicketOrderController {

    private final TicketOrderOperationUseCase ticketOrderOperationUseCase;
    private final TicketOrderReadUseCase ticketOrderReadUseCase;

    @Operation(summary = "입장권 주문 추가하는 API")
    @PostMapping("")
    public ResponseEntity<ApiResponseView<TicketOrderDTO>> createTicketOrder(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Validated TicketOrderRequest request) {

        TicketOrderDTO ticketOrder = ticketOrderOperationUseCase.createTicketOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(ticketOrder));
    }

    @Operation(summary = "입장권 주문 조회하는 API")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseView<TicketOrderDTO>> getOrder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId) {

        TicketOrderDTO orderDTO = ticketOrderReadUseCase.getTicketOrder(userId, orderId);
        return ResponseEntity.ok(new ApiResponseView<>(orderDTO));
    }

    @Operation(summary = "사용자의 입장권 주문 조회하는 API")
    @GetMapping("/me")
    public ResponseEntity<ApiResponseView<List<TicketOrderDTO>>> getMyOrders(
            @AuthenticationPrincipal Long userId) {

        List<TicketOrderDTO> orderDTOList = ticketOrderReadUseCase.getAllOrdersByUserId(userId);
        return ResponseEntity.ok(new ApiResponseView<>(orderDTOList));
    }

    @Operation(summary = "입장권 주문 취소하는 API")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId) {

        ticketOrderOperationUseCase.cancelTicketOrder(userId, orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "입장권 사용자가 사용해서 사용완료로 상태를 변경하는 API")
    @PatchMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId) {

        ticketOrderOperationUseCase.completeOrder(userId, orderId);
        return ResponseEntity.ok().build();
    }
}
