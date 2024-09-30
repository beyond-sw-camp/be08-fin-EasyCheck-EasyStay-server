package com.beyond.easycheck.attractions.ui.controller;

import com.beyond.easycheck.attractions.application.service.AttractionOperationUseCase;
import com.beyond.easycheck.attractions.application.service.AttractionOperationUseCase.AttractionCreateCommand;
import com.beyond.easycheck.attractions.application.service.AttractionOperationUseCase.AttractionUpdateCommand;
import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase;
import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase.FindAttractionResult;
import com.beyond.easycheck.attractions.ui.requestbody.AttractionRequest;
import com.beyond.easycheck.attractions.ui.view.AttractionView;
import com.beyond.easycheck.common.ui.view.ApiResponseView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/parks/{themeParkId}/attractions")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionOperationUseCase attractionOperationUseCase;
    private final AttractionReadUseCase attractionReadUseCase;

    @PostMapping("")
    public ResponseEntity<ApiResponseView<AttractionView>> createAttraction(@PathVariable Long themeParkId,
                                                                            @RequestBody AttractionRequest request) {
        AttractionCreateCommand command = AttractionCreateCommand.builder()
                .themeParkId(themeParkId)
                .name(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .build();

        FindAttractionResult attraction = attractionOperationUseCase.createAttraction(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(new AttractionView(attraction)));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponseView<List<AttractionView>>> getAttractions(@PathVariable Long themeParkId) {
        List<FindAttractionResult> attractions = attractionReadUseCase.getAttractionsByThemePark(themeParkId);
        List<AttractionView> attractionViews = attractions.stream()
                .map(AttractionView::new)
                .toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(attractionViews));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseView<AttractionView>> getAttraction(@PathVariable Long id) {
        FindAttractionResult result = attractionReadUseCase.getAttractionById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(new AttractionView(result)));
    }

    @PutMapping("/{attractionId}")
    public ResponseEntity<ApiResponseView<AttractionView>> updateAttraction(@PathVariable Long attractionId,
                                                                            @RequestBody AttractionRequest request) {
        AttractionUpdateCommand command = AttractionUpdateCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .build();

        FindAttractionResult result = attractionOperationUseCase.updateAttraction(attractionId, command);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(new AttractionView(result)));
    }

    @DeleteMapping("/{attractionId}")
    public ResponseEntity<Void> deleteAttraction(@PathVariable Long attractionId) {
        attractionOperationUseCase.deleteAttraction(attractionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}