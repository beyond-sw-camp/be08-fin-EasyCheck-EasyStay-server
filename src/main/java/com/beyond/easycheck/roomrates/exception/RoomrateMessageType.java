package com.beyond.easycheck.roomrates.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RoomrateMessageType implements MessageType {

    ROOM_RATE_NOT_FOUND("해당 객실 요금 ID는 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    ROOM_RATES_NOT_FOUND("객실 요금 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    ARGUMENT_NOT_VALID("잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus status;
}
