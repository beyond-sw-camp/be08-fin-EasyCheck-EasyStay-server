package com.beyond.easycheck.permissions.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PermissionMessageType implements MessageType {

    PERMISSION_ALREADY_EXISTS("", HttpStatus.CONFLICT),
    PERMISSION_NOT_FOUND("", HttpStatus.NOT_FOUND),
    PERMISSION_ALREADY_GRANTED("", HttpStatus.CONFLICT),

    CANNOT_REVOKE_NONEXISTENT_PERMISSION("", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus status;
}
