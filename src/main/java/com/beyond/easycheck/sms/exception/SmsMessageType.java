package com.beyond.easycheck.sms.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SmsMessageType implements MessageType {

    INVALID_VERIFICATION_CODE("", HttpStatus.BAD_REQUEST),
    SMS_VERIFICATION_CODE_NOT_MATCHED("", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus status;
}
