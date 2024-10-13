package com.beyond.easycheck.events.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EventMessageType implements MessageType {

    EVENT_NOT_FOUND("해당 ID의 이벤트가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    EVENTS_NOT_FOUND("존재하는 이벤트가 없습니다.", HttpStatus.NOT_FOUND),
    ARGUMENT_NOT_VALID("잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND("이벤트 이미지가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus status;
}
