package com.beyond.easycheck.user.ui.controller;

import com.beyond.easycheck.user.application.service.user.UserOperationUseCase;
import com.beyond.easycheck.user.application.service.user.UserOperationUseCase.GuestUserLoginCommand;
import com.beyond.easycheck.user.ui.requestbody.GuestUserLoginRequest;
import com.beyond.easycheck.user.ui.view.UserLoginView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.beyond.easycheck.user.application.service.user.UserReadUseCase.FindJwtResult;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/guests")
public class GuestController {

    private final UserOperationUseCase userOperationUseCase;

    @PostMapping
    public ResponseEntity<UserLoginView> login(@RequestBody @Validated GuestUserLoginRequest request) {

        GuestUserLoginCommand command = new GuestUserLoginCommand(request.name(), request.phone());

        FindJwtResult result = userOperationUseCase.loginGuest(command);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserLoginView(result));
    }
}
