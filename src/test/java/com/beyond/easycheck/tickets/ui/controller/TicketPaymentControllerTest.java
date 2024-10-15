package com.beyond.easycheck.tickets.ui.controller;

import com.beyond.easycheck.common.ui.view.ApiResponseView;
import com.beyond.easycheck.tickets.application.service.TicketPaymentService;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketPaymentEntity;
import com.beyond.easycheck.tickets.ui.requestbody.TicketPaymentRequest;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TicketPaymentControllerTest {

    @MockBean
    private TicketPaymentService ticketPaymentService;

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
    void processPayment_shouldReturnCreated() throws Exception {
        Long orderId = 1L;
        TicketPaymentRequest request = new TicketPaymentRequest(orderId, "CARD", BigDecimal.valueOf(1000));
        TicketPaymentEntity paymentEntity = new TicketPaymentEntity();

        when(ticketPaymentService.processPayment(anyLong(), anyLong(), any(TicketPaymentRequest.class))).thenReturn(paymentEntity);

        mockMvc.perform(post("/api/v1/tickets/payment/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void cancelPayment_shouldReturnOk() throws Exception {
        Long orderId = 1L;
        TicketPaymentEntity paymentEntity = new TicketPaymentEntity();

        when(ticketPaymentService.cancelPayment(anyLong(), anyLong(), any(String.class))).thenReturn(paymentEntity);

        mockMvc.perform(patch("/api/v1/tickets/payment/" + orderId + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Cancellation reason\"")
                        .header("userId", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void refundPayment_shouldReturnOk() throws Exception {
        Long orderId = 1L;
        TicketPaymentEntity paymentEntity = new TicketPaymentEntity();

        when(ticketPaymentService.refundPayment(anyLong(), anyLong(), any(String.class))).thenReturn(paymentEntity);

        mockMvc.perform(patch("/api/v1/tickets/payment/" + orderId + "/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Refund reason\"")
                        .header("userId", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getPaymentStatus_shouldReturnOk() throws Exception {
        Long orderId = 1L;
        TicketPaymentEntity paymentEntity = new TicketPaymentEntity();

        when(ticketPaymentService.getPaymentStatus(anyLong())).thenReturn(paymentEntity);

        mockMvc.perform(get("/api/v1/tickets/payment/" + orderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getPaymentHistory_shouldReturnOk() throws Exception {
        List<TicketPaymentEntity> paymentHistory = Collections.singletonList(new TicketPaymentEntity());

        when(ticketPaymentService.getPaymentHistory(anyLong())).thenReturn(paymentHistory);

        mockMvc.perform(get("/api/v1/tickets/payment/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("userId", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void retryPayment_shouldReturnOk() throws Exception {
        Long orderId = 1L;
        TicketPaymentRequest request = new TicketPaymentRequest(orderId, "CARD", BigDecimal.valueOf(1000));
        TicketPaymentEntity paymentEntity = new TicketPaymentEntity();

        when(ticketPaymentService.retryPayment(anyLong(), anyLong(), any(TicketPaymentRequest.class))).thenReturn(paymentEntity);

        mockMvc.perform(patch("/api/v1/tickets/payment/" + orderId + "/retry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("userId", 1L))
                .andExpect(status().isOk());
    }
}