package com.beyond.easycheck.common.security.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtMessageType implements MessageType {

    TOKEN_EXPIRED("", HttpStatus.FORBIDDEN),
    ;
    private final String message;
    private final HttpStatus status;
}
