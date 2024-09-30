package com.beyond.easycheck.user.application.service;

import com.beyond.easycheck.user.application.service.UserReadUseCase.FindJwtResult;

public interface UserOperationUseCase {

    void registerUser(UserRegisterCommand command);

    FindJwtResult login(UserLoginCommand command);

    void logout(UserLogoutCommand command);

    void changePassword(ChangePasswordCommand command);

    record UserRegisterCommand(
            String email,
            String password,
            String name,
            String phone,
            String addr,
            String addrDetail,
            char marketingConsent
    ) {
    }

    record UserLoginCommand(
            String email,
            String password
    ) {
    }

    record UserLogoutCommand(
            String accessToken,
            Long userId
    ) {
    }

    record ChangePasswordCommand(
            String email,
            String newPassword
    ) {
    }
}
