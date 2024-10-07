package com.beyond.easycheck.tickets.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TicketOrderMessageType implements MessageType {

    ORDER_NOT_FOUND("해당 주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_CANCELLED("이미 취소된 주문입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_COMPLETED("이미 완료된 결제입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_FOUND("해당 결제를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_ORDER_STATUS_FOR_PAYMENT("주문이 결제 가능한 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS_FOR_COMPLETION("주문이 완료 가능한 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS_FOR_CANCELLATION("주문이 취소 가능한 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACCESS("접근 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    ;
   private final String message;
   private final HttpStatus status;
}
