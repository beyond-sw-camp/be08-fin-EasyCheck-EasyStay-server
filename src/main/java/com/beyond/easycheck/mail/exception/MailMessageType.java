package com.beyond.easycheck.mail.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MailMessageType implements MessageType {


    VERIFICATION_EXPIRED("Your email verification has expired.", HttpStatus.REQUEST_TIMEOUT),
    VERIFICATION_CODE_INVALID("The verification code doesn't match.", HttpStatus.BAD_REQUEST),
    EMAIL_UNAUTHORIZED("This is an unauthenticated email.", HttpStatus.UNAUTHORIZED),

    ;
    private final String message;
    private final HttpStatus status;
}
