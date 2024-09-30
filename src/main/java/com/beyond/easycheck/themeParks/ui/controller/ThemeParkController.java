package com.beyond.easycheck.themeparks.ui.controller;

import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase;
import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase.ThemeParkCreateCommand;
import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase.ThemeParkUpdateCommand;
import com.beyond.easycheck.themeparks.application.service.ThemeParkReadUseCase;
import com.beyond.easycheck.themeparks.application.service.ThemeParkReadUseCase.FindThemeParkResult;
import com.beyond.easycheck.themeparks.ui.requestbody.ThemeParkCreateRequest;
import com.beyond.easycheck.themeparks.ui.requestbody.ThemeParkUpdateRequest;
import com.beyond.easycheck.common.ui.view.ApiResponseView;
import com.beyond.easycheck.themeparks.ui.view.ThemeParkView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ThemePark", description = "테마파크 정보 관리 API")
@RestController
@RequestMapping ("/api/v1/accommodations/{accommodationId}/parks")
@RequiredArgsConstructor
public class ThemeParkController {

    private final ThemeParkOperationUseCase themeParkOperationUseCase;
    private final ThemeParkReadUseCase themeParkReadUseCase;

    @Operation(summary = "테마파크를 등록하는 API")
    @PostMapping("")
    public ResponseEntity<ApiResponseView<ThemeParkView>> createThemePark(
            @PathVariable Long accommodationId,
            @RequestBody @Validated ThemeParkCreateRequest request) {

        ThemeParkCreateCommand command = ThemeParkCreateCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .location(request.getLocation())
                .image(request.getImage())
                .build();

        FindThemeParkResult result = themeParkOperationUseCase.saveThemePark(command, accommodationId);

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
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseView<ThemeParkView>> updateThemePark(
            @PathVariable Long accommodationId,
            @PathVariable Long id,
            @RequestBody @Validated ThemeParkUpdateRequest request) {

        ThemeParkUpdateCommand command = ThemeParkUpdateCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .location(request.getLocation())
                .image(request.getImage())
                .build();

        FindThemeParkResult result = themeParkOperationUseCase.updateThemePark(id, command, accommodationId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(new ThemeParkView(result)));
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
