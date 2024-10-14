package com.beyond.easycheck.reservationservices.application.service;

import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.additionalservices.infrastructure.repository.AdditionalServiceRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationStatus;
import com.beyond.easycheck.reservationrooms.infrastructure.repository.ReservationRoomRepository;
import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceEntity;
import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceStatus;
import com.beyond.easycheck.reservationservices.infrastructure.repository.ReservationServiceRepository;
import com.beyond.easycheck.reservationservices.ui.requestbody.ReservationServiceCreateRequest;
import com.beyond.easycheck.reservationservices.ui.requestbody.ReservationServiceUpdateRequest;
import com.beyond.easycheck.reservationservices.ui.view.ReservationServiceView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceServiceTest {

    @Mock
    private ReservationRoomRepository reservationRoomRepository;

    @Mock
    private AdditionalServiceRepository additionalServiceRepository;

    @Mock
    private ReservationServiceRepository reservationServiceRepository;

    @InjectMocks
    private ReservationServiceService reservationService;


    @Test
    @DisplayName("[부가서비스 예약] - 성공")
    void createReservationRoom_Success() {
        // Given
        ReservationServiceCreateRequest request = new ReservationServiceCreateRequest(
                1L, 2L, 2, 20000, ReservationServiceStatus.RESERVATION
        );


        ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                .reservationStatus(ReservationStatus.RESERVATION)
                .build();

        AdditionalServiceEntity additionalService = AdditionalServiceEntity.builder()
                .price(10000)
                .build();


        when(reservationRoomRepository.findById(1L)).thenReturn(Optional.of(reservationRoom));
        when(additionalServiceRepository.findById(2L)).thenReturn(Optional.of(additionalService));
        when(reservationServiceRepository.existsByReservationRoomEntityAndAdditionalServiceEntity(any(), any())).thenReturn(false);
        when(reservationServiceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservationServiceEntity result = reservationService.createReservationRoom(request);

        // Then
        assertNotNull(result);
        assertEquals(reservationRoom, result.getReservationRoomEntity());
        assertEquals(additionalService, result.getAdditionalServiceEntity());
        assertEquals(2, result.getQuantity());
        assertEquals(request.getTotalPrice(), result.getTotalPrice());
        assertEquals(ReservationServiceStatus.RESERVATION, result.getReservationServiceStatus());

        verify(reservationRoomRepository).findById(1L);
        verify(additionalServiceRepository).findById(2L);
        verify(reservationServiceRepository).existsByReservationRoomEntityAndAdditionalServiceEntity(reservationRoom, additionalService);
        verify(reservationServiceRepository).save(any(ReservationServiceEntity.class));
    }

    @Test
    @DisplayName("[부가서비스 예약] - 실패 - 예약 객실을 찾지 못함")
    void createReservationRoom_ReservationRoomNotFound() {
        // Given
        ReservationServiceCreateRequest request = new ReservationServiceCreateRequest(
                1L, 2L, 2, 20000, ReservationServiceStatus.RESERVATION
        );

        when(reservationRoomRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EasyCheckException.class, () -> reservationService.createReservationRoom(request));
        verify(reservationRoomRepository).findById(1L);
    }

    @Test
    @DisplayName("[부가서비스 예약] - 실패 - 예약이 취소됨")
    void createReservationRoom_ReservationCanceled() {
        // Given
        ReservationServiceCreateRequest request = new ReservationServiceCreateRequest(
                1L, 2L, 2, 20000, ReservationServiceStatus.RESERVATION
        );


        ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                .reservationStatus(ReservationStatus.CANCELED)
                .build();


        when(reservationRoomRepository.findById(1L)).thenReturn(Optional.of(reservationRoom));

        // When & Then
        assertThrows(EasyCheckException.class, () -> reservationService.createReservationRoom(request));
        verify(reservationRoomRepository).findById(1L);
    }

    @Test
    @DisplayName("[부가서비스 예약] - 실패 - 부가서비스를 찾지 못함")
    void createReservationRoom_AdditionalServiceNotFound() {
        // Given
        // Given
        ReservationServiceCreateRequest request = new ReservationServiceCreateRequest(
                1L, 2L, 2, 20000, ReservationServiceStatus.RESERVATION
        );


        ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                .reservationStatus(ReservationStatus.RESERVATION)
                .build();


        when(reservationRoomRepository.findById(1L)).thenReturn(Optional.of(reservationRoom));
        when(additionalServiceRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EasyCheckException.class, () -> reservationService.createReservationRoom(request));
        verify(reservationRoomRepository).findById(1L);
        verify(additionalServiceRepository).findById(2L);
    }

    @Test
    @DisplayName("[부가서비스 예약] - 실패 - 중복된 부가서비스")
    void createReservationRoom_DuplicateService() {
        // Given
        ReservationServiceCreateRequest request = new ReservationServiceCreateRequest(
                1L, 2L, 2, 20000, ReservationServiceStatus.RESERVATION
        );


        ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                .reservationStatus(ReservationStatus.RESERVATION)
                .build();

        AdditionalServiceEntity additionalService = AdditionalServiceEntity.builder()
                .price(2000)
                .build();



        when(reservationRoomRepository.findById(1L)).thenReturn(Optional.of(reservationRoom));
        when(additionalServiceRepository.findById(2L)).thenReturn(Optional.of(additionalService));
        when(reservationServiceRepository.existsByReservationRoomEntityAndAdditionalServiceEntity(any(), any())).thenReturn(true);

        // When & Then
        assertThrows(EasyCheckException.class, () -> reservationService.createReservationRoom(request));
        verify(reservationRoomRepository).findById(1L);
        verify(additionalServiceRepository).findById(2L);
        verify(reservationServiceRepository).existsByReservationRoomEntityAndAdditionalServiceEntity(reservationRoom, additionalService);
    }

    @Test
    @DisplayName("[부가서비스 예약] - 실패 - 잘못된 부가서비스 수량")
    void createReservationRoom_InvalidQuantity() {
        // Given
        ReservationServiceCreateRequest request = new ReservationServiceCreateRequest(
                1L, 2L, 2, 20000, ReservationServiceStatus.RESERVATION
        );


        ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                .reservationStatus(ReservationStatus.RESERVATION)
                .build();

        AdditionalServiceEntity additionalService = AdditionalServiceEntity.builder()
                .price(2000)
                .build();

        when(reservationRoomRepository.findById(1L)).thenReturn(Optional.of(reservationRoom));
        when(additionalServiceRepository.findById(2L)).thenReturn(Optional.of(additionalService));
        when(reservationServiceRepository.existsByReservationRoomEntityAndAdditionalServiceEntity(any(), any())).thenReturn(false);

        // When & Then
        assertThrows(EasyCheckException.class, () -> reservationService.createReservationRoom(request));
    }

    @Test
    @DisplayName("[부가서비스 예약] - 실패 - 잘못된 총 가격")
    void createReservationRoom_InvalidTotalPrice() {
        // Given
        // Given
        ReservationServiceCreateRequest request = new ReservationServiceCreateRequest(
                1L, 2L, 2, 20000, ReservationServiceStatus.RESERVATION
        );


        ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                .reservationStatus(ReservationStatus.RESERVATION)
                .build();

        AdditionalServiceEntity additionalService = AdditionalServiceEntity.builder()
                .price(2000)
                .build();

        when(reservationRoomRepository.findById(1L)).thenReturn(Optional.of(reservationRoom));
        when(additionalServiceRepository.findById(2L)).thenReturn(Optional.of(additionalService));
        when(reservationServiceRepository.existsByReservationRoomEntityAndAdditionalServiceEntity(any(), any())).thenReturn(false);

        // When & Then
        assertThrows(EasyCheckException.class, () -> reservationService.createReservationRoom(request));
    }

    @Test
    @DisplayName("[부가서비스 목록 조회] - 성공")
    void getAllReservationServices_Success() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                .id(1L)
                .reservationStatus(ReservationStatus.RESERVATION)
                .build();

        AdditionalServiceEntity additionalServiceEntity = AdditionalServiceEntity.builder()
                .id(1L)
                .price(2000)
                .build();

        ReservationServiceEntity entity1 = ReservationServiceEntity.builder()
                .id(1L)
                .quantity(2)
                .totalPrice(20000)
                .reservationRoomEntity(reservationRoom)
                .additionalServiceEntity(additionalServiceEntity)
                .reservationServiceStatus(ReservationServiceStatus.RESERVATION)
                .build();

        ReservationServiceEntity entity2 = ReservationServiceEntity.builder()
                .id(2L)
                .quantity(1)
                .totalPrice(10000)
                .additionalServiceEntity(additionalServiceEntity)
                .reservationRoomEntity(reservationRoom)
                .reservationServiceStatus(ReservationServiceStatus.RESERVATION)
                .build();

        List<ReservationServiceEntity> entities = Arrays.asList(entity1, entity2);
        Page<ReservationServiceEntity> entityPage = new PageImpl<>(entities, pageable, entities.size());

        when(reservationServiceRepository.findAll(pageable)).thenReturn(entityPage);

        // When
        List<ReservationServiceView> result = reservationService.getAllReservationServices(page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(reservationServiceRepository).findAll(pageable);
    }

    @Test
    @DisplayName("[부가서비스 단일 조회] - 성공")
    void getReservationServiceById_Success() {
        // Given
        Long id = 1L;

        ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                .id(1L)
                .reservationStatus(ReservationStatus.RESERVATION)
                .build();


        AdditionalServiceEntity additionalServiceEntity = AdditionalServiceEntity.builder()
                .id(1L)
                .price(2000)
                .build();


        ReservationServiceEntity entity = ReservationServiceEntity.builder()
                .id(id)
                .quantity(2)
                .totalPrice(20000)
                .additionalServiceEntity(additionalServiceEntity)
                .reservationRoomEntity(reservationRoom)
                .reservationServiceStatus(ReservationServiceStatus.RESERVATION)
                .build();

        when(reservationServiceRepository.findById(id)).thenReturn(Optional.of(entity));

        // When
        ReservationServiceView result = reservationService.getReservationServiceById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(2, result.getQuantity());
        assertEquals(20000, result.getTotalPrice());
        assertEquals(ReservationServiceStatus.RESERVATION, result.getReservationServiceStatus());

        verify(reservationServiceRepository).findById(id);
    }

    @Test
    @DisplayName("[부가서비스 단일 조회] - 실패 - 부가서비스를 찾지 못함")
    void getReservationServiceById_NotFound() {
        // Given
        Long id = 1L;
        when(reservationServiceRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EasyCheckException.class, () -> reservationService.getReservationServiceById(id));
        verify(reservationServiceRepository).findById(id);
    }

    @Test
    @DisplayName("[부가서비스 예약 취소] - 성공")
    void cancelReservationService_Success() {
        // Given
        Long id = 1L;
        ReservationServiceUpdateRequest updateRequest = new ReservationServiceUpdateRequest(ReservationServiceStatus.CANCELED);

        ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                .reservationStatus(ReservationStatus.RESERVATION)
                .build();

        ReservationServiceEntity reservationServiceEntity = ReservationServiceEntity.builder()
                .id(id)
                .reservationRoomEntity(reservationRoom)
                .reservationServiceStatus(ReservationServiceStatus.RESERVATION)
                .build();

        when(reservationServiceRepository.findById(id)).thenReturn(Optional.of(reservationServiceEntity));
        when(reservationServiceRepository.save(any(ReservationServiceEntity.class))).thenReturn(reservationServiceEntity);

        // When
        assertDoesNotThrow(() -> reservationService.cancelReservationService(id, updateRequest));

        // Then
        verify(reservationServiceRepository).findById(id);
        verify(reservationServiceRepository).save(reservationServiceEntity);
        assertEquals(ReservationServiceStatus.CANCELED, reservationServiceEntity.getReservationServiceStatus());
    }

    @Test
    @DisplayName("[부가서비스 예약 취소] - 실패 - 부가서비스를 찾지 못함")
    void cancelReservationService_NotFound() {
        // Given
        Long id = 1L;
        ReservationServiceUpdateRequest updateRequest = new ReservationServiceUpdateRequest(ReservationServiceStatus.CANCELED);

        when(reservationServiceRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EasyCheckException.class, () -> reservationService.cancelReservationService(id, updateRequest));
        verify(reservationServiceRepository).findById(id);
        verify(reservationServiceRepository, never()).save(any(ReservationServiceEntity.class));
    }

    @Test
    @DisplayName("[부가서비스 예약 취소] - 실패 - 이미 취소된 부가서비스")
    void cancelReservationService_AlreadyCanceled() {
        // Given
        Long id = 1L;
        ReservationServiceUpdateRequest updateRequest = new ReservationServiceUpdateRequest(ReservationServiceStatus.CANCELED);

        ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                .reservationStatus(ReservationStatus.RESERVATION)
                .build();

        ReservationServiceEntity reservationServiceEntity = ReservationServiceEntity.builder()
                .id(id)
                .reservationRoomEntity(reservationRoom)
                .reservationServiceStatus(ReservationServiceStatus.CANCELED)
                .build();

        when(reservationServiceRepository.findById(id)).thenReturn(Optional.of(reservationServiceEntity));

        // When & Then
        assertThrows(EasyCheckException.class, () -> reservationService.cancelReservationService(id, updateRequest));
        verify(reservationServiceRepository).findById(id);
        verify(reservationServiceRepository, never()).save(any(ReservationServiceEntity.class));
    }

    @Test
    @DisplayName("[부가서비스 예약 취소] - 실패 - 이미 취소된 예약")
    void cancelReservationService_ReservationAlreadyCanceled() {
        // Given
        Long id = 1L;
        ReservationServiceUpdateRequest updateRequest = new ReservationServiceUpdateRequest(ReservationServiceStatus.CANCELED);

        ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                .reservationStatus(ReservationStatus.CANCELED)
                .build();

        ReservationServiceEntity reservationServiceEntity = ReservationServiceEntity.builder()
                .id(id)
                .reservationRoomEntity(reservationRoom)
                .reservationServiceStatus(ReservationServiceStatus.RESERVATION)
                .build();

        when(reservationServiceRepository.findById(id)).thenReturn(Optional.of(reservationServiceEntity));

        // When & Then
        assertThrows(EasyCheckException.class, () -> reservationService.cancelReservationService(id, updateRequest));
        verify(reservationServiceRepository).findById(id);
        verify(reservationServiceRepository, never()).save(any(ReservationServiceEntity.class));
    }
}