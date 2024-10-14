package com.beyond.easycheck.mail.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerificationCodeEntity;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerifiedEmailEntity;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerificationCodeRepository;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerifiedEmailRepository;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.PaymentStatus;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationStatus;
import com.beyond.easycheck.reservationrooms.ui.view.ReservationRoomView;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.AgreementType;
import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.SuggestionEntity;
import com.beyond.easycheck.suggestion.infrastructure.persistence.repository.SuggestionsRepository;
import com.beyond.easycheck.suggestion.ui.requestbody.SuggestionReplyRequestBody;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private VerifiedEmailRepository verifiedEmailRepository;

    @Mock
    private SuggestionsRepository suggestionsRepository;

    @InjectMocks
    private MailServiceImpl mailService;

    @Test
    @DisplayName("이메일 인증 코드 전송 테스트")
    void sendVerificationCode() throws MessagingException {
        String email = "test@example.com";

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(mailSender).send(any(MimeMessage.class));
        when(verificationCodeRepository.save(any(VerificationCodeEntity.class))).thenReturn(mock(VerificationCodeEntity.class));

        assertDoesNotThrow(() -> mailService.sendVerificationCode(email));

        verify(mailSender).send(any(MimeMessage.class));
        verify(verificationCodeRepository).save(any(VerificationCodeEntity.class));
    }

    @Test
    @DisplayName("이메일 인증 테스트")
    void verifyEmail() {
        String code = "testCode";
        VerificationCodeEntity verificationCodeEntity = VerificationCodeEntity.createVerificationCode("test@example.com", code, 300L);

        when(verificationCodeRepository.findByCode(code)).thenReturn(Optional.of(verificationCodeEntity));
        when(verifiedEmailRepository.save(any(VerifiedEmailEntity.class))).thenReturn(mock(VerifiedEmailEntity.class));

        assertDoesNotThrow(() -> mailService.verifyEmail(code));

        verify(verificationCodeRepository).findByCode(code);
        verify(verifiedEmailRepository).save(any(VerifiedEmailEntity.class));
        verify(verificationCodeRepository).delete(verificationCodeEntity);
    }

    @Test
    @DisplayName("예약 확인 이메일 전송 테스트")
    void sendReservationConfirmationEmail() throws MessagingException {
        String email = "test@example.com";
        ReservationRoomView reservationDetails = new ReservationRoomView(
                1L, "Test User", 1L, "Deluxe Room",
                Arrays.asList("image1.jpg", "image2.jpg"),
                RoomStatus.예약가능,
                LocalDate.now(), LocalDate.now().plusDays(2),
                ReservationStatus.RESERVATION, 100000,
                PaymentStatus.PAID
        );

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> mailService.sendReservationConfirmationEmail(email, reservationDetails));

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("건의사항 답변 이메일 전송 테스트")
    void sendSuggestionReply() throws MessagingException {
        SuggestionReplyRequestBody requestBody = new SuggestionReplyRequestBody();
        requestBody.setSuggestionId(1L);
        requestBody.setReplyContent("Thank you for your suggestion");

        SuggestionEntity suggestionEntity = mock(SuggestionEntity.class);
        when(suggestionEntity.getId()).thenReturn(1L);
        when(suggestionEntity.getAccommodationEntity()).thenReturn(mock(AccommodationEntity.class));
        when(suggestionEntity.getAccommodationEntity().getName()).thenReturn("Test Hotel");
        when(suggestionEntity.getUserEntity()).thenReturn(mock(UserEntity.class));
        when(suggestionEntity.getUserEntity().getName()).thenReturn("Test User");
        when(suggestionEntity.getType()).thenReturn("Complaint");
        when(suggestionEntity.getSubject()).thenReturn("칭찬");
        when(suggestionEntity.getEmail()).thenReturn("test@example.com");
        when(suggestionEntity.getTitle()).thenReturn("Great service");
        when(suggestionEntity.getContent()).thenReturn("I had a wonderful experience");
        when(suggestionEntity.getAttachmentPath()).thenReturn("/path/to/attachment");
        when(suggestionEntity.getAgreementType()).thenReturn(AgreementType.Agree);

        when(suggestionsRepository.findById(1L)).thenReturn(Optional.of(suggestionEntity));
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> mailService.sendSuggestionReply(requestBody));

        verify(suggestionsRepository).findById(1L);
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("존재하지 않는 건의사항에 대한 답변 시도 테스트")
    void sendSuggestionReply_SuggestionNotFound() {
        SuggestionReplyRequestBody requestBody = new SuggestionReplyRequestBody(1L, "tew");

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        when(suggestionsRepository.findById(requestBody.getSuggestionId())).thenReturn(Optional.empty());

        assertThrows(EasyCheckException.class, () -> mailService.sendSuggestionReply(requestBody));

        verify(suggestionsRepository).findById(requestBody.getSuggestionId());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}