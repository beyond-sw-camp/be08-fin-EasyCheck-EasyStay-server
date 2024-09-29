package com.beyond.easycheck.reservationroom.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.reservationroom.exception.ReservationRoomMessageType;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationroom.infrastructure.repository.ReservationRoomRepository;
import com.beyond.easycheck.reservationroom.ui.requestbody.ReservationRoomCreateRequest;
import com.beyond.easycheck.reservationroom.ui.view.ReservationRoomView;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ReservationRoomService {

    private final ReservationRoomRepository reservationRoomRepository;

    @Transactional
    public ReservationRoomEntity createReservation(ReservationRoomCreateRequest reservationRoomCreateRequest) {

        ReservationRoomEntity reservationRoomEntity = ReservationRoomEntity.builder()
                .reservationDate(LocalDateTime.now())
                .checkinDate(reservationRoomCreateRequest.getCheckinDate())
                .checkoutDate(reservationRoomCreateRequest.getCheckoutDate())
                .reservationStatus(reservationRoomCreateRequest.getReservationStatus())
                .totalPrice(reservationRoomCreateRequest.getTotalPrice())
                .paymentStatus(reservationRoomCreateRequest.getPaymentStatus())
                .build();

        return reservationRoomRepository.save(reservationRoomEntity);
    }

    public List<ReservationRoomView> getAllReservations(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReservationRoomEntity> reservationPage = reservationRoomRepository.findAll(pageable);

        return reservationPage.getContent().stream()
                .map(ReservationRoomView::of)
                .collect(Collectors.toList());
    }

    public ReservationRoomView getReservationById(Long id) {

        ReservationRoomEntity reservationRoomEntity = reservationRoomRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(ReservationRoomMessageType.RESERVATION_ROOM_NOT_FOUND)
        );

        return ReservationRoomView.of(reservationRoomEntity);
    }
}
