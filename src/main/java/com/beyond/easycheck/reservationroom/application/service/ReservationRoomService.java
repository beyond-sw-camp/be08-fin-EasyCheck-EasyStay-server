package com.beyond.easycheck.reservationroom.application.service;

import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationroom.infrastructure.repository.ReservationRoomRepository;
import com.beyond.easycheck.reservationroom.ui.requestbody.ReservationRoomCreateRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
