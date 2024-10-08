package com.beyond.easycheck.tickets.ui.controller;

import com.beyond.easycheck.common.ui.view.ApiResponseView;
import com.beyond.easycheck.tickets.application.service.TicketPaymentService;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketPaymentEntity;
import com.beyond.easycheck.tickets.ui.requestbody.TicketPaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TicketPayment", description = "입장권 결제 정보 관리 API")
@RestController
@RequestMapping("/api/v1/tickets/order/{orderId}/payment")
@RequiredArgsConstructor
public class TicketPaymentController {

    private final TicketPaymentService ticketPaymentService;

    @Operation(summary = "입장권 결제 추가하는 API")
    @PostMapping("")
    public ResponseEntity<ApiResponseView<TicketPaymentEntity>> processPayment(
            @PathVariable Long orderId,
            @RequestBody TicketPaymentRequest request,
            @AuthenticationPrincipal Long userId) {

        TicketPaymentEntity payment = ticketPaymentService.processPayment(orderId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(payment));
    }

    @Operation(summary = "입장권 결제 취소 API")
    @PutMapping("/cancel")
    public ResponseEntity<ApiResponseView<TicketPaymentEntity>> cancelPayment(
            @PathVariable Long orderId,
            @RequestBody String cancelReason,
            @AuthenticationPrincipal Long userId) {

        TicketPaymentEntity cancelledPayment = ticketPaymentService.cancelPayment(orderId, userId, cancelReason);
        return ResponseEntity.ok(new ApiResponseView<>(cancelledPayment));
    }
}
