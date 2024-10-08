package com.beyond.easycheck.notices.ui.controller;

import com.beyond.easycheck.accomodations.exception.AccommodationMessageType;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.notices.application.service.NoticesService;
import com.beyond.easycheck.notices.exception.NoticesMessageType;
import com.beyond.easycheck.notices.infrastructure.persistence.entity.NoticesEntity;
import com.beyond.easycheck.notices.ui.requestbody.NoticesCreateRequest;
import com.beyond.easycheck.notices.ui.requestbody.NoticesUpdateRequest;
import com.beyond.easycheck.notices.ui.view.NoticesView;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoticesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    NoticesService noticesService;

    @Test
    @Transactional
    @DisplayName("[공지사항 등록] - 성공")
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    void createNotices() throws Exception {
        // given
        NoticesCreateRequest request = new NoticesCreateRequest(1L,"공지사항","공지사항 내용");

        // when
        ResultActions perform = mockMvc.perform(
                post("/api/v1/notices-reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        // then
        perform.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("[공지사항 등록] - 숙소 찾기 실패")
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    void CreateNotices_fail_due_to_accommodation() throws Exception{
        // given
        NoticesCreateRequest request = new NoticesCreateRequest(9999L,"공지사항","공지사항 내용");

        // when
        ResultActions perform = mockMvc.perform(
                post("/api/v1/notices-reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
         );

        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(AccommodationMessageType.ACCOMMODATION_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(AccommodationMessageType.ACCOMMODATION_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("[공지사항 등록] - 관리자 찾기 실패")
    @WithEasyCheckMockUser(id = 999L, role = "ADMIN")
    void CreateNotices_fail_due_to_user() throws Exception{
        // given
        NoticesCreateRequest request = new NoticesCreateRequest(1L,"공지사항","공지사항 내용");

        // when
        ResultActions perform = mockMvc.perform(
                post("/api/v1/notices-reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(UserMessageType.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(UserMessageType.USER_NOT_FOUND.getMessage()));


    }

    @Test
    @Transactional
    @DisplayName("[공지사항 목록 조회] - 성공")
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    void getAllNotices() throws Exception {
        // given
        NoticesCreateRequest request1 = new NoticesCreateRequest(4L, "공지사항","공지사항 내용");
        NoticesCreateRequest request2 = new NoticesCreateRequest(4L, "공지사항 등록 중","공지사항 내용");

        noticesService.createNotices(4L, request1);
        noticesService.createNotices(4L, request2);

        // when
        ResultActions perform = mockMvc.perform(
                get("/api/v1/notices-reply")
                        .param("page","0")
                        .param("size","10")
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").value(request1.getTitle()))
                .andExpect(jsonPath("$[0].content").value(request1.getContent()));


    }

    @Test
    @DisplayName("[공지사항 목록 조회] - 빈 리스트")
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    void getAllNotices_emptyList() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(
                get("/api/v1/notices-reply")
                        .param("page","0")
                        .param("size","10")
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    @Transactional
    @DisplayName("[공지사항 조회] - 성공")
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    void getNotices() throws Exception {
        // given
        NoticesCreateRequest request = new NoticesCreateRequest(1L,"공지사항","공지사항 내용");
        Optional<NoticesEntity> createdNotice = noticesService.createNotices(4L, request);
        Assertions.assertThat(createdNotice).isPresent();

        Long noticeId = createdNotice.get().getId();

        // when
        ResultActions perform = mockMvc.perform(
                get("/api/v1/notices-reply/" + noticeId)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.content").value(request.getContent()));
    }

    @Test
    @DisplayName("[공지사항 조회] - 공지사항 찾기 실패")
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    void getNotices_fail() throws Exception {
        // given
        Long invalidId = 9999L;

        // when
        ResultActions perform = mockMvc.perform(
                get("/api/v1/notices-reply/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(NoticesMessageType.NOTICES_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(NoticesMessageType.NOTICES_NOT_FOUND.getMessage()));
    }

    @Test
    @Transactional
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    @DisplayName("[공지사항 수정] - 성공")
    void updateNotices() throws Exception {
        // given
        NoticesCreateRequest request = new NoticesCreateRequest(1L,"공지사항","공지사항 내용");

        Optional<NoticesEntity> notices = noticesService.createNotices(4L, request);


        Long noticeId = notices.get().getId(); // 생성한 공지사항의 ID
        System.out.println(noticeId);

        NoticesUpdateRequest updateRequest = new NoticesUpdateRequest("수정된 공지사항 제목", "수정한 내용");

        // when
        mockMvc.perform(
                put("/api/v1/notices-reply/" + noticeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                        .andExpect(status().isNoContent());



        // then
       mockMvc.perform(get("/api/v1/notices-reply/" + noticeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateRequest.getTitle()))
                .andExpect(jsonPath("$.content").value(updateRequest.getContent()));
    }

    @Test
    @DisplayName("[공지사항 수정] - 공지사항 찾기 실패")
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    void updateNotices_fail() throws Exception {

        // given
        Long invalidId = 9999L;
        NoticesUpdateRequest updateRequest = new NoticesUpdateRequest("수정된 공지사항","수정된 내용");

        // when
        ResultActions perform = mockMvc.perform(
                put("/api/v1/notices-reply/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
        );

        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(NoticesMessageType.NOTICES_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(NoticesMessageType.NOTICES_NOT_FOUND.getMessage()));
    }

    @Test
    @Transactional
    @DisplayName("[공지사항 삭제] - 성공")
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    void deleteNotices() throws Exception {
        // given
        NoticesCreateRequest request = new NoticesCreateRequest(1L,"공지사항","공지사항 내용");

        Optional<NoticesEntity> notices = noticesService.createNotices(4L, request);


        Long noticeId = notices.get().getId(); // 생성한 공지사항의 ID
        System.out.println(noticeId);

        // when: 공지사항 삭제
        mockMvc.perform(delete("/api/v1/notices-reply/" + noticeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // 성공적인 삭제 확인 (204 No Content)

        // then: 삭제된 공지사항 조회 시 예외 발생 확인
        mockMvc.perform(get("/api/v1/notices-reply/" + noticeId)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound()) // 공지사항이 삭제되었으므로 404 반환 확인
                    .andExpect(jsonPath("$.errors[0].errorType").value(NoticesMessageType.NOTICES_NOT_FOUND.name()))
                    .andExpect(jsonPath("$.errors[0].errorMessage").value(NoticesMessageType.NOTICES_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("[공지사항 삭제] - 공지사항 찾기 실패")
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    void deleteNotices_fail() throws Exception {
        // given
        Long invalidId = 9999L;

        // when & then
        ResultActions perform = mockMvc.perform(delete("/api/v1/notices-reply/" + invalidId)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(NoticesMessageType.NOTICES_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(NoticesMessageType.NOTICES_NOT_FOUND.getMessage()));
    }
}