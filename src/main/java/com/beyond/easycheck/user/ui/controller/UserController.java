package com.beyond.easycheck.user.ui.controller;

import com.beyond.easycheck.user.application.service.UserOperationUseCase;
import com.beyond.easycheck.user.ui.requestbody.UserRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.beyond.easycheck.user.application.service.UserOperationUseCase.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserOperationUseCase userOperationUseCase;

    @PostMapping("")
    public ResponseEntity<Void> registerUser(@RequestBody @Validated UserRegisterRequest request) {

        UserRegisterCommand command = new UserRegisterCommand(
                request.email(),
                request.password(),
                request.name(),
                request.phone(),
                request.addr(),
                request.addrDetail(),
                request.marketingConsent()
        );

        userOperationUseCase.registerUser(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
