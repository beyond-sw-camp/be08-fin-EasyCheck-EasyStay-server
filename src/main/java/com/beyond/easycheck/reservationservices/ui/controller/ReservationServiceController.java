package com.beyond.easycheck.reservationservices.ui.controller;

import com.beyond.easycheck.reservationservices.application.service.ReservationServiceService;
import com.beyond.easycheck.reservationservices.ui.requestbody.ReservationServiceCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ReservationService", description = "부가 서비스 예약 관리")
@RestController
@RequestMapping("/api/v1/reservation-service")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ReservationServiceController {

    private final ReservationServiceService reservationServiceService;

    @Operation(summary = "부가 서비스를 예약하는 API")
    @PostMapping("")
    public ResponseEntity<ReservationServiceCreateRequest> createReservationRoom(
            @RequestBody @Valid ReservationServiceCreateRequest reservationServiceCreateRequest) {

        reservationServiceService.createReservationRoom(reservationServiceCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
