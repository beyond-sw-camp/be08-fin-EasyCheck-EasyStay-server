package com.beyond.easycheck.mail.application.service;

import com.beyond.easycheck.common.exception.CommonMessageType;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.mail.exception.MailMessageType;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerificationCodeEntity;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerifiedEmailEntity;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerificationCodeRepository;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerifiedEmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{

    private static final String MAIL_SUBJECT = "EasyCheck 이메일 인증코드";

    public static final Long VERIFICATION_EXPIRED_TIME = 300L;

    private final JavaMailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerifiedEmailRepository verifiedEmailRepository;

    @Override
    @Transactional
    public void sendVerificationCode(String email) {

        final String SENDER_EMAIL_ADDRESS = "yonginfren@gmail.com";

        log.info("sender = {}", SENDER_EMAIL_ADDRESS);
        // html 형식으로 내용을 첨부하기 위한 객체
        MimeMessage message = mailSender.createMimeMessage();

        try {
            // 송신자 메일 설정
            message.setFrom(new InternetAddress(SENDER_EMAIL_ADDRESS));
            // 인증 코드 생성
            String verificationCode = generateVerificationCode();

            VerificationCodeEntity verificationCodeEntity = VerificationCodeEntity.createVerificationCode(email, verificationCode, VERIFICATION_EXPIRED_TIME);

            // 현재 이메일과 인증코드 레디스에 저장
            verificationCodeRepository.save(verificationCodeEntity);

            // 메일 내용 html로 생성
            String htmlContent = generateEmailContent(verificationCode);
            // 수신자 이메일 주소 설정
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            // 메일 제목 설정
            message.setSubject(MAIL_SUBJECT);
            // 메일 내용 설정
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // 메일 전송
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EasyCheckException(CommonMessageType.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void verifyEmail(String code) {

        VerificationCodeEntity verified = verificationCodeRepository.findByCode(code)
                .orElseThrow(() -> new EasyCheckException(MailMessageType.VERIFICATION_CODE_INVALID));

        // 이메일 인증 성공 했을 경우
        // 인증된 이메일 저장소에 저장
        VerifiedEmailEntity verifiedEmail = VerifiedEmailEntity.createVerifiedEmail(verified.getEmail());
        verifiedEmailRepository.save(verifiedEmail);

        // 인증 코드 삭제
        verificationCodeRepository.delete(verified);

    }

    private String generateVerificationCode() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final int CODE_LENGTH = 8;
        final SecureRandom RANDOM = new SecureRandom();

        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();

    }

    private String generateEmailContent(String verificationCode) {

        return "<!DOCTYPE html>" +
                "<html lang=\"ko\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>EasyCheck 인증 코드</title>" +
                "</head>" +
                "<body style=\"font-family: 'Helvetica Neue', Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4;\">" +
                "<table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">" +
                "<tr>" +
                "<td align=\"center\" style=\"padding: 40px 0;\">" +
                "<table role=\"presentation\" style=\"width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);\">" +
                "<tr>" +
                "<td style=\"padding: 40px 40px 20px 40px; text-align: center;\">" +
                "<img src=\"https://example.com/ticketlink-logo.png\" alt=\"티켓링크 로고\" style=\"max-width: 200px; height: auto;\">" +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"padding: 0 40px;\">" +
                "<h1 style=\"color: #333333; font-size: 24px; margin-bottom: 20px; text-align: center;\">인증 코드 안내</h1>" +
                "<p style=\"color: #666666; font-size: 16px; line-height: 1.5; margin-bottom: 20px;\">" +
                "안녕하세요,<br><br>" +
                "EasyCheck를 이용해 주셔서 감사합니다. 아래의 인증 코드를 입력하여 본인 확인을 완료해 주세요." +
                "</p>" +
                "<div style=\"background-color: #f8f8f8; border-radius: 4px; padding: 20px; text-align: center; margin-bottom: 20px;\">" +
                "<h2 style=\"color: #e74c3c; font-size: 32px; margin: 0; letter-spacing: 5px;\">" +
                verificationCode +
                "</h2>" +
                "</div>" +
                "<p style=\"color: #666666; font-size: 14px; line-height: 1.5; margin-bottom: 30px;\">" +
                "이 인증 코드는 5분 동안 유효합니다. 본 메일을 요청하지 않으셨다면, 이 메일을 무시해 주세요." +
                "</p>" +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"padding: 20px 40px 40px 40px;\">" +
                "<p style=\"color: #666666; font-size: 16px; line-height: 1.5; margin-bottom: 10px;\">" +
                "감사합니다,<br>" +
                "티켓링크 팀" +
                "</p>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "<table role=\"presentation\" style=\"width: 600px; border-collapse: collapse;\">" +
                "<tr>" +
                "<td style=\"padding: 20px 0; text-align: center;\">" +
                "<p style=\"color: #999999; font-size: 12px; margin: 0;\">" +
                "© 2024 EasyCheck. All rights reserved.<br>" +
                "서울특별시 강남구 테헤란로 123, EasyStay 타워" +
                "</p>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</body>" +
                "</html>";
    }





}
