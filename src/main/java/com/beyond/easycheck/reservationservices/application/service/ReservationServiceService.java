package com.beyond.easycheck.reservationservices.application.service;

import com.beyond.easycheck.additionalservices.exception.AdditionalServiceMessageType;
import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.additionalservices.infrastructure.repository.AdditionalServiceRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.reservationroom.exception.ReservationRoomMessageType;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationroom.infrastructure.repository.ReservationRoomRepository;
import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceEntity;
import com.beyond.easycheck.reservationservices.infrastructure.repository.ReservationServiceRepository;
import com.beyond.easycheck.reservationservices.ui.requestbody.ReservationServiceCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationServiceService {

    private final ReservationServiceRepository reservationServiceRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final AdditionalServiceRepository additionalServiceRepository;

    @Transactional
    public ReservationServiceEntity createReservationRoom(ReservationServiceCreateRequest reservationServiceCreateRequest) {

        ReservationRoomEntity reservationRoomEntity = reservationRoomRepository.findById(reservationServiceCreateRequest.getReservationRoomId()).orElseThrow(
                () -> new EasyCheckException(ReservationRoomMessageType.RESERVATION_NOT_FOUND)
        );

        AdditionalServiceEntity additionalServiceEntity = additionalServiceRepository.findById(reservationServiceCreateRequest.getAdditionalServiceId()).orElseThrow(
                () -> new EasyCheckException(AdditionalServiceMessageType.ADDITIONAL_SERVICE_NOT_FOUND)
        );

        ReservationServiceEntity reservationServiceEntity = ReservationServiceEntity.builder()
                .reservationRoomEntity(reservationRoomEntity)
                .additionalServiceEntity(additionalServiceEntity)
                .quantity(reservationServiceCreateRequest.getQuantity())
                .totalPrice(reservationServiceCreateRequest.getTotalPrice())
                .build();

        return reservationServiceRepository.save(reservationServiceEntity);
    }
}
