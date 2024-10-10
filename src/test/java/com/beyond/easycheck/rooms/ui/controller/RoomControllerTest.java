package com.beyond.easycheck.rooms.ui.controller;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.rooms.application.service.RoomService;
import com.beyond.easycheck.rooms.exception.RoomMessageType;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import com.beyond.easycheck.rooms.ui.requestbody.RoomCreateRequest;
import com.beyond.easycheck.rooms.ui.requestbody.RoomUpdateRequest;
import com.beyond.easycheck.rooms.ui.view.RoomView;
import com.beyond.easycheck.roomtypes.exception.RoomtypeMessageType;
import com.beyond.easycheck.roomtypes.infrastructure.entity.RoomtypeEntity;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public class RoomControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("객실 생성 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createRoom() throws Exception {
        // Given
        AccommodationEntity accommodationEntity = new AccommodationEntity(1L, "선셋 리조트", "123 해변로, 오션 시티", AccommodationType.RESORT);

        RoomtypeEntity roomtypeEntity = new RoomtypeEntity(1L, accommodationEntity, "디럭스", "한 명이 살기 좋은 방", 1);

        RoomCreateRequest roomCreateRequest = new RoomCreateRequest(1L, "402", "roomPic1", RoomStatus.예약가능, 10, 5);

        // When
        ResultActions perform = mockMvc.perform(
                post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomCreateRequest))
        );

        // Then
        perform.andExpect(status().isCreated());

    }

    @Test
    @DisplayName("객실 생성 실패 - 존재하지 않는 roomtypeID")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createRoom_fail() throws Exception {
        // Given
        Long roomtypeId = 999L;
        RoomCreateRequest roomCreateRequest = new RoomCreateRequest(roomtypeId, "402", "roomPic1", RoomStatus.예약가능, 10, 5);

        when(roomService.createRoom(any(RoomCreateRequest.class)))
                .thenThrow(new EasyCheckException(RoomtypeMessageType.ROOM_TYPE_NOT_FOUND));

        // When
        ResultActions perform = mockMvc.perform(post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomCreateRequest)));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(RoomtypeMessageType.ROOM_TYPE_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(RoomtypeMessageType.ROOM_TYPE_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("객실 단일 조회 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readRoom_success() throws Exception {
        // Given
        Long id = 1L;
        RoomView roomView = new RoomView(id, "402", "roomPic1", 10, 5, RoomStatus.예약가능, 1L, 1L, "디럭스", "한 명이 살기 좋은 방", 1);

        when(roomService.readRoom(id)).thenReturn(roomView);

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/rooms/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(1L))
                .andExpect(jsonPath("$.roomNumber").value("402"));

    }

    @Test
    @DisplayName("객실 단일 조회 실패 - 존재하지 않는 roomId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readRoom_fail() throws Exception {
        // Given
        Long id = 999L;
        when(roomService.readRoom(id)).thenThrow(new EasyCheckException(RoomMessageType.ROOM_NOT_FOUND));

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/rooms/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(RoomMessageType.ROOM_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(RoomMessageType.ROOM_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("객실 전체 조회 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readRooms_success() throws Exception {
        // Given
        RoomView roomView1 = new RoomView(1L, "402", "roomPic1", 10, 5, RoomStatus.예약가능, 1L, 1L, "디럭스", "한 명이 살기 좋은 방", 1);
        RoomView roomView2 = new RoomView(2L, "403", "roomPic2", 8, 3, RoomStatus.예약가능, 2L, 1L, "스탠다드", "두 명이 살기 좋은 방", 2);

        List<RoomView> roomViews = Arrays.asList(roomView1, roomView2);

        when(roomService.readRooms()).thenReturn(roomViews);

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].roomId").value(1L))
                .andExpect(jsonPath("$[1].roomId").value(2L));
    }

    @Test
    @DisplayName("객실 전체 조회 실패 - 빈 객실")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readRooms_fail() throws Exception {
        // Given
        when(roomService.readRooms()).thenReturn(Collections.emptyList());

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("객실 수정 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateRoom_success() throws Exception {
        // Given
        Long roomId = 1L;
        RoomUpdateRequest roomUpdateRequest = new RoomUpdateRequest("501", "roomPic2", 8, RoomStatus.예약불가);

        // When
        ResultActions perform = mockMvc.perform(put("/api/v1/rooms/{id}", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomUpdateRequest)));

        // Then
        perform.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("객실 수정 실패 - 잘못된 roomtypeId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateRoom_fail() throws Exception {
        // Given
        Long roomtypeId = 999L;
        RoomUpdateRequest roomUpdateRequest = new RoomUpdateRequest("402", "roomPic2", -5, RoomStatus.예약불가);

        // Then
        ResultActions perform = mockMvc.perform(put("/api/v1/rooms/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomUpdateRequest)));

        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorType").value(RoomMessageType.ARGUMENT_NOT_VALID.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(RoomMessageType.ARGUMENT_NOT_VALID.getMessage()));
    }

    @Test
    @DisplayName("객실 삭제 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void deleteRoom_success() throws Exception {
        // Given
        Long roomId = 1L;

        // When
        ResultActions perform = mockMvc.perform(delete("/api/v1/rooms/{id}", roomId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("객실 삭제 실패 - 존재하지 않는 roomId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void deleteRoom_fail() throws Exception {
        // Given
        Long roomId = 999L;

        // When
        doThrow(new EasyCheckException(RoomMessageType.ROOM_NOT_FOUND))
                .when(roomService).deleteRoom(roomId);

        ResultActions perform = mockMvc.perform(delete("/api/v1/rooms/{id}", roomId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(RoomMessageType.ROOM_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(RoomMessageType.ROOM_NOT_FOUND.getMessage()));
    }
}
