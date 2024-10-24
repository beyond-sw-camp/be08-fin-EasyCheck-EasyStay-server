package com.beyond.easycheck.adasfas.ui.controller;

import com.beyond.easycheck.adasfas.application.service.ThemeParkOperationUseCase;
import com.beyond.easycheck.adasfas.application.service.ThemeParkOperationUseCase.ThemeParkCreateCommand;
import com.beyond.easycheck.adasfas.application.service.ThemeParkOperationUseCase.ThemeParkUpdateCommand;
import com.beyond.easycheck.adasfas.application.service.ThemeParkReadUseCase;
import com.beyond.easycheck.adasfas.application.service.ThemeParkReadUseCase.FindThemeParkResult;
import com.beyond.easycheck.adasfas.ui.requestbody.ThemeParkCreateRequest;
import com.beyond.easycheck.adasfas.ui.requestbody.ThemeParkUpdateRequest;
import com.beyond.easycheck.common.ui.view.ApiResponseView;
import com.beyond.easycheck.adasfas.ui.view.ThemeParkView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "ThemePark", description = "테마파크 정보 관리 API")
@RestController
@RequestMapping ("/api/v1/accommodations/{accommodationId}/parks")
@RequiredArgsConstructor
public class ThemeParkController {

    private final ThemeParkOperationUseCase themeParkOperationUseCase;
    private final ThemeParkReadUseCase themeParkReadUseCase;

    @Operation(summary = "테마파크를 등록하는 API")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseView<ThemeParkView>> createThemePark(
            @PathVariable Long accommodationId,
            @RequestPart("request") @Validated ThemeParkCreateRequest request,  // JSON 데이터
            @RequestPart("imageFiles") List<MultipartFile> imageFiles) {

        ThemeParkCreateCommand command = ThemeParkCreateCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        FindThemeParkResult result = themeParkOperationUseCase.saveThemePark(command, accommodationId, imageFiles);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(new ThemeParkView(result)));
    }

    @Operation(summary = "해당 사업장 내 테마파크를 전체 조회하는 API")
    @GetMapping("")
    public ResponseEntity<ApiResponseView<List<ThemeParkView>>> getAllThemeParks(
            @PathVariable Long accommodationId) {

        List<FindThemeParkResult> results = themeParkReadUseCase.getThemeParks(accommodationId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(results.stream().map(ThemeParkView::new).toList()));
    }

    @Operation(summary = "해당 사업장 내 특정 테마파크를 조회하는 API")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseView<ThemeParkView>> getThemePark(
            @PathVariable Long accommodationId,
            @PathVariable Long id) {

        FindThemeParkResult result = themeParkReadUseCase.getFindThemePark(id, accommodationId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(new ThemeParkView(result)));
    }

    @Operation(summary = "테마파크를 수정하는 API")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateThemePark(
            @PathVariable Long accommodationId,
            @PathVariable Long id,
            @RequestBody ThemeParkUpdateRequest request) {

        ThemeParkUpdateCommand command = ThemeParkUpdateCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        themeParkOperationUseCase.updateThemePark(id, command, accommodationId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "테마파크 이미지를 수정하는 API (일부 이미지 삭제 및 새로운 이미지 추가)")
    @PatchMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateThemeParkImages(
            @PathVariable Long id,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart(value = "imageIdsToDelete", required = false) List<Long> imageIdsToDelete) {

        themeParkOperationUseCase.updateThemeParkImages(id, imageFiles, imageIdsToDelete);

        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "테마파크를 삭제하는 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThemePark(
            @PathVariable Long accommodationId,
            @PathVariable Long id) {

        themeParkOperationUseCase.deleteThemePark(id, accommodationId);
        return ResponseEntity.noContent().build();
    }
}
