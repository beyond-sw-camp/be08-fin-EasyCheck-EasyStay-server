package com.beyond.easycheck.permissions.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PermissionMessageType implements MessageType {

    PERMISSION_ALREADY_EXISTS("The specified permission already exists", HttpStatus.CONFLICT),
    PERMISSION_NOT_FOUND("The requested permission could not be found", HttpStatus.NOT_FOUND),
    PERMISSION_ALREADY_GRANTED("The permission has already been granted to this entity", HttpStatus.CONFLICT),
    CANNOT_REVOKE_NONEXISTENT_PERMISSION("Cannot revoke a permission that has not been granted", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus status;
}
