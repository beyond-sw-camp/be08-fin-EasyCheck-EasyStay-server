package com.beyond.easycheck.mail.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MailServiceImplTest {

    @Autowired
    private MailService mailService;

    @Test
    void sendVerificationCode() {
        System.out.println("hello");
        mailService.sendVerificationCode("enjoy2573@naver.com");
    }

    @Test
    void verifyEmail() {
    }
}