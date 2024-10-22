package com.beyond.easycheck.admin.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminMessageType implements MessageType {

    ACCOMMODATION_ADMIN_AUTHORITY_NOT_FOUND("숙소 관리자 권한이 존재하지 않습니다.", HttpStatus.FORBIDDEN),
    ;

    private final String message;
    private final HttpStatus status;
}