package com.beyond.easycheck.tickets.ui.controller;

import com.beyond.easycheck.tickets.application.service.TicketOperationUseCase;
import com.beyond.easycheck.tickets.application.service.TicketReadUseCase;
import com.beyond.easycheck.tickets.application.service.TicketReadUseCase.FindTicketResult;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketEntity;
import com.beyond.easycheck.tickets.ui.requestbody.TicketRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketOperationUseCase ticketOperationUseCase;

    @MockBean
    private TicketReadUseCase ticketReadUseCase;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // 자원을 해제
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCreateTicketSuccessfully() throws Exception {
        // Given
        TicketRequest request = new TicketRequest("테스트 티켓", BigDecimal.valueOf(50000),
                LocalDateTime.now(), LocalDateTime.now().plusDays(10),
                LocalDateTime.now(), LocalDateTime.now().plusDays(30));

        TicketEntity ticketEntity = new TicketEntity();
        when(ticketOperationUseCase.createTicket(any())).thenReturn(ticketEntity);

        // When & Then
        mockMvc.perform(post("/api/v1/parks/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldUpdateTicketSuccessfully() throws Exception {
        // Given
        TicketRequest request = new TicketRequest("업데이트 티켓", BigDecimal.valueOf(60000),
                LocalDateTime.now(), LocalDateTime.now().plusDays(20),
                LocalDateTime.now(), LocalDateTime.now().plusDays(40));

        TicketEntity updatedTicket = new TicketEntity();
        when(ticketOperationUseCase.updateTicket(eq(1L), eq(1L), any())).thenReturn(updatedTicket);

        // When & Then
        mockMvc.perform(put("/api/v1/parks/1/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldDeleteTicketSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/parks/1/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldGetAllTicketsByThemePark() throws Exception {
        // Given
        FindTicketResult ticketResult = FindTicketResult.builder().build();
        when(ticketReadUseCase.getTicketsByThemePark(1L)).thenReturn(List.of(ticketResult));

        // When & Then
        mockMvc.perform(get("/api/v1/parks/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"user"})
    void shouldGetTicketsByThemeParkOnSale() throws Exception {
        // Given
        FindTicketResult ticketResult = FindTicketResult.builder().build();
        when(ticketReadUseCase.getTicketsByThemeParkOnSale(1L)).thenReturn(List.of(ticketResult));

        // When & Then
        mockMvc.perform(get("/api/v1/parks/1/tickets/on-sale")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"user"})
    void shouldGetTicketById() throws Exception {
        // Given
        FindTicketResult ticketResult = FindTicketResult.builder().build();
        when(ticketReadUseCase.getTicketById(1L)).thenReturn(ticketResult);

        // When & Then
        mockMvc.perform(get("/api/v1/parks/1/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }
}
