package com.beyond.easycheck.roomrates.ui.controller;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.roomrates.application.service.RoomrateService;
import com.beyond.easycheck.roomrates.ui.requestbody.RoomrateCreateRequest;
import com.beyond.easycheck.roomrates.ui.requestbody.RoomrateUpdateRequest;
import com.beyond.easycheck.roomrates.ui.view.RoomrateView;
import com.beyond.easycheck.rooms.infrastructure.repository.RoomRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static com.beyond.easycheck.roomrates.exception.RoomrateMessageType.ARGUMENT_NOT_VALID;
import static com.beyond.easycheck.rooms.exception.RoomMessageType.ROOM_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@Tag(name = "RoomRate", description = "객실 요금 관리 API")
@RequestMapping("api/v1/roomrates")
public class RoomrateController {

    private final RoomrateService roomrateService;
    private final RoomRepository roomRepository;

    @PostMapping("")
    @Operation(summary = "객실 요금 생성 API")
    public ResponseEntity<Void> createRoomrate(@RequestBody @Valid RoomrateCreateRequest roomrateCreateRequest) {
        roomrateService.createRoomrate(roomrateCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "객실 요금 단일 조회 API")
    public ResponseEntity<RoomrateView> readRoomrate(@PathVariable Long id) {
        RoomrateView roomrateView = roomrateService.readRoomrate(id);
        return ResponseEntity.ok().body(roomrateView);
    }

    @GetMapping("")
    @Operation(summary = "객실 요금 전체 조회 API")
    public ResponseEntity<List<RoomrateView>> readRoomrates() {
        List<RoomrateView> roomrateViews = roomrateService.readRoomrates();
        return ResponseEntity.ok().body(roomrateViews);
    }

    @PutMapping("/{id}")
    @Operation(summary = "객실 요금 수정 API")
    public ResponseEntity<Void> updateRoomrate(@PathVariable Long id, @RequestBody RoomrateUpdateRequest roomrateUpdateRequest) {
        if (roomrateUpdateRequest.getRate() == null || roomrateUpdateRequest.getRateType() == null) {
            throw new EasyCheckException(ARGUMENT_NOT_VALID);
        }

        if (roomrateUpdateRequest.getRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new EasyCheckException(ARGUMENT_NOT_VALID);
        }

        if (!roomRepository.findById(roomrateUpdateRequest.getRoomEntity()).isPresent()) {
            throw new EasyCheckException(ROOM_NOT_FOUND);
        }

        roomrateService.updateRoomrate(id, roomrateUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "객실 요금 삭제 API")
    public ResponseEntity<Void> deleteRoomrate(@PathVariable Long id) {
        roomrateService.deleteRoomrate(id);
        return ResponseEntity.noContent().build();
    }
    
}
