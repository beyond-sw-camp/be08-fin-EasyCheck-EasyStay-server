package com.beyond.easycheck.tickets.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TicketMessageType implements MessageType {

    THEME_PARK_NOT_FOUND("해당 테마파크를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TICKET_NOT_FOUND("해당 입장권을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_TICKET("이미 동일한 티켓이 존재합니다.", HttpStatus.CONFLICT),
    MISSING_REQUIRED_FIELD("필수 입력 항목이 누락되었습니다.", HttpStatus.BAD_REQUEST),
    TICKET_NOT_BELONG_TO_THEME_PARK("입장권이 해당 테마파크에 속하지 않습니다.", HttpStatus.BAD_REQUEST),
    TICKET_SALE_PERIOD_INVALID("입장권의 판매 기간이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY("주문 수량이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_OR_GUEST("사용자 ID 또는 게스트 ID 중 하나는 필수입니다.", HttpStatus.BAD_REQUEST),

    ;

    private final String message;
    private final HttpStatus status;
}
