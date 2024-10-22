package com.beyond.easycheck.user.ui.controller;

import com.beyond.easycheck.user.application.service.admin.AdminOperationUseCase;
import com.beyond.easycheck.user.application.service.admin.AdminOperationUseCase.UserStatusUpdateCommand;
import com.beyond.easycheck.user.application.service.user.UserOperationUseCase;
import com.beyond.easycheck.user.application.service.user.UserReadUseCase;
import com.beyond.easycheck.user.ui.requestbody.ChangePasswordRequest;
import com.beyond.easycheck.user.ui.requestbody.UserLoginRequest;
import com.beyond.easycheck.user.ui.requestbody.UserRegisterRequest;
import com.beyond.easycheck.user.ui.requestbody.UserStatusUpdateRequest;
import com.beyond.easycheck.user.ui.view.UserLoginView;
import com.beyond.easycheck.user.ui.view.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import static com.beyond.easycheck.user.application.service.user.UserOperationUseCase.*;
import static com.beyond.easycheck.user.application.service.user.UserReadUseCase.*;
import static com.beyond.easycheck.user.application.service.user.UserReadUseCase.FindJwtResult;
import static com.beyond.easycheck.user.application.service.user.UserReadUseCase.FindUserResult;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "유저 관리 API")
public class UserController {

    private final UserReadUseCase userReadUseCase;

    private final UserOperationUseCase userOperationUseCase;

    private final AdminOperationUseCase adminOperationUseCase;

    @PostMapping("")
    @Operation(summary = "일반 유저 회원가입 API")
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
    @Operation(summary = "일반 유저 로그인 API")
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
    @Operation(summary = "일반 유저 로그아웃 API")
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

    @GetMapping("/info")
    @Operation(summary = "로그인한 유저 정보 불러오기 API")
    public ResponseEntity<UserView> getUserInfo(@AuthenticationPrincipal Long userId) {

        UserFindQuery query = new UserFindQuery(userId);

        FindUserResult result = userReadUseCase.getUserInfo(query);

        return ResponseEntity.ok(new UserView(result));
    }

    @PatchMapping("/change-password")
    @Operation(summary = "비밀번호 변경 API")
    public ResponseEntity<Void> changePassword(@RequestBody @Validated ChangePasswordRequest request) {

        ChangePasswordCommand command = new ChangePasswordCommand(request.email(), request.oldPassword(), request.newPassword());

        userOperationUseCase.changePassword(command);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "유저 정보 바꾸는 API")
    public ResponseEntity<UserView> changeUserStatus(@PathVariable Long id, @RequestBody @Validated UserStatusUpdateRequest request) {
        UserStatusUpdateCommand command = new UserStatusUpdateCommand(id, request.status());

        FindUserResult result = adminOperationUseCase.updateUserStatus(command);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserView(result));
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
