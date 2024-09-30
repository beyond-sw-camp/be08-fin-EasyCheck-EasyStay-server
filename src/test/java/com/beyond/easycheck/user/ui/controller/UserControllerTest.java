package com.beyond.easycheck.user.ui.controller;

import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerifiedEmailEntity;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerifiedEmailRepository;
import com.beyond.easycheck.user.ui.requestbody.UserRegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    VerifiedEmailRepository verifiedEmailRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("[회원가입 요청] - 성공 ")
    void registerUser() throws Exception {
        // given

        final String verifiedEmail = "cloudyong3620@gmail.com";
        verifiedEmailRepository.save(VerifiedEmailEntity.createVerifiedEmail(verifiedEmail));

        UserRegisterRequest request = new UserRegisterRequest(
                verifiedEmail,
                "qwer1234!",
                "hello",
                "010-1111-2222",
                "서울시",
                "동작구",
                'Y'
        );
        // when
        ResultActions perform_200 = mockMvc.perform(
                post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        perform_200.andExpect(status().isCreated());
    }

}