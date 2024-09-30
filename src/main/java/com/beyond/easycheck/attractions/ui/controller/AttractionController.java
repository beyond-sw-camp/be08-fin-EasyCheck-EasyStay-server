package com.beyond.easycheck.attractions.ui.controller;

import com.beyond.easycheck.attractions.application.service.AttractionOperationUseCase;
import com.beyond.easycheck.attractions.application.service.AttractionOperationUseCase.AttractionCreateCommand;
import com.beyond.easycheck.attractions.application.service.AttractionOperationUseCase.AttractionUpdateCommand;
import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase;
import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase.FindAttractionResult;
import com.beyond.easycheck.attractions.ui.requestbody.AttractionRequest;
import com.beyond.easycheck.attractions.ui.view.AttractionView;
import com.beyond.easycheck.common.ui.view.ApiResponseView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Attraction", description = "어트랙션 시설 정보 관리 API")
@RestController
@RequestMapping("/api/v1/parks/{themeParkId}/attractions")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionOperationUseCase attractionOperationUseCase;
    private final AttractionReadUseCase attractionReadUseCase;

    @Operation(summary = "어트랙션 시설을 등록하는 API")
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

    @Operation(summary = "테마파크 내의 어트랙션 시설정보를 전체 조회하는 API")
    @GetMapping("")
    public ResponseEntity<ApiResponseView<List<AttractionView>>> getAttractions(@PathVariable Long themeParkId) {
        List<FindAttractionResult> attractions = attractionReadUseCase.getAttractionsByThemePark(themeParkId);
        List<AttractionView> attractionViews = attractions.stream()
                .map(AttractionView::new)
                .toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(attractionViews));
    }

    @Operation(summary = "특정 어트랙션 시설 정보를 조회하는 API")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseView<AttractionView>> getAttraction(@PathVariable Long id) {
        FindAttractionResult result = attractionReadUseCase.getAttractionById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(new AttractionView(result)));
    }

    @Operation(summary = "어트랙션 시설을 수정하는 API")
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
    
    @Operation(summary = "어트랙션 시설을 삭제하는 API")
    @DeleteMapping("/{attractionId}")
    public ResponseEntity<Void> deleteAttraction(@PathVariable Long attractionId) {
        attractionOperationUseCase.deleteAttraction(attractionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}