package com.beyond.easycheck.reservationrooms.ui.controller;

import com.beyond.easycheck.reservationrooms.application.service.ReservationRoomService;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.PaymentStatus;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationStatus;
import com.beyond.easycheck.reservationrooms.ui.requestbody.ReservationRoomCreateRequest;
import com.beyond.easycheck.reservationrooms.ui.requestbody.ReservationRoomUpdateRequest;
import com.beyond.easycheck.reservationrooms.ui.view.DayRoomAvailabilityView;
import com.beyond.easycheck.reservationrooms.ui.view.ReservationRoomView;
import com.beyond.easycheck.reservationrooms.ui.view.RoomAvailabilityView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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
    private ReservationRoomUpdateRequest reservationRoomUpdateRequest;

    @BeforeEach
    void setUp() {

        reservationRoomEntity = new ReservationRoomEntity();
        reservationRoomCreateRequest = new ReservationRoomCreateRequest();
        reservationRoomUpdateRequest = new ReservationRoomUpdateRequest();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateReservation() throws Exception {

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
    void testGetAvailableRooms() throws Exception {

        // when
        List<RoomAvailabilityView> availableRooms = Collections.singletonList(new RoomAvailabilityView(1L, "Deluxe", "101", 1, null));
        when(reservationRoomService.getAvailableRoomsByCheckInCheckOut(any(LocalDate.class), any(LocalDate.class))).thenReturn(availableRooms);

        // perform
        mockMvc.perform(get("/api/v1/reservation-room/available")
                        .param("checkinDate", "2024-10-11")
                        .param("checkoutDate", "2024-10-13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].roomNumber").value("101"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetRoomAvailabilityByMonth() throws Exception {

        // when
        List<DayRoomAvailabilityView> availability = Collections.singletonList(new DayRoomAvailabilityView(LocalDate.of(2024, 10, 11), "Monday", Collections.emptyList()));
        when(reservationRoomService.getAvailableRoomsByMonth(anyInt(), anyInt())).thenReturn(availability);

        // perform
        mockMvc.perform(get("/api/v1/reservation-room/room-list")
                        .param("year", "2024")
                        .param("month", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].date").value("2024-10-11"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllReservations() throws Exception {

        // when
        List<ReservationRoomView> reservations = Collections.singletonList(new ReservationRoomView());
        when(reservationRoomService.getAllReservations(anyInt(), anyInt())).thenReturn(reservations);

        // perform
        mockMvc.perform(get("/api/v1/reservation-room")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetReservationById() throws Exception {

        // when
        ReservationRoomView reservationRoomView = new ReservationRoomView();
        when(reservationRoomService.getReservationById(any(Long.class))).thenReturn(reservationRoomView);

        // perform
        mockMvc.perform(get("/api/v1/reservation-room/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCancelReservation() throws Exception {

        // when
        doNothing().when(reservationRoomService).cancelReservation(any(Long.class), any(ReservationRoomUpdateRequest.class));

        String requestJson = objectMapper.writeValueAsString(reservationRoomUpdateRequest);

        // perform
        mockMvc.perform(put("/api/v1/reservation-room/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNoContent());
    }
}