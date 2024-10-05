package com.beyond.easycheck.reservationroom.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationRoomMessageType implements MessageType {

    BAD_REQUEST("Check API request URL protocol, parameter, etc. for errors",HttpStatus.BAD_REQUEST),
    ARGUMENT_NOT_VALID("The format of the argument passed is invalid.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("An error occurred inside the server.", HttpStatus.INTERNAL_SERVER_ERROR),
    RESERVATION_NOT_FOUND("Reservation not found", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("your request method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    ROOM_NOT_AVAILABLE("The room is not available for reservation.", HttpStatus.BAD_REQUEST),
    INVALID_CHECKIN_DATE("Check-in date must be after the current date.", HttpStatus.BAD_REQUEST),
    INVALID_CHECKOUT_DATE("Checkout date must be after check-in date.", HttpStatus.BAD_REQUEST),
    ROOM_ALREADY_BOOKED("The room is already booked for the selected dates.", HttpStatus.CONFLICT),
    ROOM_ALREADY_FULL("The room is already fully booked.", HttpStatus.CONFLICT),
    CANNOT_CANCEL_CHECKED_IN_RESERVATION("You cannot cancel a reservation that has already checked in.", HttpStatus.BAD_REQUEST);
    ;

    private final String message;
    private final HttpStatus status;
}
