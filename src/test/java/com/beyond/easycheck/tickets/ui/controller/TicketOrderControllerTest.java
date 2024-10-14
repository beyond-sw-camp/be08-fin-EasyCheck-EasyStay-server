package com.beyond.easycheck.tickets.ui.controller;

import com.beyond.easycheck.tickets.application.service.TicketOrderOperationUseCase;
import com.beyond.easycheck.tickets.application.service.TicketOrderReadUseCase;
import com.beyond.easycheck.tickets.ui.requestbody.TicketOrderRequest;
import com.beyond.easycheck.tickets.ui.view.TicketOrderDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.beyond.easycheck.tickets.infrastructure.entity.CollectionAgreementType.Y;
import static com.beyond.easycheck.tickets.infrastructure.entity.ReceiptMethodType.EMAIL;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TicketOrderControllerTest {

    @MockBean
    private TicketOrderOperationUseCase ticketOrderOperationUseCase;

    @MockBean
    private TicketOrderReadUseCase ticketOrderReadUseCase;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, "password", List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );
    }

    @Test
    void createTicketOrder_shouldReturnCreated() throws Exception {
        Long userId = 1L;

        TicketOrderDTO mockTicketOrder = new TicketOrderDTO(
                1L, "Test Ticket", 2, BigDecimal.valueOf(1000),
                1L, LocalDateTime.now(), null);

        TicketOrderRequest request = new TicketOrderRequest(
                1L, 2, EMAIL, Y);

        when(ticketOrderOperationUseCase.createTicketOrder(eq(userId), any(TicketOrderRequest.class)))
                .thenReturn(mockTicketOrder);

        mockMvc.perform(post("/api/v1/tickets/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.ticketName").value("Test Ticket"))
                .andExpect(jsonPath("$.data.quantity").value(2))
                .andExpect(jsonPath("$.data.totalPrice").value(1000));

        verify(ticketOrderOperationUseCase).createTicketOrder(eq(userId), any(TicketOrderRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getOrder_shouldReturnOrder() throws Exception {
        TicketOrderDTO mockTicketOrder = new TicketOrderDTO(
                1L, "Test Ticket", 2, BigDecimal.valueOf(1000), 1L, LocalDateTime.now(), null);

        when(ticketOrderReadUseCase.getTicketOrder(anyLong(), anyLong()))
                .thenReturn(mockTicketOrder);

        mockMvc.perform(get("/api/v1/tickets/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ticketName").value("Test Ticket"))
                .andExpect(jsonPath("$.data.quantity").value(2))
                .andExpect(jsonPath("$.data.totalPrice").value(1000));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getMyOrders_shouldReturnOrders() throws Exception {
        TicketOrderDTO mockTicketOrder = new TicketOrderDTO(
                1L, "Test Ticket", 2, BigDecimal.valueOf(1000), 1L, LocalDateTime.now(), null);

        when(ticketOrderReadUseCase.getAllOrdersByUserId(anyLong()))
                .thenReturn(List.of(mockTicketOrder));

        mockMvc.perform(get("/api/v1/tickets/orders/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].ticketName").value("Test Ticket"))
                .andExpect(jsonPath("$.data[0].quantity").value(2))
                .andExpect(jsonPath("$.data[0].totalPrice").value(1000));
    }

    @Test
    void cancelOrder_shouldReturnNoContent() throws Exception {
        mockMvc.perform(patch("/api/v1/tickets/orders/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void completeOrder_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/v1/tickets/orders/1/complete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}