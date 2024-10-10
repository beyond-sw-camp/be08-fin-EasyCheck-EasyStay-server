package com.beyond.easycheck.suggestion.ui.controller;

import com.beyond.easycheck.accomodations.exception.AccommodationMessageType;
import com.beyond.easycheck.notices.exception.NoticesMessageType;
import com.beyond.easycheck.suggestion.application.service.SuggestionService;
import com.beyond.easycheck.suggestion.exception.SuggestionMessageType;
import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.AgreementType;
import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.SuggestionEntity;
import com.beyond.easycheck.suggestion.ui.requestbody.SuggestionCreateRequest;
import com.beyond.easycheck.suggestion.ui.view.SuggestionView;
import com.beyond.easycheck.user.application.mock.WithEasyCheckMockUser;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class SuggestionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SuggestionService suggestionService;

    @Test
    @DisplayName("[건의사항 등록] - 성공")
    @WithEasyCheckMockUser(id = 1L, role = "USER")
    void createSuggestion() throws Exception {
        // given
        SuggestionCreateRequest request = new SuggestionCreateRequest(1L, "객실", "문의", "enjoy2573@gmail.com", "객실 관련 문의합니다.", "객실 사용 후 휴대폰을 분실하였습니다.", "", AgreementType.Agree);

        // when
        ResultActions perform = mockMvc.perform(
                post("/api/v1/suggestions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        // then
        perform.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("[건의사항 등록] - 숙소 찾기 실패")
    @WithEasyCheckMockUser(id = 1L, role = "USER")
    void createSuggestion_fail_due_to_accommodation() throws Exception {
        // given
        SuggestionCreateRequest request = new SuggestionCreateRequest(9999L, "객실", "문의", "enjoy2573@gmail.com", "객실 관련 문의합니다.", "객실 사용 후 휴대폰을 분실하였습니다.", "", AgreementType.Agree);

        // when
        ResultActions perform = mockMvc.perform(
                post("/api/v1/suggestions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(AccommodationMessageType.ACCOMMODATION_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(AccommodationMessageType.ACCOMMODATION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("[건의사항 등록] - 관리자 찾기 실패")
    @WithEasyCheckMockUser(id = 999L, role = "USER")
    void CreateNotices_fail_due_to_user() throws Exception{
        // given
        SuggestionCreateRequest request = new SuggestionCreateRequest(1L, "객실", "문의", "enjoy2573@gmail.com", "객실 관련 문의합니다.", "객실 사용 후 휴대폰을 분실하였습니다.", "", AgreementType.Agree);

        // when
        ResultActions perform = mockMvc.perform(
                post("/api/v1/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );
        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(UserMessageType.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(UserMessageType.USER_NOT_FOUND.getMessage()));

    }

    @Test
    void replySuggestion() {
    }

    @Test
    @DisplayName("[건의사항 목록 조회] - 성공")
    @WithEasyCheckMockUser(id = 1L, role = "USER")
    void getAllSuggestions() throws Exception {
        // given
        SuggestionCreateRequest request1 = new SuggestionCreateRequest(1L, "객실", "문의", "enjoy2573@gmail.com", "객실 관련 문의합니다.", "객실 사용 후 휴대폰을 분실하였습니다.", "", AgreementType.Agree);
        SuggestionCreateRequest request2 = new SuggestionCreateRequest(1L, "편의시설", "칭찬", "enjoy2573@gmail.com", " 편의시설을 칭찬합니다.", "편의시설이 너무 깨끗하고 좋습니다. 다음에 또 오고 싶어요.", "", AgreementType.Agree);

        suggestionService.createSuggestion(1L, request1);
        suggestionService.createSuggestion(1L, request2);

        // when
        ResultActions perform = mockMvc.perform(
                get("/api/v1/suggestions")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").value(request1.getTitle()))
                .andExpect(jsonPath("$[0].content").value(request1.getContent()));

    }

    @Test
    @DisplayName("[건의사항 목록 조회] - 빈 리스트")
    @WithEasyCheckMockUser(id = 1L, role = "USER")
    void getAllsuggestions_emptyList() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(
                get("/api/v1/suggestions")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("[건의사항 조회] - 성공")
    @WithEasyCheckMockUser(id = 1L, role = "USER")
    void getSuggestionById() throws Exception {
        // given
        SuggestionCreateRequest request = new SuggestionCreateRequest(1L, "객실", "문의", "enjoy2573@gmail.com", "객실 관련 문의합니다.", "객실 사용 후 휴대폰을 분실하였습니다.", "", AgreementType.Agree);
        Optional<SuggestionEntity> createsuggestion = suggestionService.createSuggestion(1L, request);
        Assertions.assertThat(createsuggestion).isPresent();

        Long suggestionId = createsuggestion.get().getId();

        // when
        ResultActions perform = mockMvc.perform(
                get("/api/v1/suggestions/" + suggestionId)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.content").value(request.getContent()));

    }

    @Test
    @DisplayName("[건의사항 조회] - 건읫사항 찾기 실패")
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    void getNotices_fail() throws Exception {
        // given
        Long invalidId = 9999L;

        // when
        ResultActions perform = mockMvc.perform(
                get("/api/v1/suggestions/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(SuggestionMessageType.SUGGESTION_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(SuggestionMessageType.SUGGESTION_NOT_FOUND.getMessage()));
    }
}