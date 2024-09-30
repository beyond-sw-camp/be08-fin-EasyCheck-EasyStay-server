package com.beyond.easycheck.reservationservices.ui.controller;

import com.beyond.easycheck.reservationservices.application.service.ReservationServiceService;
import com.beyond.easycheck.reservationservices.ui.requestbody.ReservationServiceCreateRequest;
import com.beyond.easycheck.reservationservices.ui.view.ReservationServiceView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "부가 서비스 예약 리스트를 조회하는 API")
    @GetMapping("")
    public ResponseEntity<List<ReservationServiceView>> getAllReservationServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<ReservationServiceView> reservationServices = reservationServiceService.getAllReservationServices(page, size);

        return ResponseEntity.ok(reservationServices);
    }

    @Operation(summary = "특정 부가 서비스 예약을 조회하는 API")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationServiceView> getReservationServiceById(@PathVariable("id") Long id) {

        ReservationServiceView reservationServiceView = reservationServiceService.getReservationServiceById(id);

        return ResponseEntity.ok(reservationServiceView);
    }
}
