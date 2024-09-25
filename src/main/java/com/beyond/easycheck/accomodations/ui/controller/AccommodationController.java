package com.beyond.easycheck.accomodations.ui.controller;

import com.beyond.easycheck.accomodations.application.service.AccommodationService;
import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationCreateRequest;
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

@Tag(name = "Accommodation", description = "시설 정보 관리 API")
@RestController
@RequestMapping("/api/v1/accommodations")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AccommodationController {

    private final AccommodationService accommodationService;

    @PostMapping("")
    public ResponseEntity<Void> createAccommodation(@RequestBody @Valid AccommodationCreateRequest accommodationCreateRequest) {

        accommodationService.createAccommodation(accommodationCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
