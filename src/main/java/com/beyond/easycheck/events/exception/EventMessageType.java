package com.beyond.easycheck.events.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EventMessageType implements MessageType {

    EVENT_NOT_FOUND("해당 ID의 이벤트가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus status;
}
