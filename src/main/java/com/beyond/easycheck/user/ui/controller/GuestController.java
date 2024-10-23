package com.beyond.easycheck.user.ui.controller;

import com.beyond.easycheck.user.application.service.UserOperationUseCase;
import com.beyond.easycheck.user.application.service.UserOperationUseCase.GuestUserLoginCommand;
import com.beyond.easycheck.user.ui.requestbody.GuestUserLoginRequest;
import com.beyond.easycheck.user.ui.view.UserLoginView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.beyond.easycheck.user.application.service.UserReadUseCase.FindJwtResult;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/guests")
@Tag(name = "Guest", description = "비회원 관련 API")
public class GuestController {

    private final UserOperationUseCase userOperationUseCase;

    @PostMapping
    @Operation(summary = "비회원 로그인")
    public ResponseEntity<UserLoginView> login(@RequestBody @Validated GuestUserLoginRequest request) {

        GuestUserLoginCommand command = new GuestUserLoginCommand(request.name(), request.phone());

        FindJwtResult result = userOperationUseCase.loginGuest(command);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserLoginView(result));
    }
}
