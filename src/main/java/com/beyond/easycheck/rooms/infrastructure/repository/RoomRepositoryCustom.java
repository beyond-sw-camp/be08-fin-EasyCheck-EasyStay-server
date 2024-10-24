package com.beyond.easycheck.rooms.infrastructure.repository;

import com.beyond.easycheck.rooms.application.dto.RoomFindQuery;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;

import java.util.List;

public interface RoomRepositoryCustom {

    List<RoomEntity> findAllRooms(RoomFindQuery query);
}
