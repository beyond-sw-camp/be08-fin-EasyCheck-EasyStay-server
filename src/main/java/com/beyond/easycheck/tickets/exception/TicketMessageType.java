package com.beyond.easycheck.tickets.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TicketMessageType implements MessageType {

    TICKET_NOT_FOUND("해당 입장권을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TICKET_NOT_BELONG_TO_THEME_PARK("해당 티켓은 이 테마파크에 속해 있지 않습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_TICKET("이미 동일한 티켓이 존재합니다.", HttpStatus.CONFLICT),
    MISSING_REQUIRED_FIELD("필수 필드가 누락되었습니다.", HttpStatus.BAD_REQUEST),
    ;
   private final String message;
   private final HttpStatus status;
}
