package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase;
import com.beyond.easycheck.tickets.application.service.TicketOperationUseCase.TicketCreateCommand;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.tickets.infrastructure.entity.*;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketOrderRepository;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketRepository;
import com.beyond.easycheck.tickets.ui.requestbody.TicketOrderRequest;
import com.beyond.easycheck.tickets.ui.view.TicketOrderDTO;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import com.beyond.easycheck.tickets.exception.TicketOrderMessageType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType.HOTEL;
import static com.beyond.easycheck.tickets.exception.TicketMessageType.TICKET_SALE_PERIOD_INVALID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TicketOrderServiceTest {

    @Mock
    private TicketOrderRepository ticketOrderRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private TicketOrderService ticketOrderService;

    private UserEntity mockUser;
    private TicketEntity mockTicket;
    private ThemeParkEntity mockThemePark;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockUser = UserEntity.createGuestUser("Test User", "010-1234-5678");

        AccommodationEntity mockAccommodation = new AccommodationEntity(1L, "Accommodation Name", "Accommodation Address", HOTEL);

        ThemeParkOperationUseCase.ThemeParkCreateCommand command = ThemeParkOperationUseCase.ThemeParkCreateCommand.builder()
                .name("Test Theme Park")
                .description("Theme Park Description")
                .location("Location")
                .build();
        mockThemePark = ThemeParkEntity.createThemePark(command, mockAccommodation);

        TicketCreateCommand ticketCommand = new TicketCreateCommand(1L, "Test Ticket", BigDecimal.valueOf(1000),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7));
        mockTicket = TicketEntity.createTicket(ticketCommand, mockThemePark);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createTicketOrder_success() {
        TicketOrderRequest request = new TicketOrderRequest(1L, 2, ReceiptMethodType.EMAIL, CollectionAgreementType.Y);

        when(userJpaRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(mockTicket));

        TicketOrderDTO result = ticketOrderService.createTicketOrder(1L, request);

        assertNotNull(result);
        assertEquals("Test Ticket", result.getTicketName());
        verify(ticketOrderRepository, times(1)).save(any(TicketOrderEntity.class));
    }

    @Test
    void createTicketOrder_ticketSalePeriodInvalid() {
        mockTicket = TicketEntity.createTicket(
                new TicketCreateCommand(1L, "Test Ticket", BigDecimal.valueOf(1000),
                        LocalDateTime.now().minusDays(10),
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(7)),
                mockThemePark
        );

        TicketOrderRequest request = new TicketOrderRequest(1L, 2, ReceiptMethodType.EMAIL, CollectionAgreementType.Y);

        when(userJpaRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(mockTicket));

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketOrderService.createTicketOrder(1L, request));

        assertEquals(TICKET_SALE_PERIOD_INVALID.getMessage(), exception.getMessage());
    }

    @Test
    void cancelTicketOrder_success() {
        mockUser = spy(UserEntity.createGuestUser("Test User", "010-1234-5678"));
        doReturn(1L).when(mockUser).getId();

        TicketOrderEntity mockOrder = new TicketOrderEntity(mockTicket, 2, mockUser,
                ReceiptMethodType.EMAIL, CollectionAgreementType.Y);
        mockOrder.cancelOrder();

        when(ticketOrderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        ticketOrderService.cancelTicketOrder(1L, 1L);

        assertEquals(OrderStatus.CANCELLED, mockOrder.getOrderStatus());
        verify(ticketOrderRepository, times(1)).save(mockOrder);
    }

    @Test
    void cancelTicketOrder_unauthorizedAccess() {
        UserEntity anotherUser = UserEntity.createGuestUser("Another User", "010-1234-5678");

        anotherUser = spy(anotherUser);
        doReturn(2L).when(anotherUser).getId();

        TicketOrderEntity mockOrder = new TicketOrderEntity(mockTicket, 2, anotherUser,
                ReceiptMethodType.EMAIL, CollectionAgreementType.Y);

        TicketOrderEntity spyOrder = spy(mockOrder);
        doReturn(1L).when(spyOrder).getId();

        when(ticketOrderRepository.findById(anyLong())).thenReturn(Optional.of(spyOrder));

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketOrderService.cancelTicketOrder(1L, spyOrder.getId()));

        assertEquals(TicketOrderMessageType.UNAUTHORIZED_ACCESS.getMessage(), exception.getMessage());
    }

    @Test
    void getOrder_orderNotFound() {
        when(ticketOrderRepository.findById(anyLong())).thenReturn(Optional.empty());

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketOrderService.getTicketOrder(1L, 999L));

        assertEquals(TicketOrderMessageType.ORDER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void completeOrder_invalidOrderStatus() {
        mockUser = spy(mockUser);
        doReturn(1L).when(mockUser).getId();

        TicketOrderEntity mockOrder = mock(TicketOrderEntity.class);
        doReturn(1L).when(mockOrder).getId();
        doReturn(mockUser).when(mockOrder).getUserEntity();
        doReturn(OrderStatus.CANCELLED).when(mockOrder).getOrderStatus();

        when(ticketOrderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketOrderService.completeOrder(1L, 1L));

        assertEquals(TicketOrderMessageType.INVALID_ORDER_STATUS_FOR_COMPLETION.getMessage(), exception.getMessage());
    }

    @Test
    void completeOrder_orderAlreadyCompleted() {
        mockUser = spy(mockUser);
        doReturn(1L).when(mockUser).getId();

        TicketOrderEntity mockOrder = new TicketOrderEntity(mockTicket, 2, mockUser,
                ReceiptMethodType.EMAIL, CollectionAgreementType.Y);
        mockOrder.completeOrder();
        TicketOrderEntity spyOrder = spy(mockOrder);
        doReturn(1L).when(spyOrder).getId();

        when(ticketOrderRepository.findById(anyLong())).thenReturn(Optional.of(spyOrder));

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketOrderService.completeOrder(1L, spyOrder.getId()));

        assertEquals(TicketOrderMessageType.ORDER_ALREADY_COMPLETED.getMessage(), exception.getMessage());
    }
}
