package com.beyond.easycheck.mail.application.service;

public interface MailService {

    void sendVerificationCode(String email);

    void verifyEmail(String code);
}
