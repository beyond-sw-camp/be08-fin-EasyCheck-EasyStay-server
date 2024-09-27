package com.beyond.easycheck.roomType.ui.controller;

import com.beyond.easycheck.roomType.application.service.RoomTypeService;
import com.beyond.easycheck.roomType.ui.requestbody.RoomTypeCreateRequest;
import com.beyond.easycheck.roomType.ui.requestbody.RoomTypeReadRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "RoomType", description = "객실 유형 관리 API")
@RequestMapping("/api/v1/roomType")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @PostMapping("")
    @Operation(summary = "객실 유형 생성 API")
    public ResponseEntity<Void> createRoomType(@RequestBody RoomTypeCreateRequest roomTypeCreateRequest) {
        roomTypeService.createRoomType(roomTypeCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "객실 유형 조회 API")
    public ResponseEntity<RoomTypeReadRequest> readRoomType(@PathVariable Long id) {
        RoomTypeReadRequest roomTypeReadRequest = roomTypeService.readRoomType(id);
        return ResponseEntity.ok().body(roomTypeReadRequest);
    }

    @GetMapping("")
    @Operation(summary = "객실 유형 전체 조회 API")
    public ResponseEntity<List<RoomTypeReadRequest>> readRoomTypes() {
        List<RoomTypeReadRequest> roomTypeReadRequests = roomTypeService.readRoomTypes();
        return ResponseEntity.ok().body(roomTypeReadRequests);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "객실 유형 삭제 API")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }
}

