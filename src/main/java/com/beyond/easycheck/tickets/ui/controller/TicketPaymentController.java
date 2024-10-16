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

import java.util.List;

@Tag(name = "TicketPayment", description = "입장권 결제 정보 관리 API")
@RestController
@RequestMapping("/api/v1/tickets/payment")
@RequiredArgsConstructor
public class TicketPaymentController {

    private final TicketPaymentService ticketPaymentService;

    @Operation(summary = "입장권 결제 추가하는 API")
    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponseView<TicketPaymentEntity>> processPayment(
            @PathVariable Long orderId,
            @RequestBody TicketPaymentRequest request,
            @AuthenticationPrincipal Long userId) {

        TicketPaymentEntity payment = ticketPaymentService.processPayment(orderId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(payment));
    }

    @Operation(summary = "입장권 결제 취소 API")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponseView<TicketPaymentEntity>> cancelPayment(
            @PathVariable Long orderId,
            @RequestBody String cancelReason,
            @AuthenticationPrincipal Long userId) {

        TicketPaymentEntity cancelledPayment = ticketPaymentService.cancelPayment(orderId, userId, cancelReason);
        return ResponseEntity.ok(new ApiResponseView<>(cancelledPayment));
    }

    @Operation(summary = "입장권 결제 환불 API")
    @PatchMapping("/{orderId}/refund")
    public ResponseEntity<ApiResponseView<TicketPaymentEntity>> refundPayment(
            @PathVariable Long orderId,
            @RequestBody String refundReason,
            @AuthenticationPrincipal Long userId) {

        TicketPaymentEntity refundedPayment = ticketPaymentService.refundPayment(orderId, userId, refundReason);
        return ResponseEntity.ok(new ApiResponseView<>(refundedPayment));
    }

    @Operation(summary = "입장권 결제 상태 조회 API")
    @GetMapping("/{orderId}/status")
    public ResponseEntity<ApiResponseView<TicketPaymentEntity>> getPaymentStatus(
            @PathVariable Long orderId) {

        TicketPaymentEntity paymentStatus = ticketPaymentService.getPaymentStatus(orderId);
        return ResponseEntity.ok(new ApiResponseView<>(paymentStatus));
    }

    @Operation(summary = "사용자의 결제 내역 조회 API")
    @GetMapping("/history")
    public ResponseEntity<ApiResponseView<List<TicketPaymentEntity>>> getPaymentHistory(
            @AuthenticationPrincipal Long userId) {

        List<TicketPaymentEntity> paymentHistory = ticketPaymentService.getPaymentHistory(userId);
        return ResponseEntity.ok(new ApiResponseView<>(paymentHistory));
    }

    @Operation(summary = "입장권 결제 재시도 API")
    @PatchMapping("/{orderId}/retry")
    public ResponseEntity<ApiResponseView<TicketPaymentEntity>> retryPayment(
            @PathVariable Long orderId,
            @RequestBody TicketPaymentRequest request,
            @AuthenticationPrincipal Long userId) {

        TicketPaymentEntity retriedPayment = ticketPaymentService.retryPayment(orderId, userId, request);
        return ResponseEntity.ok(new ApiResponseView<>(retriedPayment));
    }
}
