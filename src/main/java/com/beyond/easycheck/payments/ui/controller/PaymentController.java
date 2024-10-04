package com.beyond.easycheck.payments.ui.controller;

import com.beyond.easycheck.payments.application.service.PaymentService;
import com.beyond.easycheck.payments.ui.requestbody.PaymentCreateRequest;
import com.beyond.easycheck.payments.ui.requestbody.PaymentUpdateRequest;
import com.beyond.easycheck.payments.ui.view.PaymentView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Payment", description = "결제 관리")
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 처리하는 API")
    @PostMapping("")
    public ResponseEntity<Void> processPayment(@RequestBody @Valid PaymentCreateRequest paymentCreateRequest) {

        paymentService.processReservationPayment(paymentCreateRequest.getReservationId(), paymentCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "결제 내역 리스트를 조회하는 API")
    @GetMapping("")
    public ResponseEntity<List<PaymentView>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<PaymentView> payments = paymentService.getAllPayments(page, size);

        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "특정 결제 내역을 조회하는 API")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentView> getPaymentById(@PathVariable("id") Long id) {

        PaymentView paymentView = paymentService.getPaymentById(id);

        return ResponseEntity.ok(paymentView);
    }

    @Operation(summary = "결제를 취소하는 API")
    @PutMapping("/{id}")
    public ResponseEntity<Void> cancelPayment(@PathVariable("id") Long id,
                                              @RequestBody @Valid PaymentUpdateRequest paymentUpdateRequest) {

        paymentService.cancelPayment(id, paymentUpdateRequest);

        return ResponseEntity.noContent().build();
    }
}
