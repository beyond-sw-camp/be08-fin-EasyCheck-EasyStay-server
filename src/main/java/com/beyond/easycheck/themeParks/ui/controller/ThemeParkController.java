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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/v1/accommodations/{accommodationId}/parks")
@RequiredArgsConstructor
public class ThemeParkController {

    private final ThemeParkOperationUseCase themeParkOperationUseCase;
    private final ThemeParkReadUseCase themeParkReadUseCase;

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

    @GetMapping("")
    public ResponseEntity<ApiResponseView<List<ThemeParkView>>> getAllThemeParks(
            @PathVariable Long accommodationId) {

        List<FindThemeParkResult> results = themeParkReadUseCase.getThemeParks(accommodationId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(results.stream().map(ThemeParkView::new).toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseView<ThemeParkView>> getThemePark(
            @PathVariable Long accommodationId,
            @PathVariable Long id) {

        FindThemeParkResult result = themeParkReadUseCase.getFindThemePark(id, accommodationId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(new ThemeParkView(result)));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThemePark(
            @PathVariable Long accommodationId,
            @PathVariable Long id) {

        themeParkOperationUseCase.deleteThemePark(id, accommodationId);
        return ResponseEntity.noContent().build();
    }
}
