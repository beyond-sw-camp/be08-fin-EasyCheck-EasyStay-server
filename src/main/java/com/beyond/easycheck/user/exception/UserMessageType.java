package com.beyond.easycheck.user.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserMessageType implements MessageType {

    USER_ALREADY_REGISTERED("This user is already registered.", HttpStatus.CONFLICT),
    USER_ROLE_NOT_FOUND("The role for this user does not exist.", HttpStatus.INTERNAL_SERVER_ERROR),

    EMAIL_UNAUTHORIZED("This is an unauthenticated email.", HttpStatus.UNAUTHORIZED),
    ;
    private final String message;
    private final HttpStatus status;
}
