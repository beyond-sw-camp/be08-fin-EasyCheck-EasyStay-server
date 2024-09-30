package com.beyond.easycheck.facilities.ui.controller;

import com.beyond.easycheck.facilities.application.service.FacilityService;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityCreateRequest;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityUpdateRequest;
import com.beyond.easycheck.facilities.ui.view.FacilityView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Facility", description = "부대시설 관리")
@RestController
@RequestMapping("/api/v1/facilities")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class FacilityController {

    private final FacilityService facilityService;

    @Operation(summary = "부대시설을 등록하는 API")
    @PostMapping("")
    public ResponseEntity<Void> createFacility(@RequestBody @Valid FacilityCreateRequest facilityCreateRequest) {

        facilityService.createFacility(facilityCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "부대시설 리스트를 조회하는 API")
    @GetMapping("")
    public ResponseEntity<List<FacilityView>> getAllFacilities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<FacilityView> facilities = facilityService.getAllFacilities(page, size);

        return ResponseEntity.ok(facilities);
    }

    @Operation(summary = "특정 부대시설을 조회하는 API")
    @GetMapping("/{id}")
    public ResponseEntity<FacilityView> getFacilityById(@PathVariable("id") Long id) {

        FacilityView facilityView = facilityService.getFacilityById(id);

        return ResponseEntity.ok(facilityView);
    }

    @Operation(summary = "부대시설을 수정하는 API")
    @PutMapping("/{id}")
    public ResponseEntity<FacilityView> updateFacility(
            @PathVariable Long id,
            @RequestBody FacilityUpdateRequest facilityUpdateRequest) {

        facilityService.updateFacility(id, facilityUpdateRequest);

        FacilityView updatedFacility = facilityService.getFacilityById(id);

        return ResponseEntity.ok(updatedFacility);
    }

    @Operation(summary = "부대시설을 삭제하는 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacility(@PathVariable Long id) {

        facilityService.deleteFacility(id);

        return ResponseEntity.noContent().build();
    }
}
