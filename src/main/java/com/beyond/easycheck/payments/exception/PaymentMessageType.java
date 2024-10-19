package com.beyond.easycheck.payments.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentMessageType implements MessageType {

    BAD_REQUEST("Check API request URL protocol, parameter, etc. for errors", HttpStatus.BAD_REQUEST),
    ARGUMENT_NOT_VALID("The format of the argument passed is invalid.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("An error occurred inside the server.", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_NOT_FOUND("Payment not found", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("your request method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    INVALID_PAYMENT_AMOUNT("The payment amount does not match the reservation total price.", HttpStatus.BAD_REQUEST),
    PAYMENT_VERIFICATION_FAILED("Payment verification failed.", HttpStatus.BAD_REQUEST),
    PORTONE_VERIFICATION_ERROR("PortOne 결제 검증 오류", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String message;
    private final HttpStatus status;
}
