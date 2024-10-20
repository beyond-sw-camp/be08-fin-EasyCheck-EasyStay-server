package com.beyond.easycheck.accomodations.ui.controller;

import com.beyond.easycheck.accomodations.application.service.AccommodationService;
import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationCreateRequest;
import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationUpdateRequest;
import com.beyond.easycheck.accomodations.ui.view.AccommodationView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Accommodation", description = "시설 정보 관리 API")
@RestController
@RequestMapping("/api/v1/accommodations")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AccommodationController {

    private final AccommodationService accommodationService;

    @Operation(summary = "시설을 등록하는 API")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createAccommodation(
            @RequestPart("accommodation") @Valid AccommodationCreateRequest accommodationCreateRequest,
            @RequestPart("thumbnails") List<MultipartFile> thumbnailFiles,
            @RequestPart("landscapes") List<MultipartFile> landscapeFiles,
            @RequestPart("directions") MultipartFile directionsUrl) {

        accommodationService.createAccommodation(accommodationCreateRequest, thumbnailFiles, landscapeFiles, directionsUrl);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "모든 시설의 리스트를 반환하는 API")
    @GetMapping("")
    public ResponseEntity<List<AccommodationView>> getAllAccommodations() {

        List<AccommodationView> accommodation = accommodationService.getAllAccommodations();
        return ResponseEntity.ok(accommodation);
    }

    @Operation(summary = "특성 시설의 정보를 반환하는 API")
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationView> getAccommodationById(@PathVariable("id") Long id) {

        AccommodationView accommodationView = accommodationService.getAccommodationById(id);

        return ResponseEntity.ok(accommodationView);
    }

    @Operation(summary = "시설의 정보를 수정하는 API")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAccommodation(@PathVariable("id") Long id,
                                                    @RequestBody @Valid AccommodationUpdateRequest accommodationUpdateRequest) {

        accommodationService.updateAccommodation(id, accommodationUpdateRequest);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "시설의 정보를 삭제하는 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable("id") Long id) {

        accommodationService.deleteAccommodation(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
