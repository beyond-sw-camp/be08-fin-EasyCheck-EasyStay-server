package com.beyond.easycheck.themeparks.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ThemeParkMessageType implements MessageType {

    THEME_PARK_NOT_FOUND("테마파크를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    THEME_PARK_CREATION_FAILED("테마파크 생성에 실패했습니다.", HttpStatus.BAD_REQUEST);
    private final String message;
    private final HttpStatus status;
}
