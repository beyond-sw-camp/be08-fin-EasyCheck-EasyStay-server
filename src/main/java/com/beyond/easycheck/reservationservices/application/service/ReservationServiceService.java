package com.beyond.easycheck.reservationservices.application.service;

import com.beyond.easycheck.additionalservices.exception.AdditionalServiceMessageType;
import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.additionalservices.infrastructure.repository.AdditionalServiceRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.reservationroom.exception.ReservationRoomMessageType;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationroom.infrastructure.repository.ReservationRoomRepository;
import com.beyond.easycheck.reservationservices.exception.ReservationServiceMessageType;
import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceEntity;
import com.beyond.easycheck.reservationservices.infrastructure.repository.ReservationServiceRepository;
import com.beyond.easycheck.reservationservices.ui.requestbody.ReservationServiceCreateRequest;
import com.beyond.easycheck.reservationservices.ui.requestbody.ReservationServiceUpdateRequest;
import com.beyond.easycheck.reservationservices.ui.view.ReservationServiceView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
                .reservationServiceStatus(reservationServiceCreateRequest.getReservationServiceStatus())
                .build();

        return reservationServiceRepository.save(reservationServiceEntity);
    }

    @Transactional(readOnly = true)
    public List<ReservationServiceView> getAllReservationServices(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReservationServiceEntity> reservationServiceEntityPage = reservationServiceRepository.findAll(pageable);

        return reservationServiceEntityPage.getContent().stream()
                .map(ReservationServiceView::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationServiceView getReservationServiceById(Long id) {

        ReservationServiceEntity reservationServiceEntity = reservationServiceRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(ReservationServiceMessageType.RESERVATION_SERVICE_NOT_FOUND)
        );

        return ReservationServiceView.of(reservationServiceEntity);
    }

    @Transactional
    public void cancelReservationService(Long id, ReservationServiceUpdateRequest reservationServiceUpdateRequest) {

        ReservationServiceEntity reservationServiceEntity = reservationServiceRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(ReservationServiceMessageType.RESERVATION_SERVICE_NOT_FOUND)
        );

        reservationServiceEntity.cancelReservationService(reservationServiceUpdateRequest);

        reservationServiceRepository.save(reservationServiceEntity);
    }
}
