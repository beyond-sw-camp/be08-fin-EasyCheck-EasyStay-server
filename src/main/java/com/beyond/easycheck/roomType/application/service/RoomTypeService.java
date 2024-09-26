package com.beyond.easycheck.roomType.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.CommonMessageType;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.roomType.infrastructure.entity.RoomTypeEntity;
import com.beyond.easycheck.roomType.infrastructure.repository.RoomTypeRepository;
import com.beyond.easycheck.roomType.ui.requestbody.RoomTypeCreateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final AccommodationRepository accommodationRepository;

    @Transactional
    public Optional<RoomTypeEntity> createRoomType(RoomTypeCreateRequest roomTypeCreateRequest) {

        log.info("RoomTypeRequest = {}", roomTypeCreateRequest);
        AccommodationEntity accommodationEntity = accommodationRepository.findById(roomTypeCreateRequest.getAccommodationId())
                .orElseThrow(() -> new EasyCheckException(CommonMessageType.NOT_FOUND));

        RoomTypeEntity roomType = RoomTypeEntity.builder()
                .accommodationEntity(accommodationEntity)
                .typeName(roomTypeCreateRequest.getTypeName())
                .description(roomTypeCreateRequest.getDescription())
                .maxOccupancy(roomTypeCreateRequest.getMaxOccupancy())
                .build();

        return Optional.of(roomTypeRepository.save(roomType));
    }
}
