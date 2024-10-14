package com.beyond.easycheck.mail.ui.controller;

import com.beyond.easycheck.mail.application.service.MailService;
import com.beyond.easycheck.mail.ui.requestbody.VerificationCodeRequest;
import com.beyond.easycheck.mail.ui.requestbody.VerificationTargetEmailRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmailControllerTest {

    @Mock
    private MailService mailService;

    @InjectMocks
    private EmailController emailController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(emailController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("인증 코드 요청 테스트")
    void requestVerificationCodeTest() throws Exception {
        VerificationTargetEmailRequest request = new VerificationTargetEmailRequest("test@example.com");

        doNothing().when(mailService).sendVerificationCode(request.targetEmail());

        mockMvc.perform(patch("/api/v1/verification-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(mailService, times(1)).sendVerificationCode(request.targetEmail());
    }

    @Test
    @DisplayName("인증 코드 확인 테스트")
    void verifyCodeTest() throws Exception {
        VerificationCodeRequest request = new VerificationCodeRequest("123456");

        doNothing().when(mailService).verifyEmail(request.code());

        mockMvc.perform(patch("/api/v1/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(mailService, times(1)).verifyEmail(request.code());
    }
    @Test
    @DisplayName("유효하지 않은 인증 코드로 확인 시 실패 테스트")
    void verifyCodeWithInvalidCodeTest() throws Exception {
        VerificationCodeRequest request = new VerificationCodeRequest("");

        mockMvc.perform(patch("/api/v1/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(mailService, never()).verifyEmail(anyString());
    }
}