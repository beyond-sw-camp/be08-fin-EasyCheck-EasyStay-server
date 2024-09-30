package com.beyond.easycheck.payments.ui.controller;

import com.beyond.easycheck.payments.application.service.PaymentService;
import com.beyond.easycheck.payments.ui.requestbody.PaymentCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment", description = "결제 관리")
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "객실 예약을 결제하는 API")
    @PostMapping("")
    public ResponseEntity<PaymentCreateRequest> createPayment(
            @RequestBody @Valid PaymentCreateRequest paymentCreateRequest) {

        paymentService.createPayment(paymentCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
