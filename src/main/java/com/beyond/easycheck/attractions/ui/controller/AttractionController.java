package com.beyond.easycheck.attractions.ui.controller;

import com.beyond.easycheck.attractions.application.service.AttractionOperationUseCase;
import com.beyond.easycheck.attractions.application.service.AttractionOperationUseCase.AttractionCreateCommand;
import com.beyond.easycheck.attractions.application.service.AttractionOperationUseCase.AttractionUpdateCommand;
import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase;
import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase.FindAttractionResult;
import com.beyond.easycheck.attractions.ui.view.AttractionView;
import com.beyond.easycheck.common.ui.view.ApiResponseView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Attraction", description = "어트랙션 시설 정보 관리 API")
@RestController
@RequestMapping("/api/v1/parks/{themeParkId}/attractions")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionOperationUseCase attractionOperationUseCase;
    private final AttractionReadUseCase attractionReadUseCase;

    @Operation(summary = "어트랙션 시설을 등록하는 API")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseView<FindAttractionResult>> createAttraction(
            @PathVariable Long themeParkId,
            @RequestPart("imageFile") MultipartFile imageFile,  // 이미지 파일을 단일 파일로 변경
            @RequestPart("request") AttractionCreateCommand command) {

        AttractionCreateCommand completeCommand = AttractionCreateCommand.builder()
                .themeParkId(themeParkId)
                .name(command.getName())
                .introduction(command.getIntroduction())
                .information(command.getInformation())
                .standardUse(command.getStandardUse())
                .build();

        FindAttractionResult attraction = attractionOperationUseCase.createAttraction(completeCommand, imageFile);  // 단일 파일 처리

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(attraction));
    }

    @Operation(summary = "어트랙션 시설의 내용을 수정하는 API")
    @PatchMapping("/{attractionId}")
    public ResponseEntity<Void> updateAttraction(
            @PathVariable Long attractionId,
            @RequestBody AttractionUpdateCommand command) {

        attractionOperationUseCase.updateAttraction(attractionId, command);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "어트랙션 시설의 이미지를 수정하는 API")
    @PatchMapping(value = "/{attractionId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)  // 단일 이미지 처리
    public ResponseEntity<Void> updateAttractionImage(
            @PathVariable Long attractionId,
            @RequestPart("imageFile") MultipartFile imageFile) {  // 단일 파일 처리

        attractionOperationUseCase.updateAttractionImage(attractionId, imageFile);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "테마파크 내의 어트랙션 시설 정보를 전체 조회하는 API")
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

    @Operation(summary = "어트랙션 시설을 삭제하는 API")
    @DeleteMapping("/{attractionId}")
    public ResponseEntity<Void> deleteAttraction(@PathVariable Long attractionId) {
        attractionOperationUseCase.deleteAttraction(attractionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
