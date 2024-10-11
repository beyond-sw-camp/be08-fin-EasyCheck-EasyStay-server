package com.beyond.easycheck.roomrates.ui.controller;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.roomrates.application.service.RoomrateService;
import com.beyond.easycheck.roomrates.infrastructure.entity.RoomrateType;
import com.beyond.easycheck.roomrates.ui.requestbody.RoomrateCreateRequest;
import com.beyond.easycheck.roomrates.ui.requestbody.RoomrateUpdateRequest;
import com.beyond.easycheck.roomrates.ui.view.RoomrateView;
import com.beyond.easycheck.rooms.application.service.RoomService;
import com.beyond.easycheck.rooms.exception.RoomMessageType;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import com.beyond.easycheck.rooms.ui.requestbody.RoomCreateRequest;
import com.beyond.easycheck.rooms.ui.requestbody.RoomUpdateRequest;
import com.beyond.easycheck.rooms.ui.view.RoomView;
import com.beyond.easycheck.roomtypes.exception.RoomtypeMessageType;
import com.beyond.easycheck.roomtypes.infrastructure.entity.RoomtypeEntity;
import com.beyond.easycheck.seasons.infrastructure.entity.SeasonEntity;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.beyond.easycheck.roomrates.exception.RoomrateMessageType.ROOM_RATE_NOT_FOUND;
import static com.beyond.easycheck.rooms.exception.RoomMessageType.ROOM_NOT_FOUND;
import static com.beyond.easycheck.seasons.exception.SeasonMessageType.SEASON_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RoomratesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RoomrateService roomrateService;

    AccommodationEntity accommodationEntity;
    RoomEntity roomEntity;
    SeasonEntity seasonEntity;
    RoomtypeEntity roomtypeEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("객실 요금 생성 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createRoomrate() throws Exception {
        // Given
        RoomrateCreateRequest roomrateCreateRequest = new RoomrateCreateRequest(
                1L,
                1L,
                RoomrateType.주말,
                BigDecimal.valueOf(100000)
        );

        // When
        ResultActions perform = mockMvc.perform(post("/api/v1/roomrates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomrateCreateRequest))
        );

        // Then
        perform.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("객실 요금 생성 실패 - 존재하지 않는 roomID")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createRoomrates_fail() throws Exception {
        // Given
        Long roomId = 999L;
        RoomrateCreateRequest roomrateCreateRequest = new RoomrateCreateRequest(
                roomId,
                1L,
                RoomrateType.주말,
                BigDecimal.valueOf(100000)
        );

        doThrow(new EasyCheckException(ROOM_NOT_FOUND)).when(roomrateService).createRoomrate(any(RoomrateCreateRequest.class));

        // When
        ResultActions perform = mockMvc.perform(post("/api/v1/roomrates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomrateCreateRequest)));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(ROOM_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(ROOM_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("객실 요금 단일 조회 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readRoomrate_success() throws Exception {
        // Given
        Long id = 1L;
        RoomrateView roomrateView = new RoomrateView(
                1L,
                RoomrateType.주말,
                BigDecimal.valueOf(100000),
                RoomStatus.예약가능,
                "디럭스",
                "여름"
        );

        when(roomrateService.readRoomrate(id)).thenReturn(roomrateView);

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/roomrates/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

    }

    @Test
    @DisplayName("객실 요금 단일 조회 실패 - 존재하지 않는 roomrateId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readRoomrate_fail() throws Exception {
        // Given
        Long id = 999L;
        when(roomrateService.readRoomrate(id)).thenThrow(new EasyCheckException(ROOM_RATE_NOT_FOUND));

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/roomrates/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(ROOM_RATE_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(ROOM_RATE_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("객실 요금 전체 조회 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readRoomrates_success() throws Exception {
        // Given
        RoomrateView roomrate1 = new RoomrateView(
                1L,
                RoomrateType.주말,
                BigDecimal.valueOf(100000),
                RoomStatus.예약가능,
                "디럭스",
                "여름"
        );

        RoomrateView roomrate2 = new RoomrateView(
                2L,
                RoomrateType.평일,
                BigDecimal.valueOf(200000),
                RoomStatus.예약가능,
                "디럭스",
                "봄"
        );

        List<RoomrateView> roomrateViews = Arrays.asList(roomrate1, roomrate2);

        when(roomrateService.readRoomrates()).thenReturn(roomrateViews);

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/roomrates")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("객실 요금 전체 조회 실패 - 빈 객실")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readRoomrates_fail() throws Exception {
        // Given
        when(roomrateService.readRoomrates()).thenReturn(Collections.emptyList());

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/roomrates")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("객실 요금 수정 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateRoomrate_success() throws Exception {
        // Given
        Long id = 1L;
        RoomrateUpdateRequest roomrateUpdateRequest = new RoomrateUpdateRequest(
                1L,
                1L,
                RoomrateType.주말,
                BigDecimal.valueOf(100000)
        );

        // When
        ResultActions perform = mockMvc.perform(put("/api/v1/roomrates/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomrateUpdateRequest)));

        // Then
        perform.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("객실 요금 수정 실패 - 잘못된 roomId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateRoomrate_fail_wrongRoomId() throws Exception {
        // Given
        Long roomId = 999L;
        RoomrateUpdateRequest roomrateUpdateRequest = new RoomrateUpdateRequest(
                999L,
                1L,
                RoomrateType.주말,
                BigDecimal.valueOf(200000)
        );

        // Then
        ResultActions perform = mockMvc.perform(put("/api/v1/roomrates/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomrateUpdateRequest)));

        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(ROOM_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(ROOM_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("객실 요금 수정 실패 - 잘못된 seasonId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateRoomrate_fail_wrongSeasonId() throws Exception {
        // Given
        Long seasonId = 999L;
        RoomrateUpdateRequest roomrateUpdateRequest = new RoomrateUpdateRequest(
                1L,
                999L,
                RoomrateType.주말,
                BigDecimal.valueOf(200000)
        );

        // Then
        ResultActions perform = mockMvc.perform(put("/api/v1/roomrates/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomrateUpdateRequest)));

        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(SEASON_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(SEASON_NOT_FOUND.getMessage()));
    }

}
