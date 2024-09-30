package com.beyond.easycheck.user.ui.controller;

import com.beyond.easycheck.user.application.service.UserOperationUseCase;
import com.beyond.easycheck.user.ui.requestbody.ChangePasswordRequest;
import com.beyond.easycheck.user.ui.requestbody.UserLoginRequest;
import com.beyond.easycheck.user.ui.requestbody.UserRegisterRequest;
import com.beyond.easycheck.user.ui.view.UserLoginView;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.beyond.easycheck.user.application.service.UserOperationUseCase.*;
import static com.beyond.easycheck.user.application.service.UserReadUseCase.FindJwtResult;

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

    @PostMapping("/login")
    public ResponseEntity<UserLoginView> login(@RequestBody @Validated UserLoginRequest request) {

        UserLoginCommand command = new UserLoginCommand(
                request.email(),
                request.password()
        );

        FindJwtResult result = userOperationUseCase.login(command);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserLoginView(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, @AuthenticationPrincipal Long userId) {

        String authorization = request.getHeader("Authorization");
        String accessToken = authorization.substring(7);

        log.info("[UserController - logout] userId = {}", userId);
        log.info("[UserController - logout] accessToken = {}", accessToken);

        UserLogoutCommand command = new UserLogoutCommand(
                accessToken,
                userId
        );

        userOperationUseCase.logout(command);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Validated ChangePasswordRequest request) {

        ChangePasswordCommand command = new ChangePasswordCommand(request.email(), request.newPassword());

        userOperationUseCase.changePassword(command);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/auth-test")
    public String changePassword(@AuthenticationPrincipal Long userId) {

        log.info("AUTH test");

        log.info("[userId] = {}", userId);
        log.info("[security context] = {}", SecurityContextHolder.getContext().toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("[authentication] = {}", authentication.getAuthorities());
        return "OK";
    }
}
