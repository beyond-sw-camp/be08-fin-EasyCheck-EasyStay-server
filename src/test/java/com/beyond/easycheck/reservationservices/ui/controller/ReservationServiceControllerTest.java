package com.beyond.easycheck.reservationservices.ui.controller;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.reservationrooms.exception.ReservationRoomMessageType;
import com.beyond.easycheck.reservationservices.application.service.ReservationServiceService;
import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceStatus;
import com.beyond.easycheck.reservationservices.ui.requestbody.ReservationServiceCreateRequest;
import com.beyond.easycheck.reservationservices.ui.requestbody.ReservationServiceUpdateRequest;
import com.beyond.easycheck.reservationservices.ui.view.ReservationServiceView;
import com.beyond.easycheck.user.application.mock.WithEasyCheckMockUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithEasyCheckMockUser
class ReservationServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationServiceService reservationServiceService;

    @Test
    @DisplayName("부가 서비스 예약 생성 - 성공")
    void createReservationRoom_Success() throws Exception {
        ReservationServiceCreateRequest request = new ReservationServiceCreateRequest(1L, 2L, 2, 20000, ReservationServiceStatus.RESERVATION);

        mockMvc.perform(post("/api/v1/reservation-service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(reservationServiceService).createReservationRoom(any(ReservationServiceCreateRequest.class));
    }

    @Test
    @DisplayName("부가 서비스 예약 생성 - 실패 (잘못된 입력)")
    void createReservationRoom_BadRequest() throws Exception {
        ReservationServiceCreateRequest request = new ReservationServiceCreateRequest(null, null, 0, -1000, null);

        mockMvc.perform(post("/api/v1/reservation-service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(reservationServiceService, never()).createReservationRoom(any(ReservationServiceCreateRequest.class));
    }

    @Test
    @DisplayName("부가 서비스 예약 목록 조회 - 성공")
    void getAllReservationServices_Success() throws Exception {
        List<ReservationServiceView> reservationServices = Arrays.asList(
                new ReservationServiceView(1L, 1L, 2L, 2, 30000, ReservationServiceStatus.RESERVATION),
                new ReservationServiceView(2L, 1L, 3L, 3, 40000, ReservationServiceStatus.RESERVATION)
        );

        when(reservationServiceService.getAllReservationServices(eq(0), eq(10))).thenReturn(reservationServices);

        mockMvc.perform(get("/api/v1/reservation-service")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(reservationServiceService).getAllReservationServices(0, 10);
    }

    @Test
    @DisplayName("특정 부가 서비스 예약 조회 - 성공")
    void getReservationServiceById_Success() throws Exception {

        ReservationServiceView reservationService = new ReservationServiceView(1L, 1L, 2L, 2, 30000, ReservationServiceStatus.RESERVATION);

        when(reservationServiceService.getReservationServiceById(1L)).thenReturn(reservationService);

        mockMvc.perform(get("/api/v1/reservation-service/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(30000))
                .andExpect(jsonPath("$.reservationServiceStatus").value("RESERVATION"));

        verify(reservationServiceService).getReservationServiceById(1L);
    }

    @Test
    @DisplayName("특정 부가 서비스 예약 조회 - 실패 (없는 ID)")
    void getReservationServiceById_NotFound() throws Exception {
        when(reservationServiceService.getReservationServiceById(99L))
                .thenThrow(new EasyCheckException(ReservationRoomMessageType.RESERVATION_NOT_FOUND));

        mockMvc.perform(get("/api/v1/reservation-service/99"))
                .andExpect(status().isNotFound());

        verify(reservationServiceService).getReservationServiceById(99L);
    }

    @Test
    @DisplayName("부가 서비스 예약 취소 - 성공")
    void cancelReservationService_Success() throws Exception {
        ReservationServiceUpdateRequest request = new ReservationServiceUpdateRequest(ReservationServiceStatus.CANCELED);

        mockMvc.perform(put("/api/v1/reservation-service/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(reservationServiceService).cancelReservationService(eq(1L), any(ReservationServiceUpdateRequest.class));
    }

    @Test
    @DisplayName("부가 서비스 예약 취소 - 실패 (잘못된 입력)")
    void cancelReservationService_BadRequest() throws Exception {
        ReservationServiceUpdateRequest request = new ReservationServiceUpdateRequest(null);

        mockMvc.perform(put("/api/v1/reservation-service/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(reservationServiceService, never()).cancelReservationService(anyLong(), any(ReservationServiceUpdateRequest.class));
    }
}