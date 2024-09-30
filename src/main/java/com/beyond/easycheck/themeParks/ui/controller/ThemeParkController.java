package com.beyond.easycheck.themeparks.ui.controller;

import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase;
import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase.ThemeParkCreateCommand;
import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase.ThemeParkUpdateCommand;
import com.beyond.easycheck.themeparks.application.service.ThemeParkReadUseCase;
import com.beyond.easycheck.themeparks.application.service.ThemeParkReadUseCase.FindThemeParkResult;
import com.beyond.easycheck.themeParks.ui.requestbody.ThemeParkCreateRequest;
import com.beyond.easycheck.themeParks.ui.requestbody.ThemeParkUpdateRequest;
import com.beyond.easycheck.common.ui.view.ApiResponseView;
import com.beyond.easycheck.themeparks.ui.view.ThemeParkView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/v1/parks")
@RequiredArgsConstructor
public class ThemeParkController {

    private final ThemeParkOperationUseCase themeParkOperationUseCase;

    private final ThemeParkReadUseCase themeParkReadUseCase;

    @PostMapping("")
    public ResponseEntity<ApiResponseView<ThemeParkView>> createThemePark(@RequestBody @Validated ThemeParkCreateRequest request) {

        ThemeParkCreateCommand command = ThemeParkCreateCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .location(request.getLocation())
                .image(request.getImage())
                .build();

        FindThemeParkResult result = themeParkOperationUseCase.saveThemePark(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(new ThemeParkView(result)));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponseView<List<ThemeParkView>>> getAllThemeParks() {

        List<FindThemeParkResult> results = themeParkReadUseCase.getThemeParks();

    return ResponseEntity.status(HttpStatus.OK)
            .body(new ApiResponseView<>(results.stream().map(ThemeParkView::new).toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseView<ThemeParkView>> getThemePark(@PathVariable Long id) {

        FindThemeParkResult result = themeParkReadUseCase.getFindThemePark(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(new ThemeParkView(result)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseView<ThemeParkView>> updateThemePark(
            @PathVariable Long id,
            @RequestBody @Validated ThemeParkUpdateRequest request) {

        ThemeParkUpdateCommand command = ThemeParkUpdateCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .location(request.getLocation())
                .image(request.getImage())
                .build();

        FindThemeParkResult result = themeParkOperationUseCase.updateThemePark(id, command);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(new ThemeParkView(result)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThemePark(@PathVariable Long id) {
        themeParkOperationUseCase.deleteThemePark(id);
        return ResponseEntity.noContent().build();
    }
}
