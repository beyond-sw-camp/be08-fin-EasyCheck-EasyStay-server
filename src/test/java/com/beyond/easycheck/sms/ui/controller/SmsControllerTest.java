package com.beyond.easycheck.sms.ui.controller;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.sms.exception.SmsMessageType;
import com.beyond.easycheck.sms.infrastructure.persistence.redis.entity.SmsVerificationCode;
import com.beyond.easycheck.sms.infrastructure.persistence.redis.entity.VerifiedPhone;
import com.beyond.easycheck.sms.infrastructure.persistence.redis.repository.SmsVerificationCodeRepository;
import com.beyond.easycheck.sms.infrastructure.persistence.redis.repository.SmsVerifiedPhoneRepository;
import com.beyond.easycheck.sms.ui.requestbody.SmsCodeVerifyRequest;
import com.beyond.easycheck.sms.ui.requestbody.SmsVerificationCodeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SmsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DefaultMessageService defaultMessageService;

    @MockBean
    private SmsVerificationCodeRepository smsVerificationCodeRepository;

    @MockBean
    private SmsVerifiedPhoneRepository smsVerifiedPhoneRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("SMS 인증 코드 요청 테스트")
    void getVerificationCodeTest() throws Exception {
        SmsVerificationCodeRequest request = new SmsVerificationCodeRequest("01087654321");
        when(defaultMessageService.sendOne(any(SingleMessageSendingRequest.class)))
                .thenReturn(mock(SingleMessageSentResponse.class));

        mockMvc.perform(post("/api/v1/sms/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(smsVerificationCodeRepository, times(1)).save(any(SmsVerificationCode.class));
        verify(defaultMessageService, times(1)).sendOne(any(SingleMessageSendingRequest.class));
    }

    @Test
    @DisplayName("SMS 인증 코드 확인 테스트 - 성공")
    void verifyCodeTest_Success() throws Exception {
        String phone = "01087654321";
        String code = "123456";
        SmsCodeVerifyRequest request = new SmsCodeVerifyRequest(phone, code);
        SmsVerificationCode smsVerificationCode = SmsVerificationCode.createSmsVerificationCode(phone, code, 300L);

        when(smsVerificationCodeRepository.findById(code)).thenReturn(Optional.of(smsVerificationCode));

        mockMvc.perform(post("/api/v1/sms/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(smsVerifiedPhoneRepository, times(1)).save(any(VerifiedPhone.class));
        verify(smsVerificationCodeRepository, times(1)).delete(smsVerificationCode);
    }

    @Test
    @DisplayName("SMS 인증 코드 확인 테스트 - 유효하지 않은 코드")
    void verifyCodeTest_InvalidCode() throws Exception {
        SmsCodeVerifyRequest request = new SmsCodeVerifyRequest("01087654321", "123456");

        when(smsVerificationCodeRepository.findById("123456")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/sms/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EasyCheckException))
                .andExpect(result -> assertEquals(SmsMessageType.INVALID_VERIFICATION_CODE.name(),
                        ((EasyCheckException) result.getResolvedException()).getType()));

        verify(smsVerifiedPhoneRepository, never()).save(any(VerifiedPhone.class));
        verify(smsVerificationCodeRepository, never()).delete(any(SmsVerificationCode.class));
    }

    @Test
    @DisplayName("SMS 인증 코드 확인 테스트 - 전화번호 불일치")
    void verifyCodeTest_PhoneMismatch() throws Exception {
        String phone = "01087654321";
        String code = "123456";
        SmsCodeVerifyRequest request = new SmsCodeVerifyRequest(phone, code);
        SmsVerificationCode smsVerificationCode = SmsVerificationCode.createSmsVerificationCode("01011112222", code, 300L);

        when(smsVerificationCodeRepository.findById(code)).thenReturn(Optional.of(smsVerificationCode));

        mockMvc.perform(post("/api/v1/sms/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EasyCheckException))
                .andExpect(result -> assertEquals(SmsMessageType.SMS_VERIFICATION_CODE_NOT_MATCHED.name(),
                        ((EasyCheckException) result.getResolvedException()).getType()));

        verify(smsVerifiedPhoneRepository, never()).save(any(VerifiedPhone.class));
        verify(smsVerificationCodeRepository, never()).delete(any(SmsVerificationCode.class));
    }
}