package com.beyond.easycheck.reservationservices.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationServiceMessageType implements MessageType {

    BAD_REQUEST("Check API request URL protocol, parameter, etc. for errors", HttpStatus.BAD_REQUEST),
    ARGUMENT_NOT_VALID("The format of the argument passed is invalid.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("An error occurred inside the server.", HttpStatus.INTERNAL_SERVER_ERROR),
    RESERVATION_SERVICE_NOT_FOUND("Reservation Service not found", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("your request method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    SERVICE_ALREADY_ADDED("This service has already been added to the reservation.", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY("The quantity must be greater than 0.", HttpStatus.BAD_REQUEST),
    INVALID_TOTAL_PRICE("The total price must match the quantity multiplied by the service price.", HttpStatus.BAD_REQUEST),
    RESERVATION_CANCELED("The reservation has been canceled and cannot be modified.", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus status;
}
