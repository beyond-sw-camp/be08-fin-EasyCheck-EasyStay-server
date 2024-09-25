package com.beyond.easycheck.common.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class EasyCheckException extends RuntimeException {

    private final String type;
    private final HttpStatus status;

    public EasyCheckException(MessageType messageType) {
        super(messageType.getMessage());
        type = messageType.name();
        status = messageType.getStatus();
    }
}
