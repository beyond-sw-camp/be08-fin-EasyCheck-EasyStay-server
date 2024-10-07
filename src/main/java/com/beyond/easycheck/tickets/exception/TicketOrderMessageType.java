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
    ;
   private final String message;
   private final HttpStatus status;
}
