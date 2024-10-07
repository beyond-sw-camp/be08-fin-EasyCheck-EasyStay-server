package com.beyond.easycheck.rooms.application.service;

import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import com.beyond.easycheck.rooms.infrastructure.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoomService roomService;
    private final RoomRepository roomRepository;

    @Override
    public void run(String... args) {

        List<RoomEntity> rooms = roomRepository.findAll();

        rooms.forEach(roomService::initializeRoomAvailability);

        System.out.println("객실 가용성 초기화 완료");
    }
}