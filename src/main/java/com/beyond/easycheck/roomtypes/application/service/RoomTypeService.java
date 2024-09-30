package com.beyond.easycheck.roomtypes.application.service;

import com.beyond.easycheck.accomodations.exception.AccommodationMessageType;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.CommonMessageType;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.rooms.exception.RoomMessageType;
import com.beyond.easycheck.roomtypes.exception.RoomTypeMessageType;
import com.beyond.easycheck.roomtypes.infrastructure.entity.RoomTypeEntity;
import com.beyond.easycheck.roomtypes.infrastructure.repository.RoomTypeRepository;
import com.beyond.easycheck.roomtypes.ui.requestbody.RoomTypeCreateRequest;
import com.beyond.easycheck.roomtypes.ui.requestbody.RoomTypeReadRequest;
import com.beyond.easycheck.roomtypes.ui.requestbody.RoomTypeUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final AccommodationRepository accommodationRepository;

    @Transactional
    public void createRoomType(RoomTypeCreateRequest roomTypeCreateRequest) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(roomTypeCreateRequest.getAccommodationId())
                .orElseThrow(() -> new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND));

        RoomTypeEntity roomType = RoomTypeEntity.builder()
                .accommodationEntity(accommodationEntity)
                .typeName(roomTypeCreateRequest.getTypeName())
                .description(roomTypeCreateRequest.getDescription())
                .maxOccupancy(roomTypeCreateRequest.getMaxOccupancy())
                .build();

        roomTypeRepository.save(roomType);
    }

    @Transactional
    public RoomTypeReadRequest readRoomType(Long roomTypeId) {

        RoomTypeEntity roomTypeEntity = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new EasyCheckException(RoomTypeMessageType.ROOM_TYPE_NOT_FOUND));

        AccommodationEntity accommodationEntity = accommodationRepository.findById(roomTypeEntity.getAccommodationEntity().getId())
                .orElseThrow(() -> new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND));

        RoomTypeReadRequest roomTypeReadRequest = RoomTypeReadRequest.builder()
                .accomodationId(accommodationEntity.getId())
                .roomTypeId(roomTypeEntity.getRoomTypeId())
                .typeName(roomTypeEntity.getTypeName())
                .description(roomTypeEntity.getDescription())
                .maxOccupancy(roomTypeEntity.getMaxOccupancy())
                .build();

        return roomTypeReadRequest;
    }

    @Transactional
    public List<RoomTypeReadRequest> readRoomTypes() {

        List<RoomTypeEntity> roomTypeEntities = roomTypeRepository.findAll();

        if (roomTypeEntities.isEmpty()) {
            throw new EasyCheckException(RoomTypeMessageType.ROOM_TYPE_NOT_FOUND);
        }
        List<RoomTypeReadRequest> roomTypeReadRequests = roomTypeEntities.stream()
                .map(roomTypeEntity -> new RoomTypeReadRequest(
                        roomTypeEntity.getRoomTypeId(),
                        roomTypeEntity.getAccommodationEntity().getId(),
                        roomTypeEntity.getTypeName(),
                        roomTypeEntity.getDescription(),
                        roomTypeEntity.getMaxOccupancy()
                )).collect(Collectors.toList());

        return roomTypeReadRequests;
    }

    @Transactional
    public void updateRoomType(Long roomTypeId, RoomTypeUpdateRequest roomTypeUpdateRequest) {

        RoomTypeEntity roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new EasyCheckException(RoomTypeMessageType.ROOM_TYPE_NOT_FOUND));

        roomType.update(roomTypeUpdateRequest);

    }

    @Transactional
    public void deleteRoomType(Long roomTypeId) {
        RoomTypeEntity roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new EasyCheckException(RoomTypeMessageType.ROOM_TYPE_NOT_FOUND));

        roomTypeRepository.delete(roomType);
    }
}
