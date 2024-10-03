package com.beyond.easycheck.reservationroom.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.reservationroom.exception.ReservationRoomMessageType;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationStatus;
import com.beyond.easycheck.reservationroom.infrastructure.repository.ReservationRoomRepository;
import com.beyond.easycheck.reservationroom.ui.requestbody.ReservationRoomCreateRequest;
import com.beyond.easycheck.reservationroom.ui.requestbody.ReservationRoomUpdateRequest;
import com.beyond.easycheck.reservationroom.ui.view.ReservationRoomView;
import com.beyond.easycheck.rooms.exception.RoomMessageType;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import com.beyond.easycheck.rooms.infrastructure.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ReservationRoomService {

    private final ReservationRoomRepository reservationRoomRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public ReservationRoomEntity createReservation(ReservationRoomCreateRequest reservationRoomCreateRequest) {

        RoomEntity roomEntity = roomRepository.findById(reservationRoomCreateRequest.getRoomId())
                .orElseThrow(() -> new EasyCheckException(RoomMessageType.ROOM_NOT_FOUND));

        if (!roomEntity.getStatus().equals(RoomStatus.예약가능)) {
            throw new EasyCheckException(ReservationRoomMessageType.ROOM_NOT_AVAILABLE);
        }

        if (reservationRoomCreateRequest.getCheckinDate().isBefore(LocalDate.now())) {
            throw new EasyCheckException(ReservationRoomMessageType.INVALID_CHECKIN_DATE);
        }

        if (reservationRoomCreateRequest.getCheckoutDate().isBefore(reservationRoomCreateRequest.getCheckinDate())) {
            throw new EasyCheckException(ReservationRoomMessageType.INVALID_CHECKOUT_DATE);
        }

        boolean isRoomAlreadyBooked = reservationRoomRepository.existsByRoomEntityAndCheckinDateLessThanEqualAndCheckoutDateGreaterThanEqual(
                roomEntity, reservationRoomCreateRequest.getCheckoutDate(), reservationRoomCreateRequest.getCheckinDate());

        if (isRoomAlreadyBooked) {
            throw new EasyCheckException(ReservationRoomMessageType.ROOM_ALREADY_BOOKED);
        }

        ReservationRoomEntity reservationRoomEntity = ReservationRoomEntity.builder()
                .roomEntity(roomEntity)
                .reservationDate(LocalDateTime.now())
                .checkinDate(reservationRoomCreateRequest.getCheckinDate())
                .checkoutDate(reservationRoomCreateRequest.getCheckoutDate())
                .reservationStatus(ReservationStatus.POSSIBLE)
                .totalPrice(reservationRoomCreateRequest.getTotalPrice())
                .paymentStatus(reservationRoomCreateRequest.getPaymentStatus())
                .build();

        return reservationRoomRepository.save(reservationRoomEntity);
    }

    @Transactional(readOnly = true)
    public List<ReservationRoomView> getAllReservations(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReservationRoomEntity> reservationPage = reservationRoomRepository.findAll(pageable);

        return reservationPage.getContent().stream()
                .map(ReservationRoomView::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationRoomView getReservationById(Long id) {

        ReservationRoomEntity reservationRoomEntity = reservationRoomRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(ReservationRoomMessageType.RESERVATION_NOT_FOUND)
        );

        return ReservationRoomView.of(reservationRoomEntity);
    }

    @Transactional
    public void cancelReservation(Long id, ReservationRoomUpdateRequest reservationRoomUpdateRequest) {

        ReservationRoomEntity reservationRoomEntity = reservationRoomRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(ReservationRoomMessageType.RESERVATION_NOT_FOUND)
        );

        reservationRoomEntity.updateReservationRoom(reservationRoomUpdateRequest);

        reservationRoomRepository.save(reservationRoomEntity);
    }
}
