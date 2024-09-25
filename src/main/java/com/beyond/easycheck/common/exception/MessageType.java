package com.beyond.easycheck.common.exception;

import org.springframework.http.HttpStatus;

public interface MessageType {
    String name();

    String getMessage();

    HttpStatus getStatus();
}
