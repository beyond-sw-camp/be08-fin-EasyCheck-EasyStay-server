package com.beyond.easycheck.facilities.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FacilityMessageType implements MessageType {

    BAD_REQUEST("Check API request URL protocol, parameter, etc. for errors", HttpStatus.BAD_REQUEST),
    ARGUMENT_NOT_VALID("The format of the argument passed is invalid.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("An error occurred inside the server.", HttpStatus.INTERNAL_SERVER_ERROR),
    FACILITY_NOT_FOUND("Facility not found", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("your request method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    NO_IMAGES_PROVIDED("업로드 이미지가 없습니다.", HttpStatus.BAD_REQUEST),
    IMAGE_UPDATE_FAILED("이미지 업로드에 실패했습니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus status;
}
