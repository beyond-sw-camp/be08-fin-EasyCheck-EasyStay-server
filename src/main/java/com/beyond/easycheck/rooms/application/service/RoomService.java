package com.beyond.easycheck.rooms.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.rooms.exception.RoomMessageType;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import com.beyond.easycheck.rooms.infrastructure.repository.RoomRepository;
import com.beyond.easycheck.rooms.ui.requestbody.RoomCreateRequest;
import com.beyond.easycheck.rooms.ui.requestbody.RoomUpdateRequest;
import com.beyond.easycheck.rooms.ui.views.RoomView;
import com.beyond.easycheck.roomtypes.infrastructure.entity.RoomTypeEntity;
import com.beyond.easycheck.roomtypes.infrastructure.repository.RoomTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Transactional
    public void createRoom(RoomCreateRequest roomCreateRequest) {

        RoomTypeEntity roomType = roomTypeRepository.findById(roomCreateRequest.getRoomTypeId())
                .orElseThrow(() -> new EasyCheckException(RoomMessageType.ROOM_NOT_FOUND));

        RoomEntity room = RoomEntity.builder()
                .roomTypeEntity(roomType)
                .roomNumber(roomCreateRequest.getRoomNumber())
                .roomPic(roomCreateRequest.getRoomPic())
                .status(roomCreateRequest.getStatus())
                .build();

        room = roomRepository.save(room);
    }

    public RoomView readRoom(Long id) {

        RoomEntity room = roomRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(RoomMessageType.ROOM_NOT_FOUND));

        RoomTypeEntity roomType = room.getRoomTypeEntity();

        RoomView roomView = RoomView.builder()
                .roomId(room.getRoomId())
                .roomNumber(room.getRoomNumber())
                .roomPic(room.getRoomPic())
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
            throw new EasyCheckException(RoomMessageType.ROOM_NOT_FOUND);
        }
        List<RoomView> roomViews = roomEntities.stream()
                .map(roomEntity -> new RoomView(
                        roomEntity.getRoomId(),
                        roomEntity.getRoomNumber(),
                        roomEntity.getRoomPic(),
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
                .orElseThrow(() -> new EasyCheckException(RoomMessageType.ROOM_NOT_FOUND));

        room.update(roomUpdateRequest);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EasyCheckException(RoomMessageType.ROOM_NOT_FOUND));

        roomRepository.delete(room);
    }
}
