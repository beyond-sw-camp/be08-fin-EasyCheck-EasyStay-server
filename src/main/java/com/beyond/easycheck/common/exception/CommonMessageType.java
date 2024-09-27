package com.beyond.easycheck.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@RequiredArgsConstructor
public enum CommonMessageType implements MessageType {

    // common errors
    BAD_REQUEST("Check API request URL protocol, parameter, etc. for errors", HttpStatus.BAD_REQUEST),
    NOT_FOUND("No data was found for the server. Please refer  to parameter description.", HttpStatus.NOT_FOUND),
    ARGUMENT_NOT_VALID("The format of the argument passed is invalid.", HttpStatus.BAD_REQUEST),

    SQL_EXCEPTION_ERROR("", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR("An error occurred inside the server.", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_CONNECTION_FAILED("데이터베이스 연결에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR)
    ;
    private final String message;
    private final HttpStatus status;
}
