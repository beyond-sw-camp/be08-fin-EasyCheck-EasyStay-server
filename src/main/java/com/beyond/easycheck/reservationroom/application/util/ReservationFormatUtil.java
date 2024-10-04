package com.beyond.easycheck.reservationroom.application.util;

import com.beyond.easycheck.reservationroom.infrastructure.entity.PaymentStatus;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationFormatUtil {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 날짜 포맷
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(dateTimeFormatter);
    }

    // 예약 상태 포맷
    public static String formatReservationStatus(ReservationStatus status) {
        switch (status) {
            case RESERVATION:
                return "예약";
            case CANCELED:
                return "예약 취소";
            default:
                return "알 수 없음";
        }
    }

    // 결제 상태 포맷
    public static String formatPaymentStatus(PaymentStatus status) {
        switch (status) {
            case PAID:
                return "결제 완료";
            case UNPAID:
                return "결제 미완료";
            default:
                return "알 수 없음";
        }
    }
}