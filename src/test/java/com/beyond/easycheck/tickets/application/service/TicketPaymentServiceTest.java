package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.themeParks.application.service.ThemeParkOperationUseCase;
import com.beyond.easycheck.themeParks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.tickets.application.service.TicketOperationUseCase.TicketCreateCommand;
import com.beyond.easycheck.tickets.infrastructure.entity.*;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketOrderRepository;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketPaymentRepository;
import com.beyond.easycheck.tickets.ui.requestbody.TicketPaymentRequest;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType.HOTEL;
import static com.beyond.easycheck.tickets.exception.TicketOrderMessageType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TicketPaymentServiceTest {

    @Mock
    private TicketOrderRepository ticketOrderRepository;

    @Mock
    private TicketPaymentRepository ticketPaymentRepository;

    @InjectMocks
    private TicketPaymentService ticketPaymentService;

    private TicketOrderEntity mockOrder;
    private TicketPaymentEntity mockPayment;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);

        AccommodationEntity mockAccommodation = new AccommodationEntity(1L, "Accommodation Name", "Accommodation Address", HOTEL);

        ThemeParkOperationUseCase.ThemeParkCreateCommand command = ThemeParkOperationUseCase.ThemeParkCreateCommand.builder()
                .name("Test Theme Park")
                .description("Theme Park Description")
                .location("Location")
                .build();
        ThemeParkEntity mockThemePark = ThemeParkEntity.createThemePark(command, mockAccommodation);

        TicketCreateCommand ticketCommand = new TicketCreateCommand(1L, "Test Ticket", BigDecimal.valueOf(1000),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7));
        TicketEntity mockTicket = TicketEntity.createTicket(ticketCommand, mockThemePark);

        UserEntity mockUser = UserEntity.createGuestUser("Test User", "010-1234-5678");
        setEntityId(mockUser);

        mockOrder = new TicketOrderEntity(mockTicket, 2, mockUser, ReceiptMethodType.EMAIL, CollectionAgreementType.Y);
        setEntityId(mockOrder);

        mockPayment = new TicketPaymentEntity(mockOrder, BigDecimal.valueOf(1000), "CARD");
        setEntityId(mockPayment);
    }

    private void setEntityId(Object entity) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, 1L);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void processPayment_success() {
        TicketPaymentRequest request = new TicketPaymentRequest(1L, "CARD", BigDecimal.valueOf(1000));
        when(ticketOrderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        TicketPaymentEntity result = ticketPaymentService.processPayment(1L, 1L, request);

        assertNotNull(result);
        verify(ticketPaymentRepository, times(1)).save(any(TicketPaymentEntity.class));
    }

    @Test
    void processPayment_orderNotFound() {
        TicketPaymentRequest request = new TicketPaymentRequest(1L, "CARD", BigDecimal.valueOf(1000));
        when(ticketOrderRepository.findById(anyLong())).thenReturn(Optional.empty());

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketPaymentService.processPayment(1L, 1L, request));
        assertEquals(ORDER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void processPayment_invalidOrderStatus() {
        TicketPaymentRequest request = new TicketPaymentRequest(1L, "CARD", BigDecimal.valueOf(1000));
        mockOrder.cancelOrder();
        when(ticketOrderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketPaymentService.processPayment(1L, 1L, request));
        assertEquals(INVALID_ORDER_STATUS_FOR_PAYMENT.getMessage(), exception.getMessage());
    }

    @Test
    void cancelPayment_success() {
        when(ticketOrderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));
        when(ticketPaymentRepository.findByTicketOrderId(anyLong())).thenReturn(Optional.of(mockPayment));
        when(ticketPaymentRepository.save(any(TicketPaymentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketPaymentEntity result = ticketPaymentService.cancelPayment(1L, 1L, "User requested cancellation");

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, mockOrder.getOrderStatus());
        assertEquals(PaymentStatus.CANCELLED, mockPayment.getPaymentStatus());
        verify(ticketPaymentRepository, times(1)).save(mockPayment);
    }

    @Test
    void cancelPayment_paymentNotFound() {
        when(ticketOrderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));
        when(ticketPaymentRepository.findByTicketOrderId(anyLong())).thenReturn(Optional.empty());

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketPaymentService.cancelPayment(1L, 1L, "User requested cancellation"));
        assertEquals(PAYMENT_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void refundPayment_invalidStatusForRefund() {
        when(ticketOrderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));
        when(ticketPaymentRepository.findByTicketOrderId(anyLong())).thenReturn(Optional.of(mockPayment));

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketPaymentService.refundPayment(1L, 1L, "User requested refund"));
        assertEquals(INVALID_STATUS_FOR_REFUND.getMessage(), exception.getMessage());
    }

    @Test
    void retryPayment_invalidOrderStatus() {
        TicketPaymentRequest request = new TicketPaymentRequest(1L, "CARD", BigDecimal.valueOf(1000));
        mockOrder.completeOrder();
        when(ticketOrderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> ticketPaymentService.retryPayment(1L, 1L, request));
        assertEquals(INVALID_ORDER_STATUS_FOR_RETRY.getMessage(), exception.getMessage());
    }
}