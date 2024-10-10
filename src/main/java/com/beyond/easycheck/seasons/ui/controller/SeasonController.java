package com.beyond.easycheck.seasons.ui.controller;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.rooms.exception.RoomMessageType;
import com.beyond.easycheck.seasons.application.service.SeasonService;
import com.beyond.easycheck.seasons.exception.SeasonMessageType;
import com.beyond.easycheck.seasons.ui.requestbody.SeasonCreateRequest;
import com.beyond.easycheck.seasons.ui.requestbody.SeasonUpdateRequest;
import com.beyond.easycheck.seasons.ui.view.SeasonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Season", description = "시즌 관리 API")
@RequestMapping("api/v1/seasons")
public class SeasonController {

    private final SeasonService seasonService;

    @PostMapping("")
    @Operation(summary = "시즌 생성 API")
    public ResponseEntity<Void> createSeason(@RequestBody SeasonCreateRequest seasonCreateRequest) {
        if (seasonCreateRequest.getSeasonName() == null ||
                seasonCreateRequest.getDescription() == null || seasonCreateRequest.getDescription().isEmpty() ||
                seasonCreateRequest.getStartDate() == null ||
                seasonCreateRequest.getEndDate() == null) {

            throw new EasyCheckException(SeasonMessageType.ARGUMENT_NOT_VALID);
        }

        seasonService.createSeason(seasonCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "시즌 단일 조회 API")
    public ResponseEntity<SeasonView> readSeason(@PathVariable Long id) {
        SeasonView seasonView = seasonService.readSeason(id);
        seasonService.readSeason(id);
        return ResponseEntity.ok().body(seasonView);
    }

    @GetMapping("")
    @Operation(summary = "시즌 전체 조회 API")
    public ResponseEntity<List<SeasonView>> readSeasons() {
        List<SeasonView> seasonViews = seasonService.readSeasons();
        return ResponseEntity.ok().body(seasonViews);
    }

    @PutMapping("/{id}")
    @Operation(summary = "시즌 수정 API")
    public ResponseEntity<Void> updateSeason(@PathVariable Long id, @RequestBody SeasonUpdateRequest seasonUpdateRequest) {
        if (seasonUpdateRequest.getSeasonName() == null || seasonUpdateRequest.getDescription().isEmpty()
                || seasonUpdateRequest.getStartDate() == null || seasonUpdateRequest.getEndDate() == null) {
            throw new EasyCheckException(RoomMessageType.ARGUMENT_NOT_VALID);
        }

        seasonService.updateSeason(id, seasonUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "시즌 삭제 API")
    public ResponseEntity<Void> deleteSeason(@PathVariable Long id) {
        seasonService.deleteSeason(id);
        return ResponseEntity.noContent().build();
    }

}
