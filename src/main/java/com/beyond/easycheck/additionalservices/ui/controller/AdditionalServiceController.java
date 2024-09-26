package com.beyond.easycheck.additionalservices.ui.controller;

import com.beyond.easycheck.additionalservices.application.service.AdditionalServiceService;
import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.additionalservices.infrastructure.repository.AdditionalServiceRepository;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceCreateRequest;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceUpdateRequest;
import com.beyond.easycheck.additionalservices.ui.view.AdditionalServiceView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Additional Service", description = "부가 서비스 관리 API")
@RestController
@RequestMapping("/api/v1/additionalservices")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AdditionalServiceController {

    private final AdditionalServiceService additionalServiceService;
    private final AdditionalServiceRepository additionalServiceRepository;

    @Operation(summary = "부가 서비스를 등록하는 API")
    @PostMapping("")
    public ResponseEntity<Void> createAdditionalService(@RequestBody @Valid AdditionalServiceCreateRequest additionalServiceCreateRequest) {

        additionalServiceService.createAdditionalService(additionalServiceCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "모든 부가 서비스를 조회하는 API")
    @GetMapping("")
    public ResponseEntity<List<AdditionalServiceView>> getAllAdditionalService(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<AdditionalServiceView> additionalServiceViews = additionalServiceService.getAllAdditionalService(page, size);

        return ResponseEntity.ok(additionalServiceViews);
    }

    @Operation(summary = "특정 부가 서비스를 조회하는 API")
    @GetMapping("/{id}")
    public ResponseEntity<AdditionalServiceView> getAdditionalServiceById(@PathVariable("id") Long id) {

        AdditionalServiceView additionalServiceView = additionalServiceService.getAdditionalServiceById(id);

        return ResponseEntity.ok(additionalServiceView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdditionalServiceView> updateAdditionalService(
            @PathVariable Long id,
            @RequestBody AdditionalServiceUpdateRequest additionalServiceUpdateRequest) {

        additionalServiceService.updateAdditionalService(id, additionalServiceUpdateRequest);

        AdditionalServiceView updatedService = additionalServiceService.getAdditionalServiceById(id);

        return ResponseEntity.ok(updatedService);
    }
}
