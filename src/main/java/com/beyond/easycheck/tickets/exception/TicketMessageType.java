package com.beyond.easycheck.tickets.exception;

import com.beyond.easycheck.common.exception.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TicketMessageType implements MessageType {

    TICKET_NOT_FOUND("해당 입장권을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

   private final String message;
   private final HttpStatus status;
}
