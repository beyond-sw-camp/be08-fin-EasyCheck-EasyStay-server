package com.beyond.easycheck.roomtypes.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.roomtypes.infrastructure.entity.RoomtypeEntity;
import com.beyond.easycheck.roomtypes.ui.requestbody.RoomtypeCreateRequest;
import com.beyond.easycheck.roomtypes.ui.view.RoomtypeView;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = {"test"})
@Transactional
class RoomtypeServiceTest {

    @Autowired
    private RoomtypeService roomtypeService;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Test
    @DisplayName("[객실 유형 조회] - 성공")
    void readRoomtype() {
        // given
        Long accommodationId = 1L;
        AccommodationEntity accommodation = AccommodationEntity.builder()
                .id(accommodationId) // AccommodationEntity의 id를 수동으로 설정
                .name("호텔")
                .address("서울시")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        accommodationRepository.save(accommodation);

        // when
        RoomtypeView result = roomtypeService.readRoomtype(accommodationId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getRoomTypeId()).isEqualTo(1L);
        Assertions.assertThat(result.getAccomodationId()).isEqualTo(accommodationId);
        Assertions.assertThat(result.getTypeName()).isEqualTo("디럭스");
        Assertions.assertThat(result.getDescription()).isEqualTo("아늑한 룸");
        Assertions.assertThat(result.getMaxOccupancy()).isEqualTo(1);

    }

    @Test
    @DisplayName("[객실 유형 목록 조회] - 성공")
    void readRoomtypes() {
        // given
        RoomtypeCreateRequest request1 = new RoomtypeCreateRequest(6L,"디럭스","한 명이 묵을 수 있는 아늑한 룸", 1);
        RoomtypeCreateRequest request2 = new RoomtypeCreateRequest(7L, "디럭스 - 원룸", "두 명이 묵을 수 있는 넓은 룸", 2);

        roomtypeService.createRoomtype(request1);
        roomtypeService.createRoomtype(request2);

        // when
        List<RoomtypeView> result = roomtypeService.readRoomtypes();

        // then
        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getTypeName()).isEqualTo(request1.getTypeName());
        Assertions.assertThat(result.get(1).getTypeName()).isEqualTo(request2.getTypeName());

    }

    @Test
    @DisplayName("[객실 유형 목록 조회] - 빈 리스트")
    void readRoomtypes_emptyList(){
        // when
        List<RoomtypeView> result = roomtypeService.readRoomtypes();

        // then
        Assertions.assertThat(result).isEmpty(); // 결과는 빈 리스트여야함.
    }


}