package com.beyond.easycheck.seasons.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SeasonMessageType implements MessageType {

    ARGUMENT_NOT_VALID("입력 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    SEASON_NOT_FOUND("해당 시즌 ID가 존재하지 않습니다", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus status;
}
