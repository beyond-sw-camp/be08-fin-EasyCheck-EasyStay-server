package com.beyond.easycheck.roomType.ui.controller;

import com.beyond.easycheck.roomType.application.service.RoomTypeService;
import com.beyond.easycheck.roomType.ui.requestbody.RoomTypeCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "RoomType", description = "객실 유형 관리 API")
@RequestMapping("/api/v1/roomType")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @PostMapping("")
    @Operation(summary = "객실 유형 생성 API")
    public ResponseEntity<Void> createRoom(@RequestBody RoomTypeCreateRequest roomTypeCreateRequest) {
        roomTypeService.createRoomType(roomTypeCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

