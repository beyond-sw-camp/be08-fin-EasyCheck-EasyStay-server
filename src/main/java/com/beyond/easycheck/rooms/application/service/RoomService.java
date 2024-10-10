package com.beyond.easycheck.rooms.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.rooms.infrastructure.entity.DailyRoomAvailabilityEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import com.beyond.easycheck.rooms.infrastructure.repository.DailyRoomAvailabilityRepository;
import com.beyond.easycheck.rooms.infrastructure.repository.RoomRepository;
import com.beyond.easycheck.rooms.ui.requestbody.RoomCreateRequest;
import com.beyond.easycheck.rooms.ui.requestbody.RoomUpdateRequest;
import com.beyond.easycheck.rooms.ui.view.RoomView;
import com.beyond.easycheck.roomtypes.infrastructure.entity.RoomtypeEntity;
import com.beyond.easycheck.roomtypes.infrastructure.repository.RoomtypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.beyond.easycheck.rooms.exception.RoomMessageType.ROOMS_NOT_FOUND;
import static com.beyond.easycheck.rooms.exception.RoomMessageType.ROOM_NOT_FOUND;
import static com.beyond.easycheck.roomtypes.exception.RoomtypeMessageType.ROOM_TYPE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomtypeRepository roomTypeRepository;
    private final DailyRoomAvailabilityRepository dailyRoomAvailabilityRepository;

    @Transactional
    public RoomEntity createRoom(RoomCreateRequest roomCreateRequest) {

        RoomtypeEntity roomType = roomTypeRepository.findById(roomCreateRequest.getRoomTypeId())
                .orElseThrow(() -> new EasyCheckException(ROOM_TYPE_NOT_FOUND));

        RoomEntity room = RoomEntity.builder()
                .roomTypeEntity(roomType)
                .roomNumber(roomCreateRequest.getRoomNumber())
                .roomPic(roomCreateRequest.getRoomPic())
                .status(roomCreateRequest.getStatus())
                .roomAmount(roomCreateRequest.getRoomAmount())
                .remainingRoom(roomCreateRequest.getRemainingRoom())
                .build();

        room = roomRepository.save(room);

        return room;
    }

    public void initializeRoomAvailability(RoomEntity roomEntity) {
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 30; i++) {
            LocalDate date = today.plusDays(i);
            DailyRoomAvailabilityEntity availability = DailyRoomAvailabilityEntity.builder()
                    .roomEntity(roomEntity)
                    .date(date.atStartOfDay())
                    .remainingRoom(roomEntity.getRoomAmount())
                    .status(RoomStatus.예약가능)
                    .build();

            dailyRoomAvailabilityRepository.save(availability);
        }
    }

    public RoomView readRoom(Long id) {

        RoomEntity room = roomRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(ROOM_NOT_FOUND));

        RoomtypeEntity roomType = room.getRoomTypeEntity();

        RoomView roomView = RoomView.builder()
                .roomId(room.getRoomId())
                .roomNumber(room.getRoomNumber())
                .roomPic(room.getRoomPic())
                .roomAmount(room.getRoomAmount())
                .remainingRoom(room.getRemainingRoom())
                .status(room.getStatus())
                .roomTypeId(roomType.getRoomTypeId())
                .accomodationId(roomType.getAccommodationEntity().getId())
                .typeName(roomType.getTypeName())
                .description(roomType.getDescription())
                .maxOccupancy(roomType.getMaxOccupancy())
                .build();

        return roomView;
    }

    @Transactional
    public List<RoomView> readRooms() {

        List<RoomEntity> roomEntities = roomRepository.findAll();

        if (roomEntities.isEmpty()) {
            throw new EasyCheckException(ROOMS_NOT_FOUND);
        }
        List<RoomView> roomViews = roomEntities.stream()
                .map(roomEntity -> new RoomView(
                        roomEntity.getRoomId(),
                        roomEntity.getRoomNumber(),
                        roomEntity.getRoomPic(),
                        roomEntity.getRoomAmount(),
                        roomEntity.getRemainingRoom(),
                        roomEntity.getStatus(),
                        roomEntity.getRoomTypeEntity().getRoomTypeId(),
                        roomEntity.getRoomTypeEntity().getAccommodationEntity().getId(),
                        roomEntity.getRoomTypeEntity().getTypeName(),
                        roomEntity.getRoomTypeEntity().getDescription(),
                        roomEntity.getRoomTypeEntity().getMaxOccupancy()
                )).collect(Collectors.toList());

        return roomViews;
    }

    @Transactional
    public void updateRoom(Long roomId, RoomUpdateRequest roomUpdateRequest) {

        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EasyCheckException(ROOM_NOT_FOUND));

        room.update(roomUpdateRequest);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EasyCheckException(ROOM_NOT_FOUND));

        roomRepository.delete(room);
    }
}
