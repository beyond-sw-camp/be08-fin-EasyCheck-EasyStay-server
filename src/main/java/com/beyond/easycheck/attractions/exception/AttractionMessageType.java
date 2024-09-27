package com.beyond.easycheck.attractions.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AttractionMessageType implements MessageType {

    ATTRACTION_NOT_FOUND("어트랙션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    VALIDATION_FAILED("잘못된 입력값이 있습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_ATTRACTION("중복된 어트랙션이 존재합니다.", HttpStatus.CONFLICT),
    ;
    private final String message;
    private final HttpStatus status;
}
