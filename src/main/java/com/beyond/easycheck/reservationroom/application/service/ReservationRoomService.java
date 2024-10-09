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
import com.beyond.easycheck.rooms.infrastructure.entity.DailyRoomAvailabilityEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import com.beyond.easycheck.rooms.infrastructure.repository.DailyRoomAvailabilityRepository;
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
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ReservationRoomService {

    private final ReservationRoomRepository reservationRoomRepository;
    private final RoomRepository roomRepository;
    private final UserJpaRepository userJpaRepository;
    private final MailService mailService;
    private final ReservationServiceRepository reservationServiceRepository;
    private final DailyRoomAvailabilityRepository dailyRoomAvailabilityRepository;

    @Transactional
    public ReservationRoomEntity createReservation(Long userId, ReservationRoomCreateRequest reservationRoomCreateRequest) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new EasyCheckException(UserMessageType.USER_NOT_FOUND));

        RoomEntity roomEntity = roomRepository.findById(reservationRoomCreateRequest.getRoomId())
                .orElseThrow(() -> new EasyCheckException(RoomMessageType.ROOM_NOT_FOUND));

        if (!roomEntity.getStatus().equals(RoomStatus.예약가능)) {
            throw new EasyCheckException(ReservationRoomMessageType.ROOM_NOT_AVAILABLE);
        }

        LocalDate checkinDate = reservationRoomCreateRequest.getCheckinDate().toLocalDate();

        DailyRoomAvailabilityEntity dailyAvailability = dailyRoomAvailabilityRepository
                .findByRoomEntityAndDate(roomEntity, checkinDate.atStartOfDay())
                .orElse(DailyRoomAvailabilityEntity.builder()
                        .roomEntity(roomEntity)
                        .date(checkinDate.atStartOfDay())
                        .remainingRoom(10)
                        .status(RoomStatus.예약가능)
                        .build());

        dailyRoomAvailabilityRepository.save(dailyAvailability);

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

        dailyAvailability.decrementRemainingRoom();
        if (dailyAvailability.getRemainingRoom() <= 0) {
            dailyAvailability.setStatus(RoomStatus.예약불가);
        }
        dailyRoomAvailabilityRepository.save(dailyAvailability);

        return reservationRoomEntity;
    }

    @Transactional(readOnly = true)
    public List<DayRoomAvailabilityView> getRoomAvailabilityByMonth(int year, int month) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 조회 범위에 해당하는 가용성 데이터 조회
        List<DailyRoomAvailabilityEntity> availabilities = dailyRoomAvailabilityRepository
                .findAvailabilityByDateRange(startDate.atStartOfDay(), endDate.atTime(23, 59));

        // 조회할 전체 객실 목록을 가져오기 (모든 객실에 대해 가용성 여부 판단)
        List<RoomEntity> allRooms = roomRepository.findAll();

        // 가용성 데이터 생성
        return createDayRoomAvailabilityViews(availabilities, startDate, endDate, allRooms);
    }

    private List<DayRoomAvailabilityView> createDayRoomAvailabilityViews(
            List<DailyRoomAvailabilityEntity> availabilities, LocalDate startDate, LocalDate endDate, List<RoomEntity> allRooms) {

        List<DayRoomAvailabilityView> result = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDate finalDate = date;

            // 모든 방에 대해 가용성 확인, 없으면 기본값 생성
            List<RoomAvailabilityView> rooms = allRooms.stream()
                    .map(room -> {
                        // 현재 날짜에 대한 방의 가용성 정보가 있는지 확인
                        DailyRoomAvailabilityEntity dailyAvailability = availabilities.stream()
                                .filter(availability -> availability.getRoomEntity().equals(room) &&
                                        availability.getDate().toLocalDate().equals(finalDate))
                                .findFirst()
                                // 가용성 정보가 없으면 기본값 생성 (예약 가능)
                                .orElse(DailyRoomAvailabilityEntity.builder()
                                        .roomEntity(room)
                                        .date(finalDate.atStartOfDay())
                                        .remainingRoom(room.getRoomAmount())  // 기본 남은 객실 수
                                        .status(RoomStatus.예약가능)           // 기본 예약 가능 상태
                                        .build());

                        // RoomAvailabilityView 생성
                        return new RoomAvailabilityView(
                                room.getRoomId(),
                                room.getRoomTypeEntity().getTypeName(),
                                room.getRoomNumber(),
                                dailyAvailability.getRemainingRoom(),
                                dailyAvailability.getStatus()
                        );
                    })
                    .collect(Collectors.toList());

            // 날짜별 가용성 정보 추가
            result.add(new DayRoomAvailabilityView(
                    finalDate,
                    finalDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN),
                    rooms
            ));
        }

        return result;
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
        LocalDate checkinDate = reservationRoomEntity.getCheckinDate().toLocalDate();

        DailyRoomAvailabilityEntity dailyAvailability = dailyRoomAvailabilityRepository.findByRoomEntityAndDate(roomEntity, checkinDate.atStartOfDay())
                .orElseThrow(() -> new EasyCheckException(ReservationRoomMessageType.ROOM_NOT_AVAILABLE));

        dailyAvailability.incrementRemainingRoom();

        if (dailyAvailability.getRemainingRoom() <= 0) {
            dailyAvailability.setStatus(RoomStatus.예약불가);
        } else {
            dailyAvailability.setStatus(RoomStatus.예약가능);
        }

        dailyRoomAvailabilityRepository.save(dailyAvailability);
    }
}
