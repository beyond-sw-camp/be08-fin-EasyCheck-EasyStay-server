package com.beyond.easycheck.rooms.ui.controller;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.rooms.application.service.RoomService;
import com.beyond.easycheck.rooms.exception.RoomMessageType;
import com.beyond.easycheck.rooms.ui.requestbody.RoomCreateRequest;
import com.beyond.easycheck.rooms.ui.requestbody.RoomUpdateRequest;
import com.beyond.easycheck.rooms.ui.view.RoomView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Room", description = "객실 관리 API")
@RequestMapping("api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("")
    @Operation(summary = "객실 생성 API")
    public ResponseEntity<Void> createRoom(@RequestBody @Valid RoomCreateRequest roomCreateRequest) {
        roomService.createRoom(roomCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "객실 단일 조회 API")
    public ResponseEntity<RoomView> readRoom(@PathVariable Long id) {
        RoomView roomView = roomService.readRoom(id);
        return ResponseEntity.ok().body(roomView);
    }

    @GetMapping("")
    @Operation(summary = "객실 전체 조회 API")
    public ResponseEntity<List<RoomView>> readRooms() {
        List<RoomView> roomViews = roomService.readRooms();
        return ResponseEntity.ok().body(roomViews);
    }

    @PutMapping("/{id}")
    @Operation(summary = "객실 수정 API")
    public ResponseEntity<Void> updateRoomType(@PathVariable Long id, @RequestBody RoomUpdateRequest roomUpdateRequest) {
        if (roomUpdateRequest.getRoomNumber() == null || roomUpdateRequest.getRoomNumber().isEmpty()
        || roomUpdateRequest.getRoomAmount() < 0) {
            throw new EasyCheckException(RoomMessageType.ARGUMENT_NOT_VALID);
        }

        roomService.updateRoom(id, roomUpdateRequest);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "객실 삭제 API")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
