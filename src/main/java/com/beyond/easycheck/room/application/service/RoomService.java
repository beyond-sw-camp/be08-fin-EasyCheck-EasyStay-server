package com.beyond.easycheck.room.application.service;

import com.beyond.easycheck.common.exception.CommonMessageType;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.room.infrastructure.persistence.entity.RoomEntity;
import com.beyond.easycheck.room.infrastructure.persistence.entity.RoomTypeEntity;
import com.beyond.easycheck.room.infrastructure.persistence.repository.RoomRateRepository;
import com.beyond.easycheck.room.infrastructure.persistence.repository.RoomRepository;
import com.beyond.easycheck.room.infrastructure.persistence.repository.RoomTypeRepository;
import com.beyond.easycheck.room.infrastructure.persistence.repository.SeasonRepository;
import com.beyond.easycheck.room.ui.requestbody.Room.RoomCreateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRateRepository roomRateRepository;
    private final SeasonRepository seasonRepository;

    @Transactional
    public void createRoom(RoomCreateRequest roomCreateRequest) {

        RoomTypeEntity roomType = roomTypeRepository.findById(roomCreateRequest.getRoomTypeId())
                .orElseThrow(() -> new EasyCheckException(CommonMessageType.NOT_FOUND));

        RoomEntity room = RoomEntity.builder()
                .roomTypeId(roomType)
                .roomNumber(roomCreateRequest.getRoomNumber())
                .roomPic(roomCreateRequest.getRoomPic())
                .status(roomCreateRequest.getStatus())
                .build();

        room = roomRepository.save(room);
    }
}
