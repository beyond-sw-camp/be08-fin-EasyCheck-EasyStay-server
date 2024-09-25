package com.beyond.easycheck.accomodations.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class AccommodationException extends RuntimeException {

    private final String type;
    private final HttpStatus status;

    public AccommodationException(AccommodationMessageType message) {
        super(message.getMessage());
        this.type = message.name();
        this.status = message.getStatus();
    }
}
