package com.beyond.easycheck.reservationroom.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationRoomMessageType implements MessageType {

    RESERVATION_NOT_FOUND("Reservation not found", HttpStatus.NOT_FOUND),
    ROOM_NOT_AVAILABLE("The room is not available for reservation.", HttpStatus.BAD_REQUEST),
    ROOM_ALREADY_FULL("The room is already fully booked.", HttpStatus.CONFLICT),
    RESERVATION_CANCELED("The reservation has been canceled.", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus status;
}
