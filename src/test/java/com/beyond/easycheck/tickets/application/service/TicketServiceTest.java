package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkRepository;
import com.beyond.easycheck.tickets.application.service.TicketOperationUseCase.TicketCreateCommand;
import com.beyond.easycheck.tickets.application.service.TicketOperationUseCase.TicketUpdateCommand;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketEntity;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.beyond.easycheck.themeparks.exception.ThemeParkMessageType.THEME_PARK_NOT_FOUND;
import static com.beyond.easycheck.tickets.exception.TicketMessageType.TICKET_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ThemeParkRepository themeParkRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 티켓 생성 테스트
    @Test
    void shouldCreateTicketSuccessfully() {
        // Given
        TicketCreateCommand command = TicketCreateCommand.builder()
                .themeParkId(1L)
                .ticketName("테스트 티켓")
                .price(BigDecimal.valueOf(50000))
                .saleStartDate(LocalDateTime.now())
                .saleEndDate(LocalDateTime.now().plusDays(10))
                .validFromDate(LocalDateTime.now())
                .validToDate(LocalDateTime.now().plusDays(30))
                .build();

        ThemeParkEntity themeParkEntity = new ThemeParkEntity();
        when(themeParkRepository.findById(1L)).thenReturn(Optional.of(themeParkEntity));
        when(ticketRepository.save(any(TicketEntity.class))).thenReturn(mock(TicketEntity.class));

        // When
        TicketEntity result = ticketService.createTicket(command);

        // Then
        assertNotNull(result);
        verify(ticketRepository, times(1)).save(any(TicketEntity.class));
    }

    // 티켓 수정 테스트
    @Test
    void shouldUpdateTicketSuccessfully() {
        // Given
        TicketUpdateCommand command = TicketUpdateCommand.builder()
                .ticketName("업데이트 티켓")
                .price(BigDecimal.valueOf(60000))
                .saleStartDate(LocalDateTime.now())
                .saleEndDate(LocalDateTime.now().plusDays(20))
                .validFromDate(LocalDateTime.now())
                .validToDate(LocalDateTime.now().plusDays(40))
                .build();

        // ThemeParkEntity의 ID 설정
        ThemeParkEntity themeParkEntity = new ThemeParkEntity();
        ReflectionTestUtils.setField(themeParkEntity, "id", 1L);

        // TicketEntity 생성 및 ID 설정
        TicketEntity ticketEntity = TicketEntity.createTicket(
                TicketCreateCommand.builder()
                        .themeParkId(1L)
                        .ticketName("테스트 티켓")
                        .price(BigDecimal.valueOf(50000))
                        .saleStartDate(LocalDateTime.now())
                        .saleEndDate(LocalDateTime.now().plusDays(10))
                        .validFromDate(LocalDateTime.now())
                        .validToDate(LocalDateTime.now().plusDays(30))
                        .build(),
                themeParkEntity
        );
        ReflectionTestUtils.setField(ticketEntity, "id", 1L); // TicketEntity의 ID 설정

        when(themeParkRepository.findById(1L)).thenReturn(Optional.of(themeParkEntity));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketEntity));

        // When
        ticketService.updateTicket(1L, 1L, command);

        // Then
        verify(ticketRepository, times(1)).save(any(TicketEntity.class));

        // 추가적으로 ticketEntity의 상태가 업데이트되었는지 확인
        assertEquals("업데이트 티켓", ticketEntity.getTicketName());
        assertEquals(BigDecimal.valueOf(60000), ticketEntity.getPrice());
    }

    // 티켓 삭제 테스트
    @Test
    void shouldDeleteTicketSuccessfully() {
        // Given
        ThemeParkEntity themeParkEntity = new ThemeParkEntity();
        ReflectionTestUtils.setField(themeParkEntity, "id", 1L); // ThemeParkEntity의 ID 설정

        TicketEntity ticketEntity = TicketEntity.createTicket(
                TicketCreateCommand.builder()
                        .themeParkId(1L)
                        .ticketName("테스트 티켓")
                        .price(BigDecimal.valueOf(50000))
                        .saleStartDate(LocalDateTime.now())
                        .saleEndDate(LocalDateTime.now().plusDays(10))
                        .validFromDate(LocalDateTime.now())
                        .validToDate(LocalDateTime.now().plusDays(30))
                        .build(),
                themeParkEntity
        );
        ReflectionTestUtils.setField(ticketEntity, "id", 1L); // TicketEntity의 ID 설정

        when(themeParkRepository.findById(1L)).thenReturn(Optional.of(themeParkEntity));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketEntity));

        // When
        ticketService.deleteTicket(1L, 1L);

        // Then
        verify(ticketRepository, times(1)).delete(ticketEntity);
    }

    // 테마파크를 찾을 수 없을 때 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenThemeParkNotFound() {
        // Given
        when(themeParkRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketService.createTicket(mock(TicketCreateCommand.class)));
        assertEquals(THEME_PARK_NOT_FOUND.getMessage(), exception.getMessage());
    }

    // 티켓을 찾을 수 없을 때 예외 처리 테스트 (삭제)
    @Test
    void shouldThrowExceptionWhenTicketNotFoundOnDelete() {
        // Given
        ThemeParkEntity themeParkEntity = new ThemeParkEntity();
        when(themeParkRepository.findById(1L)).thenReturn(Optional.of(themeParkEntity));
        when(ticketRepository.existsById(1L)).thenReturn(false);

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketService.deleteTicket(1L, 1L));
        assertEquals(TICKET_NOT_FOUND.getMessage(), exception.getMessage());
    }

    // 티켓을 찾을 수 없을 때 예외 처리 테스트 (수정)
    @Test
    void shouldThrowExceptionWhenTicketNotFoundOnUpdate() {
        // Given
        ThemeParkEntity themeParkEntity = new ThemeParkEntity();
        when(themeParkRepository.findById(1L)).thenReturn(Optional.of(themeParkEntity));
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketService.updateTicket(1L, 1L, mock(TicketUpdateCommand.class)));
        assertEquals(TICKET_NOT_FOUND.getMessage(), exception.getMessage());
    }
}
