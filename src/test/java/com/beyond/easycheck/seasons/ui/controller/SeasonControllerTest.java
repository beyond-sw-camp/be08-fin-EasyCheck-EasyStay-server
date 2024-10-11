package com.beyond.easycheck.seasons.ui.controller;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.seasons.application.service.SeasonService;
import com.beyond.easycheck.seasons.exception.SeasonMessageType;
import com.beyond.easycheck.seasons.infrastructure.entity.SeasonEntity;
import com.beyond.easycheck.seasons.infrastructure.repository.SeasonRepository;
import com.beyond.easycheck.seasons.ui.requestbody.SeasonCreateRequest;
import com.beyond.easycheck.seasons.ui.requestbody.SeasonUpdateRequest;
import com.beyond.easycheck.seasons.ui.view.SeasonView;
import com.beyond.easycheck.user.application.mock.WithEasyCheckMockUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SeasonControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    SeasonService seasonService;

    @MockBean
    private SeasonRepository seasonRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("시즌 생성 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createSeason_success() throws Exception {
        // Given
        SeasonEntity season = new SeasonEntity(
                2L,
                "여름",
                "따뜻한 여름철, 해수욕과 바캉스를 즐길 수 있는 시즌입니다.",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        SeasonCreateRequest seasonCreateRequest = new SeasonCreateRequest(
                "여름",
                "따뜻한 여름철, 해수욕과 바캉스를 즐길 수 있는 시즌입니다.",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        // When
        ResultActions perform = mockMvc.perform(
                post("/api/v1/seasons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seasonCreateRequest))
        );

        // Then
        perform.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("시즌 생성 실패 - 잘못된 입력값")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createSeason_fail() throws Exception {
        // Given
        SeasonCreateRequest seasonCreateRequest = new SeasonCreateRequest(
                null,
                "따뜻한 여름철, 해수욕과 바캉스를 즐길 수 있는 시즌입니다.",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        // When
        ResultActions perform = mockMvc.perform(post("/api/v1/seasons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seasonCreateRequest)));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorType").value(SeasonMessageType.ARGUMENT_NOT_VALID.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(SeasonMessageType.ARGUMENT_NOT_VALID.getMessage()));

    }

    @Test
    @DisplayName("시즌 단일 조회 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readSeason_success() throws Exception {
        // Given
        Long id = 2L;
        SeasonView seasonView = new SeasonView(
                2L,
                "여름",
                "따뜻한 여름철, 해수욕과 바캉스를 즐길 수 있는 시즌입니다.",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        when(seasonService.readSeason(id)).thenReturn(seasonView);

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/seasons/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @DisplayName("시즌 단일 조회 실패 - 존재하지 않는 seasonId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readSeason_fail() throws Exception {
        // Given
        Long id = 999L;
        when(seasonService.readSeason(id)).thenThrow(new EasyCheckException(SeasonMessageType.SEASON_NOT_FOUND));

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/seasons/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(SeasonMessageType.SEASON_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(SeasonMessageType.SEASON_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("시즌 전체 조회 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readSeasons_success() throws Exception {
        // Given
        SeasonView seasonView1 = new SeasonView(
                2L,
                "여름",
                "따뜻한 여름철, 해수욕과 바캉스를 즐길 수 있는 시즌입니다.",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        SeasonView seasonView2 = new SeasonView(
                3L,
                "가을",
                "선선한 가을, 단풍놀이와 추수의 계절입니다.",
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 11, 30)
        );

        List<SeasonView> seasonViews = Arrays.asList(seasonView1, seasonView2);

        when(seasonService.readSeasons()).thenReturn(seasonViews);

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/seasons")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    @DisplayName("시즌 전체 조회 실패 - 빈 시즌")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readSeasons_fail() throws Exception {
        // Given
        when(seasonService.readSeasons()).thenReturn(Collections.emptyList());

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/seasons")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("시즌 수정 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateSeason_success() throws Exception {
        // Given
        Long seasonId = 1L;
        SeasonEntity season = new SeasonEntity(
                seasonId,
                "여름",
                "시원한 바다",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));

        SeasonUpdateRequest seasonUpdateRequest = new SeasonUpdateRequest(
                "가을",
                "선선한 가을, 단풍놀이와 추수의 계절입니다",
                LocalDate.of(2024, 8, 1),
                LocalDate.of(2024, 11, 30)
        );

        doNothing().when(seasonService).updateSeason(eq(seasonId), any(SeasonUpdateRequest.class));

        // When
        ResultActions perform = mockMvc.perform(put("/api/v1/seasons/{id}", seasonId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seasonUpdateRequest)));

        // Then
        perform.andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("시즌 수정 실패 - 잘못된 seasonId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateSeason_fail_wrongId() throws Exception {
        // Given
        Long id = 999L;
        SeasonUpdateRequest seasonUpdateRequest = new SeasonUpdateRequest(
                "가을",
                "선선한 가을, 단풍놀이와 추수의 계절입니다.",
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 11, 30)
        );

        // Then
        ResultActions perform = mockMvc.perform(put("/api/v1/seasons/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seasonUpdateRequest)));

        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(SeasonMessageType.SEASON_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(SeasonMessageType.SEASON_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("시즌 수정 실패 - 잘못된 입력값")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateSeason_fail_wrongValue() throws Exception {
        // Given
        Long id = 1L;
        SeasonUpdateRequest seasonUpdateRequest = new SeasonUpdateRequest(
                null,
                "선선한 가을, 단풍놀이와 추수의 계절입니다.",
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 11, 30)
        );

        // Then
        ResultActions perform = mockMvc.perform(put("/api/v1/seasons/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seasonUpdateRequest)));

        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorType").value(SeasonMessageType.ARGUMENT_NOT_VALID.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(SeasonMessageType.ARGUMENT_NOT_VALID.getMessage()));
    }

    @Test
    @DisplayName("시즌 삭제 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void deleteSeason_success() throws Exception {
        // Given
        Long id = 1L;

        // When
        ResultActions perform = mockMvc.perform(delete("/api/v1/seasons/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("시즌 삭제 실패 - 존재하지 않는 seasonId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void deleteSeason_fail() throws Exception {
        // Given
        Long id = 999L;

        // When
        doThrow(new EasyCheckException(SeasonMessageType.SEASON_NOT_FOUND))
                .when(seasonService).deleteSeason(id);

        ResultActions perform = mockMvc.perform(delete("/api/v1/seasons/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(SeasonMessageType.SEASON_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(SeasonMessageType.SEASON_NOT_FOUND.getMessage()));
    }
}
