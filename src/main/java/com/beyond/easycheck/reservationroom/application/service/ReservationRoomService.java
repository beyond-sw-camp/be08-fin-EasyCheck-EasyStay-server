package com.beyond.easycheck.reservationroom.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.mail.application.service.MailService;
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
import com.beyond.easycheck.user.exception.UserMessageType;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ReservationRoomService {

    private final ReservationRoomRepository reservationRoomRepository;
    private final RoomRepository roomRepository;
    private final UserJpaRepository userJpaRepository;
    private final MailService mailService;

    @Transactional
    public ReservationRoomEntity createReservation(Long userId, ReservationRoomCreateRequest reservationRoomCreateRequest) {

        UserEntity userEntity = userJpaRepository.findById(userId).orElseThrow(
                () -> new EasyCheckException(UserMessageType.USER_NOT_FOUND)
        );

        RoomEntity roomEntity = roomRepository.findById(reservationRoomCreateRequest.getRoomId()).orElseThrow(
                () -> new EasyCheckException(RoomMessageType.ROOM_NOT_FOUND)
        );

        // 방의 상태 확인
        if (!roomEntity.getStatus().equals(RoomStatus.예약가능)) {
            throw new EasyCheckException(ReservationRoomMessageType.ROOM_NOT_AVAILABLE);
        }

        // 체크인 날짜가 현재 시간보다 이전인지 확인
        if (reservationRoomCreateRequest.getCheckinDate().isBefore(LocalDateTime.now())) {
            throw new EasyCheckException(ReservationRoomMessageType.INVALID_CHECKIN_DATE);
        }

        // 체크아웃 날짜가 체크인 날짜보다 이전인지 확인
        if (reservationRoomCreateRequest.getCheckoutDate().isBefore(reservationRoomCreateRequest.getCheckinDate())) {
            throw new EasyCheckException(ReservationRoomMessageType.INVALID_CHECKOUT_DATE);
        }

        // 해당 방의 이미 예약된 개수 확인
        int bookedRoomsCount = reservationRoomRepository.countByRoomEntityAndCheckinDateLessThanEqualAndCheckoutDateGreaterThanEqual(
                roomEntity, reservationRoomCreateRequest.getCheckoutDate(), reservationRoomCreateRequest.getCheckinDate());

        // 방의 남은 개수 확인
        int remainingRooms = roomEntity.getRoomAmount() - bookedRoomsCount;

        if (remainingRooms <= 0) {
            throw new EasyCheckException(ReservationRoomMessageType.ROOM_ALREADY_BOOKED);
        }

        // 예약 생성
        ReservationRoomEntity reservationRoomEntity = ReservationRoomEntity.builder()
                .roomEntity(roomEntity)
                .userEntity(userEntity)
                .reservationDate(LocalDateTime.now())
                .checkinDate(reservationRoomCreateRequest.getCheckinDate())
                .checkoutDate(reservationRoomCreateRequest.getCheckoutDate())
                .reservationStatus(reservationRoomCreateRequest.getReservationStatus())
                .totalPrice(reservationRoomCreateRequest.getTotalPrice())
                .paymentStatus(reservationRoomCreateRequest.getPaymentStatus())
                .build();

        reservationRoomRepository.save(reservationRoomEntity);

        // 방이 모두 예약된 경우 RoomStatus를 "예약불가"로 변경
        if (remainingRooms - 1 == 0) {
            roomEntity.setStatus(RoomStatus.예약불가);
            roomRepository.save(roomEntity); // 방의 상태를 업데이트
        }

        ReservationRoomView reservationRoomView = ReservationRoomView.of(reservationRoomEntity);
        mailService.sendReservationConfirmationEmail(userEntity.getEmail(), reservationRoomView);

        return reservationRoomEntity;
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

        ReservationRoomEntity reservationRoomEntity = reservationRoomRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(ReservationRoomMessageType.RESERVATION_NOT_FOUND));

        reservationRoomEntity.updateReservationRoom(reservationRoomUpdateRequest);

        reservationRoomRepository.save(reservationRoomEntity);
    }
}
