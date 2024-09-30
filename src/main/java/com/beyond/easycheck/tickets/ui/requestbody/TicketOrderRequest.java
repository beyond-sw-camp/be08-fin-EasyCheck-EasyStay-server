package com.beyond.easycheck.tickets.ui.requestbody;

import com.beyond.easycheck.tickets.infrastructure.entity.CollectionAggrementType;
import com.beyond.easycheck.tickets.infrastructure.entity.ReceiptMethodType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TicketOrderRequest {
    private Long ticketId;
    private int quantity;
    private Long userId;  // 회원일 경우
    private Long guestId;  // 비회원일 경우
    private ReceiptMethodType receiptMethod;
    private CollectionAggrementType collectionAgreement;
}