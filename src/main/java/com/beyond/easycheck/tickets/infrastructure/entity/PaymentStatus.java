package com.beyond.easycheck.tickets.infrastructure.entity;

public enum PaymentStatus {
    PENDING,       // 결제 대기
    CANCELLED,     // 결제 취소
    COMPLETED,     // 결제 완료
    FAILED,         // 결제 실패
    REFUNDED     // 환불
}
