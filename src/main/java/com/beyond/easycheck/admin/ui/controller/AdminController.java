package com.beyond.easycheck.admin.ui.controller;

import com.beyond.easycheck.admin.application.service.AdminOperationUseCase;
import com.beyond.easycheck.admin.application.service.AdminReadUseCase;
import com.beyond.easycheck.admin.ui.requestbody.UserStatusUpdateRequest;
import com.beyond.easycheck.admin.ui.view.*;

import com.beyond.easycheck.user.application.service.UserReadUseCase;
import com.beyond.easycheck.user.ui.view.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.beyond.easycheck.admin.application.service.AdminReadUseCase.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "관리자 관련 API")
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminOperationUseCase adminOperationUseCase;

    private final AdminReadUseCase adminReadUseCase;

    @PatchMapping("/{id}/status")
    @Operation(summary = "유저 정보 바꾸는 API")
    public ResponseEntity<UserView> changeUserStatus(@PathVariable Long id, @RequestBody @Validated UserStatusUpdateRequest request) {
        AdminOperationUseCase.UserStatusUpdateCommand command = new AdminOperationUseCase.UserStatusUpdateCommand(id, request.status());

        UserReadUseCase.FindUserResult result = adminOperationUseCase.updateUserStatus(command);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserView(result));
    }

    @GetMapping("/notices")
    @Operation(summary = "관리자 담당 업체 건의사항 모두 조회")
    public ResponseEntity<List<NoticesView>> getAllNotices() {

        List<FindNoticeResult> results = adminReadUseCase.getAllNotices();

        return ResponseEntity.ok(
                results.stream()
                        .map(NoticesView::new)
                        .toList()
        );
    }

    @GetMapping("/events")
    @Operation(summary = "관리자 담당 업체 이벤트 모두 조회")
    public ResponseEntity<List<EventView>> getAllEvents() {
        return ResponseEntity.ok(
                adminReadUseCase
                        .getAllEvents()
                        .stream()
                        .map(EventView::new).toList()
        );
    }

    @GetMapping("/suggestions")
    @Operation(summary = "관리자 담당 업체 건의사항 모두 조회")
    public ResponseEntity<List<SuggestionCompactView>> getAllSuggestions() {

        List<FindSuggestionResult> result = adminReadUseCase.getAllSuggestions();

        return ResponseEntity.ok(
                result.stream()
                        .map(SuggestionCompactView::new)
                        .toList()
        );
    }

    @GetMapping("/additional-services")
    @Operation(summary = "관리자 담당 업체 부가 서비드 모두 조회")
    public ResponseEntity<List<AdditionalServiceView>> getAllAdditionalServices() {
        return ResponseEntity.ok(
                adminReadUseCase
                        .getAllAdditionalServices()
                        .stream()
                        .map(AdditionalServiceView::new).toList()
        );
    }

    @GetMapping("/facilities")
    @Operation(summary = "관리자 담당 업체 부대시설 모두 조회")
    public ResponseEntity<List<FacilityView>> getAllFacilities() {
        return ResponseEntity.ok(
                adminReadUseCase
                        .getAllFacilities()
                        .stream()
                        .map(FacilityView::new).toList()
        );
    }

    @GetMapping("/parks")
    @Operation(summary = "관리자 담당 업체 테마파크 모두 조회")
    public ResponseEntity<List<ThemeParkView>> getAllThemeParks() {
        return ResponseEntity.ok(
                adminReadUseCase
                        .getAllThemeParks()
                        .stream()
                        .map(ThemeParkView::new).toList()
        );
    }
}
