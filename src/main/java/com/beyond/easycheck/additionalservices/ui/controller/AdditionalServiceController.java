package com.beyond.easycheck.additionalservices.ui.controller;

import com.beyond.easycheck.additionalservices.application.service.AdditionalServiceService;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Additional Service", description = "부가 서비스 관리 API")
@RestController
@RequestMapping("/api/v1/additionalservices")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AdditionalServiceController {

    private final AdditionalServiceService additionalServiceService;

    @Operation(summary = "부가 서비스를 등록하는 API")
    @PostMapping("")
    public ResponseEntity<Void> createAdditionalService(@RequestBody @Valid AdditionalServiceCreateRequest additionalServiceCreateRequest) {

        additionalServiceService.createAdditionalService(additionalServiceCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
