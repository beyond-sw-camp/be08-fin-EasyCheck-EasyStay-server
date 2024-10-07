package com.beyond.easycheck.corporate.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CorporateMessageType implements MessageType {



    INVALID_BUSINESS_LICENSE_NUMBER("", HttpStatus.BAD_REQUEST),

    ;

    private final String message;
    private final HttpStatus status;
}
