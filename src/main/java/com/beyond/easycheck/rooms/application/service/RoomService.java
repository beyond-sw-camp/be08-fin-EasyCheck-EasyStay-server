package com.beyond.easycheck.rooms.application.service;

import com.beyond.easycheck.rooms.infrastructure.persistence.entity.RoomEntity;
import com.beyond.easycheck.rooms.infrastructure.persistence.repository.RoomRepository;
import com.beyond.easycheck.rooms.ui.requestbody.RoomCreateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public void createRoom(RoomCreateRequest roomCreateRequest) {

        RoomEntity room = RoomEntity.builder()
                .roomTypeId(roomCreateRequest.getRoomTypeId())
                .roomNumber(roomCreateRequest.getRoomNumber())
                .roomPic(roomCreateRequest.getRoomPic())
                .status(roomCreateRequest.getStatus())
                .build();

        roomRepository.save(room);
    }
}
