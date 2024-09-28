package com.beyond.easycheck.themeParks.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ThemeParkMessageType implements MessageType {

    THEME_PARK_NOT_FOUND("테마파크를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    VALIDATION_FAILED("잘못된 입력값이 있습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_THEME_PARK("중복된 테마파크가 존재합니다.", HttpStatus.CONFLICT),
    DATABASE_CONNECTION_FAILED("데이터베이스 연결에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    private final String message;
    private final HttpStatus status;
}
