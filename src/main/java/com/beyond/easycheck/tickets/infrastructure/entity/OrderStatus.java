package com.beyond.easycheck.tickets.infrastructure.entity;

public enum OrderStatus {
    PENDING,    // 결제 대기
    CANCELLED,  // 주문 취소
    CONFIRMED,  // 결제 완료
    COMPLETED,   // 주문 완료
    FAILED      // 결제 실패
}
