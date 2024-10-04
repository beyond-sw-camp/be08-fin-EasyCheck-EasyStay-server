package com.beyond.easycheck.mail.application.service;

import com.beyond.easycheck.common.exception.CommonMessageType;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.mail.exception.MailMessageType;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerificationCodeEntity;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerifiedEmailEntity;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerificationCodeRepository;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerifiedEmailRepository;
import com.beyond.easycheck.reservationroom.application.util.ReservationFormatUtil;
import com.beyond.easycheck.reservationroom.ui.view.ReservationRoomView;
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
    private static final String MAIL_RESERVATION = "EasyCheck 예약 안내";

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
            String htmlContent = generateVerificationCodeEmailContent(verificationCode);

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

    @Override
    @Transactional
    public void sendReservationConfirmationEmail(String email, ReservationRoomView reservationDetails) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            // 송신자 메일 설정
            final String SENDER_EMAIL_ADDRESS = "yonginfren@gmail.com";
            message.setFrom(new InternetAddress(SENDER_EMAIL_ADDRESS));

            // 메일 수신자 설정
            message.setRecipients(MimeMessage.RecipientType.TO, email);

            // 메일 제목 설정
            message.setSubject(MAIL_RESERVATION);

            // 메일 본문 내용 생성
            String htmlContent = generateReservationConfirmationEmailContent(reservationDetails);

            // 메일 내용 설정
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // 메일 전송
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send reservation confirmation email", e);
            throw new EasyCheckException(CommonMessageType.INTERNAL_SERVER_ERROR);
        }
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

    private String generateReservationConfirmationEmailContent(ReservationRoomView reservationDetails) {

        String title = "EasyCheck 예약 안내 메일";
        String mainContent = "<div style=\"width: 100%; max-width: 600px; margin: 0 auto; font-family: Arial, sans-serif;\">" +
                "<div style=\"background-color: #f8f8f8; padding: 20px; border-radius: 8px;\">" +
                "<h1 style=\"color: #FF6B35; font-size: 24px; margin-bottom: 15px; text-align: center; font-weight: bold;\">" +
                "EasyCheck 예약 내역</h1>" +
                "<p style=\"font-size: 16px; color: #333333; text-align: center;\">" +
                "안녕하세요, <strong>" + reservationDetails.getUserName() + "</strong>님.</p>" +
                "<p style=\"font-size: 16px; color: #333333; text-align: center; margin-bottom: 20px;\">" +
                "다음과 같이 예약 내역을 확인해주세요.</p>" +
                "<div style=\"background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1); margin-top: 20px;\">" +
                "<table style=\"width: 100%; border-collapse: collapse; font-size: 16px; color: #333333;\">" +
                "<tr>" +
                "<td style=\"padding: 10px; border-bottom: 1px solid #ddd; border-right: 1px solid #ddd;\"><strong>객실 이름</strong></td>" +
                "<td style=\"padding: 10px; border-bottom: 1px solid #ddd;\">" + reservationDetails.getTypeName() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"padding: 10px; border-bottom: 1px solid #ddd; border-right: 1px solid #ddd;\"><strong>체크인 날짜</strong></td>" +
                "<td style=\"padding: 10px; border-bottom: 1px solid #ddd;\">" + ReservationFormatUtil.formatLocalDateTime(reservationDetails.getCheckinDate()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"padding: 10px; border-bottom: 1px solid #ddd; border-right: 1px solid #ddd;\"><strong>체크아웃 날짜</strong></td>" +
                "<td style=\"padding: 10px; border-bottom: 1px solid #ddd;\">" + ReservationFormatUtil.formatLocalDateTime(reservationDetails.getCheckoutDate()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"padding: 10px; border-bottom: 1px solid #ddd; border-right: 1px solid #ddd;\"><strong>예약 상태</strong></td>" +
                "<td style=\"padding: 10px; border-bottom: 1px solid #ddd;\">" + ReservationFormatUtil.formatReservationStatus(reservationDetails.getReservationStatus()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"padding: 10px; border-bottom: 1px solid #ddd; border-right: 1px solid #ddd;\"><strong>총 가격</strong></td>" +
                "<td style=\"padding: 10px; border-bottom: 1px solid #ddd;\">" + reservationDetails.getTotalPrice() + "원</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"padding: 10px; border-right: 1px solid #ddd;\"><strong>결제 상태</strong></td>" +
                "<td style=\"padding: 10px;\">" + ReservationFormatUtil.formatPaymentStatus(reservationDetails.getPaymentStatus()) + "</td>" +
                "</tr>" +
                "</table>" +
                "</div>" +
                "</div>" +
                "<p style=\"color: #666666; font-size: 14px; text-align: center; margin-top: 20px;\">" +
                "감사합니다,<br><strong>EasyCheck 팀</strong></p>" +
                "</div>";

        return generateEmailTemplate(title, mainContent);
    }

    private String generateCustomerInquiryResponseContent(String customerName, String inquirySubject, String responseContent) {
        String title = "EasyCheck 고객 건의사항 답변";
        String mainContent =
                "<h1 style=\"color: #FF6B35; font-size: 24px; margin: 0 0 15px 0; text-align: center; font-weight: bold;\">고객 건의사항 답변</h1>" +
                        "<p style=\"color: #333333; font-size: 16px; line-height: 1.4; margin: 0 0 20px 0; text-align: left;\">" +
                        "안녕하세요 " + customerName + "님,<br><br>" +
                        "귀하의 소중한 의견에 감사드립니다. 아래와 같이 답변 드립니다." +
                        "</p>" +
                        "<div style=\"background-color: #f8f8f8; border-radius: 8px; padding: 20px; margin-bottom: 20px;\">" +
                        "<h2 style=\"color: #FF6B35; font-size: 18px; margin: 0 0 10px 0;\">건의사항: " + inquirySubject + "</h2>" +
                        "<p style=\"color: #333333; font-size: 16px; line-height: 1.6; margin: 0;\">" +
                        responseContent +
                        "</p>" +
                        "</div>" +
                        "<p style=\"color: #666666; font-size: 14px; line-height: 1.4; margin: 0 0 15px 0; text-align: left;\">" +
                        "추가 문의사항이 있으시면 언제든 연락 주시기 바랍니다." +
                        "</p>" +
                        "<p style=\"color: #333333; font-size: 14px; line-height: 1.4; margin: 0; text-align: left;\">" +
                        "감사합니다,<br>" +
                        "<strong>EasyCheck 고객지원팀</strong>" +
                        "</p>";

        return generateEmailTemplate(title, mainContent);
    }

    private String generateVerificationCodeEmailContent(String verificationCode) {
        String title = "EasyCheck 인증 코드";
        String mainContent = "<h1 style=\"color: #FF6B35; font-size: 24px; margin: 0 0 15px 0; text-align: center; font-weight: bold;\">인증 코드 안내</h1>" +
                "<p style=\"color: #333333; font-size: 16px; line-height: 1.4; margin: 0 0 10px 0; text-align: center;\">" +
                "안녕하세요,<br>" +
                "아래의 인증 코드를 입력해 주세요." +
                "</p>" +
                "<div style=\"background-color: #f8f8f8; border-radius: 8px; padding: 15px; text-align: center; margin-bottom: 15px;\">" +
                "<h2 style=\"color: #FF6B35; font-size: 32px; margin: 0; letter-spacing: 5px; font-weight: bold;\">" +
                verificationCode +
                "</h2>" +
                "</div>" +
                "<p style=\"color: #666666; font-size: 14px; line-height: 1.4; margin: 0 0 15px 0; text-align: left;\">" +
                "* 인증 코드는 5분간 유효합니다." +
                "</p>" +
                "<p style=\"color: #333333; font-size: 14px; line-height: 1.4; margin: 0; text-align: left;\">" +
                "감사합니다,<br>" +
                "<strong>EasyCheck 팀</strong>" +
                "</p>";

        return generateEmailTemplate(title, mainContent);
    }

    private String generateEmailTemplate(String title, String mainContent) {
        final String FOOTER_CONTENT = "© 2024 EasyCheck. All rights reserved.<br>" +
                "서울특별시 강남구 테헤란로 123, EasyStay 타워";

        return "<!DOCTYPE html>" +
                "<html lang=\"ko\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>" + title + "</title>" +
                "</head>" +
                "<body style=\"font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif; margin: 0; padding: 0; background-color: #f4f4f4;\">" +
                "<table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">" +
                "<tr>" +
                "<td align=\"center\" style=\"padding: 20px 0;\">" +
                "<table role=\"presentation\" style=\"width: 100%; max-width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);\">" +
                "<tr>" +
                "<td style=\"padding: 20px 20px 10px 20px;\">" +
                "<img src=\"https://beyond-easycheck.s3.us-east-1.amazonaws.com/logos/email-logo.png\" alt=\"EasyCheck\" style=\"max-width: 100%; height: auto; border-radius: 8px;\">" +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"padding: 10px 20px;\">" +
                mainContent +
                "</td>" +
                "</tr>" +
                "</table>" +
                "<table role=\"presentation\" style=\"width: 100%; max-width: 600px; border-collapse: collapse;\">" +
                "<tr>" +
                "<td style=\"padding: 20px 0; text-align: center;\">" +
                "<p style=\"color: #999999; font-size: 12px; margin: 0; line-height: 1.4;\">" +
                FOOTER_CONTENT +
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
