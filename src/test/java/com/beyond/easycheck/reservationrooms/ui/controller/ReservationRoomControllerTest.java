package com.beyond.easycheck.reservationrooms.ui.controller;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.reservationrooms.application.service.ReservationRoomService;
import com.beyond.easycheck.reservationrooms.exception.ReservationRoomMessageType;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.PaymentStatus;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationStatus;
import com.beyond.easycheck.reservationrooms.ui.requestbody.ReservationRoomCreateRequest;
import com.beyond.easycheck.reservationrooms.ui.requestbody.ReservationRoomUpdateRequest;
import com.beyond.easycheck.reservationrooms.ui.view.ReservationRoomView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReservationRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationRoomService reservationRoomService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReservationRoomEntity reservationRoomEntity;
    private ReservationRoomCreateRequest reservationRoomCreateRequest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        reservationRoomEntity = new ReservationRoomEntity();
        reservationRoomCreateRequest = new ReservationRoomCreateRequest();
    }

    @Test
    @WithMockUser(roles = "USER")
    void createReservation_Success() throws Exception {

        // given
        reservationRoomCreateRequest.setRoom(1L, LocalDateTime.now(), LocalDate.of(2024, 10, 11), LocalDate.of(2024, 10, 13), ReservationStatus.RESERVATION, 10000, PaymentStatus.PAID);

        // when
        when(reservationRoomService.createReservation(any(Long.class), any(ReservationRoomCreateRequest.class)))
                .thenReturn(reservationRoomEntity);

        String requestJson = objectMapper.writeValueAsString(reservationRoomCreateRequest);

        // perform
        mockMvc.perform(post("/api/v1/reservation-room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createReservation_Failure_InvalidInput() throws Exception {

        // given
        ReservationRoomCreateRequest request = new ReservationRoomCreateRequest(
                null,
                LocalDateTime.now(),
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 3),
                null,
                10000,
                null
        );

        // when / then
        mockMvc.perform(post("/api/v1/reservation-room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllReservations_Success() throws Exception {

        // given
        ReservationRoomView mockReservationRoomView = new ReservationRoomView(
                1L, "John Doe", 1L, "Deluxe", List.of(), null, LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 3), null, 10000, null);

        given(reservationRoomService.getAllReservations(0, 10)).willReturn(List.of(mockReservationRoomView));

        // when / then
        mockMvc.perform(get("/api/v1/reservation-room")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userName").value("John Doe"));

        verify(reservationRoomService).getAllReservations(0, 10);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getReservationById_Success() throws Exception {

        // given
        Long reservationId = 1L;
        ReservationRoomView mockReservationRoomView = new ReservationRoomView(
                1L, "John Doe", 1L, "Deluxe", List.of(), null, LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 3), null, 10000, null);

        given(reservationRoomService.getReservationById(reservationId)).willReturn(mockReservationRoomView);

        // when / then
        mockMvc.perform(get("/api/v1/reservation-room/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userName").value("John Doe"));

        verify(reservationRoomService).getReservationById(reservationId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getReservationById_Failure_NotFound() throws Exception {

        // given
        Long reservationId = 1L;

        doThrow(new EasyCheckException(ReservationRoomMessageType.RESERVATION_NOT_FOUND))
                .when(reservationRoomService).getReservationById(reservationId);

        // when / then
        mockMvc.perform(get("/api/v1/reservation-room/{id}", reservationId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void cancelReservation_Success() throws Exception {

        // given
        Long reservationId = 1L;
        ReservationRoomUpdateRequest updateRequest = new ReservationRoomUpdateRequest(null);

        // when
        doNothing().when(reservationRoomService).cancelReservation(eq(reservationId), any(ReservationRoomUpdateRequest.class));

        // perform
        mockMvc.perform(put("/api/v1/reservation-room/{id}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNoContent());

        // verify
        verify(reservationRoomService).cancelReservation(eq(reservationId), any(ReservationRoomUpdateRequest.class));
    }
}