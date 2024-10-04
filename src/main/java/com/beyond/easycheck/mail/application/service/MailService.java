package com.beyond.easycheck.mail.application.service;

import com.beyond.easycheck.reservationroom.ui.view.ReservationRoomView;

public interface MailService {

    void sendVerificationCode(String email);

    void verifyEmail(String code);

    void sendReservationConfirmationEmail(String email, ReservationRoomView reservationDetails);
}
