package com.beyond.easycheck.rooms.ui.controller;

import com.beyond.easycheck.rooms.application.service.RoomService;
import com.beyond.easycheck.rooms.ui.requestbody.RoomCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Room", description = "객실 관리 API")
@RequestMapping("api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("")
    @Operation(summary = "객실 생성 API")
    public ResponseEntity<Void> createRoom(@RequestBody @Valid RoomCreateRequest roomCreateRequest) {
        roomService.createRoom(roomCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
