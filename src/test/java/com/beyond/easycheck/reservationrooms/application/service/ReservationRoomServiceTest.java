package com.beyond.easycheck.reservationrooms.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.mail.application.service.MailService;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.PaymentStatus;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationStatus;
import com.beyond.easycheck.reservationrooms.infrastructure.repository.ReservationRoomRepository;
import com.beyond.easycheck.reservationrooms.ui.requestbody.ReservationRoomCreateRequest;
import com.beyond.easycheck.reservationrooms.ui.requestbody.ReservationRoomUpdateRequest;
import com.beyond.easycheck.reservationservices.infrastructure.repository.ReservationServiceRepository;
import com.beyond.easycheck.rooms.infrastructure.entity.DailyRoomAvailabilityEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import com.beyond.easycheck.rooms.infrastructure.repository.DailyRoomAvailabilityRepository;
import com.beyond.easycheck.rooms.infrastructure.repository.RoomRepository;
import com.beyond.easycheck.roomtypes.infrastructure.entity.RoomtypeEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationRoomServiceTest {

    @InjectMocks
    private ReservationRoomService reservationRoomService;

    @Mock
    private ReservationRoomRepository reservationRoomRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private MailService mailService;

    @Mock
    private DailyRoomAvailabilityRepository dailyRoomAvailabilityRepository;

    @Mock
    private ReservationServiceRepository reservationServiceRepository;

    private ReservationRoomEntity reservationRoomEntity;
    private UserEntity userEntity;
    private RoomEntity roomEntity;
    private DailyRoomAvailabilityEntity dailyRoomAvailabilityEntity;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        userEntity = mock(UserEntity.class);
        roomEntity = mock(RoomEntity.class);
        dailyRoomAvailabilityEntity = mock(DailyRoomAvailabilityEntity.class);
        reservationRoomEntity = mock(ReservationRoomEntity.class);

        when(userEntity.getEmail()).thenReturn("test@test.com");
        when(roomEntity.getRoomTypeEntity()).thenReturn(mock(RoomtypeEntity.class));
        when(roomEntity.getRoomTypeEntity().getTypeName()).thenReturn("Deluxe");
    }

    @Test
    void testCreateReservation_success() {

        // given
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(roomEntity));

        LocalDate checkinDate = LocalDate.of(2024, 10, 11);
        LocalDate checkoutDate = LocalDate.of(2024, 10, 13);

        when(dailyRoomAvailabilityRepository.findByRoomEntityAndDate(any(), any())).thenReturn(Optional.of(dailyRoomAvailabilityEntity));
        when(dailyRoomAvailabilityEntity.getRemainingRoom()).thenReturn(1);  // Room available

        when(reservationRoomRepository.save(any(ReservationRoomEntity.class))).thenReturn(reservationRoomEntity);

        ReservationRoomCreateRequest request = new ReservationRoomCreateRequest();
        request.setRoom(1L, LocalDateTime.now(), checkinDate, checkoutDate, ReservationStatus.RESERVATION, 10000, null);

        // when
        ReservationRoomEntity result = reservationRoomService.createReservation(1L, request);

        // then
        assertNotNull(result);
        verify(reservationRoomRepository, times(1)).save(any(ReservationRoomEntity.class));
        verify(mailService, times(1)).sendReservationConfirmationEmail(anyString(), any());
    }

    @Test
    void testCreateReservation_userNotFound() {

        // given
        when(userJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        ReservationRoomCreateRequest request = new ReservationRoomCreateRequest();
        request.setRoom(1L, LocalDateTime.now(), LocalDate.of(2024, 10, 11), LocalDate.of(2024, 10, 13),
                ReservationStatus.RESERVATION, 10000, PaymentStatus.UNPAID);

        EasyCheckException exception = assertThrows(EasyCheckException.class, () ->
                reservationRoomService.createReservation(1L, request));

        assertEquals("User not found in the system", exception.getMessage());
    }

    @Test
    void testCreateReservation_roomNotFound() {

        // given
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        ReservationRoomCreateRequest request = new ReservationRoomCreateRequest();
        request.setRoom(1L, LocalDateTime.now(), LocalDate.of(2024, 10, 11), LocalDate.of(2024, 10, 13),
                ReservationStatus.RESERVATION, 10000, PaymentStatus.UNPAID);

        EasyCheckException exception = assertThrows(EasyCheckException.class, () ->
                reservationRoomService.createReservation(1L, request));

        assertEquals("Room not found", exception.getMessage());
    }

    @Test
    void testCancelReservation_success() {

        // given
        when(reservationRoomRepository.findById(1L)).thenReturn(Optional.of(reservationRoomEntity));

        LocalDate checkinDate = LocalDate.of(2024, 10, 11);
        LocalDate checkoutDate = LocalDate.of(2024, 10, 13);
        when(reservationRoomEntity.getCheckinDate()).thenReturn(checkinDate);
        when(reservationRoomEntity.getCheckoutDate()).thenReturn(checkoutDate);
        when(reservationRoomEntity.getRoomEntity()).thenReturn(roomEntity);

        when(dailyRoomAvailabilityRepository.findByRoomEntityAndDate(any(RoomEntity.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(dailyRoomAvailabilityEntity));

        when(dailyRoomAvailabilityEntity.getRemainingRoom()).thenReturn(1);

        ReservationRoomUpdateRequest updateRequest = new ReservationRoomUpdateRequest();
        updateRequest.setReservationStatus(ReservationStatus.CANCELED);

        // when
        reservationRoomService.cancelReservation(1L, updateRequest);

        // then
        verify(reservationRoomRepository, times(1)).save(any(ReservationRoomEntity.class));
        verify(dailyRoomAvailabilityRepository, times(3)).save(any(DailyRoomAvailabilityEntity.class)); // 3 days
    }
}