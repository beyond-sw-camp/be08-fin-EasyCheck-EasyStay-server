package com.beyond.easycheck.payments.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentMessageType implements MessageType {

    PAYMENT_NOT_FOUND("Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_VERIFICATION_FAILED("Payment verification failed.", HttpStatus.BAD_REQUEST),
    PORTONE_VERIFICATION_ERROR("PortOne 결제 검증 오류", HttpStatus.INTERNAL_SERVER_ERROR),
    PORTONE_REFUND_FAILED("PortOne 환불 처리 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String message;
    private final HttpStatus status;
}
