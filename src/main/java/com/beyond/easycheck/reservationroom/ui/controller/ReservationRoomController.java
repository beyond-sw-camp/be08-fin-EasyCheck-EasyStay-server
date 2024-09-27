package com.beyond.easycheck.reservationroom.ui.controller;

import com.beyond.easycheck.reservationroom.application.service.ReservationRoomService;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationroom.ui.requestbody.ReservationRoomCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "ReservationRoom", description = "객실 예약 관리")
@RestController
@RequestMapping("/api/v1/reservationroom")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ReservationRoomController {

    private final ReservationRoomService reservationRoomService;

    @Operation(summary = "객실을 예약하는 API")
    @PostMapping("")
    public ResponseEntity<ReservationRoomEntity> createReservation(
            @RequestBody @Valid ReservationRoomCreateRequest reservationRoomCreateRequest) {

        reservationRoomService.createReservation(reservationRoomCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
