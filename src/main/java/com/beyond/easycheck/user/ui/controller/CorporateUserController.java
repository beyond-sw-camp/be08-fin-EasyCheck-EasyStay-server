package com.beyond.easycheck.user.ui.controller;


import com.beyond.easycheck.corporate.ui.requestbody.CorporateCreateRequest;
import com.beyond.easycheck.user.application.service.UserOperationUseCase;
import com.beyond.easycheck.user.ui.requestbody.UserRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.beyond.easycheck.user.application.service.UserOperationUseCase.UserRegisterCommand;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/corp-users")
public class CorporateUserController {

    private final UserOperationUseCase userOperationUseCase;

    @PostMapping("")
    public ResponseEntity<Void> register(
            @RequestPart(name = "user") @Validated UserRegisterRequest userRegisterRequest,
            @RequestPart(name = "corporate") @Validated CorporateCreateRequest corporateCreateRequest,
            @RequestPart(name = "file") MultipartFile zipFile
    ) {

        UserRegisterCommand command = new UserRegisterCommand(
                userRegisterRequest.email(),
                userRegisterRequest.password(),
                userRegisterRequest.name(),
                userRegisterRequest.phone(),
                userRegisterRequest.addr(),
                userRegisterRequest.addrDetail(),
                userRegisterRequest.marketingConsent()
        );

        userOperationUseCase.registerCorporateUser(command, corporateCreateRequest, zipFile);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
