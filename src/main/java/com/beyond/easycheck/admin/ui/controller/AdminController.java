package com.beyond.easycheck.admin.ui.controller;

import com.beyond.easycheck.admin.application.service.AdminOperationUseCase;
import com.beyond.easycheck.admin.application.service.AdminReadUseCase;
import com.beyond.easycheck.admin.ui.requestbody.UserStatusUpdateRequest;
import com.beyond.easycheck.user.application.service.UserReadUseCase;
import com.beyond.easycheck.user.ui.view.UserView;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
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
}
