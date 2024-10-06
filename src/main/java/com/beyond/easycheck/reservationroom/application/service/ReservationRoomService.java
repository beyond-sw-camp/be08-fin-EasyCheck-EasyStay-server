package com.beyond.easycheck.reservationroom.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.mail.application.service.MailService;
import com.beyond.easycheck.reservationroom.exception.ReservationRoomMessageType;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationStatus;
import com.beyond.easycheck.reservationroom.infrastructure.repository.ReservationRoomRepository;
import com.beyond.easycheck.reservationroom.ui.requestbody.ReservationRoomCreateRequest;
import com.beyond.easycheck.reservationroom.ui.requestbody.ReservationRoomUpdateRequest;
import com.beyond.easycheck.reservationroom.ui.view.DayRoomAvailabilityView;
import com.beyond.easycheck.reservationroom.ui.view.ReservationRoomView;
import com.beyond.easycheck.reservationroom.ui.view.RoomAvailabilityView;
import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceEntity;
import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceStatus;
import com.beyond.easycheck.reservationservices.infrastructure.repository.ReservationServiceRepository;
import com.beyond.easycheck.reservationservices.ui.requestbody.ReservationServiceUpdateRequest;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ReservationRoomService {

    private final ReservationRoomRepository reservationRoomRepository;
    private final RoomRepository roomRepository;
    private final UserJpaRepository userJpaRepository;
    private final MailService mailService;
    private final ReservationServiceRepository reservationServiceRepository;

    @Transactional
    public ReservationRoomEntity createReservation(Long userId, ReservationRoomCreateRequest reservationRoomCreateRequest) {

        UserEntity userEntity = userJpaRepository.findById(userId).orElseThrow(
                () -> new EasyCheckException(UserMessageType.USER_NOT_FOUND)
        );

        RoomEntity roomEntity = roomRepository.findById(reservationRoomCreateRequest.getRoomId()).orElseThrow(
                () -> new EasyCheckException(RoomMessageType.ROOM_NOT_FOUND)
        );

        if (!roomEntity.getStatus().equals(RoomStatus.예약가능)) {
            throw new EasyCheckException(ReservationRoomMessageType.ROOM_NOT_AVAILABLE);
        }

        ReservationRoomEntity reservationRoomEntity = ReservationRoomEntity.builder()
                .roomEntity(roomEntity)
                .userEntity(userEntity)
                .reservationDate(LocalDateTime.now())
                .checkinDate(reservationRoomCreateRequest.getCheckinDate())
                .checkoutDate(reservationRoomCreateRequest.getCheckoutDate())
                .reservationStatus(ReservationStatus.RESERVATION)
                .totalPrice(reservationRoomCreateRequest.getTotalPrice())
                .paymentStatus(reservationRoomCreateRequest.getPaymentStatus())
                .build();

        reservationRoomRepository.save(reservationRoomEntity);

        roomEntity.setRemainingRoom(roomEntity.getRemainingRoom() - 1);

        if (roomEntity.getRemainingRoom() <= 0) {
            roomEntity.setStatus(RoomStatus.예약불가);
        } else {
            roomEntity.setStatus(RoomStatus.예약가능);
        }

        roomRepository.save(roomEntity);

//        ReservationRoomView reservationRoomView = ReservationRoomView.of(reservationRoomEntity);
//        mailService.sendReservationConfirmationEmail(userEntity.getEmail(), reservationRoomView);

        return reservationRoomEntity;
    }

    @Transactional(readOnly = true)
    public List<DayRoomAvailabilityView> getRoomAvailabilityByMonth(int year, int month) {
        // 요청된 연월에 해당하는 시작 날짜와 끝 날짜 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 모든 객실 정보와 예약 정보를 조회
        List<RoomEntity> allRooms = roomRepository.findAll();
        List<ReservationRoomEntity> reservations = reservationRoomRepository.findByCheckinDateBetween(startDate.atStartOfDay(), endDate.atTime(23, 59));

        // 날짜별로 객실 예약 가능 상태를 수집
        List<DayRoomAvailabilityView> availabilityViews = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDate finalDate = date;  // effectively final 변수 생성
            List<RoomAvailabilityView> roomsForDay = allRooms.stream()
                    .map(room -> new RoomAvailabilityView(room, isRoomAvailable(room, reservations, finalDate)))
                    .collect(Collectors.toList());
            availabilityViews.add(new DayRoomAvailabilityView(finalDate, roomsForDay));
        }

        return availabilityViews;
    }

    // 객실이 해당 날짜에 예약 가능한지 확인하는 메서드
    private boolean isRoomAvailable(RoomEntity room, List<ReservationRoomEntity> reservations, LocalDate date) {
        return reservations.stream()
                .noneMatch(reservation -> reservation.getRoomEntity().equals(room) &&
                        !reservation.getCheckinDate().toLocalDate().isAfter(date) &&
                        !reservation.getCheckoutDate().toLocalDate().isBefore(date));
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

        List<ReservationServiceEntity> additionalServices = reservationServiceRepository.findByReservationRoomEntity(reservationRoomEntity);

        for (ReservationServiceEntity service : additionalServices) {
            service.cancelReservationService(new ReservationServiceUpdateRequest(ReservationServiceStatus.CANCELED));
        }
        reservationServiceRepository.saveAll(additionalServices);

        RoomEntity roomEntity = reservationRoomEntity.getRoomEntity();

        roomEntity.setRemainingRoom(roomEntity.getRemainingRoom() + 1);

        if (roomEntity.getRemainingRoom() > 0) {
            roomEntity.setStatus(RoomStatus.예약가능);
        }

        roomRepository.save(roomEntity);
    }
}
