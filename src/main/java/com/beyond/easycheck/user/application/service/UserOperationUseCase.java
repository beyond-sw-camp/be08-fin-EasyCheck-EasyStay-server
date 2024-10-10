package com.beyond.easycheck.user.application.service;

import com.beyond.easycheck.corporate.ui.requestbody.CorporateCreateRequest;
import com.beyond.easycheck.user.application.domain.UserStatus;
import com.beyond.easycheck.user.application.service.UserReadUseCase.FindJwtResult;
import com.beyond.easycheck.user.application.service.UserReadUseCase.FindUserResult;
import org.springframework.web.multipart.MultipartFile;

public interface UserOperationUseCase {

    void registerUser(UserRegisterCommand command);

    void registerCorporateUser(UserRegisterCommand command, CorporateCreateRequest corporateCreateRequest, MultipartFile verificationFilesZip);

    FindUserResult updateUserStatus(UserStatusUpdateCommand command);

    FindJwtResult login(UserLoginCommand command);

    FindJwtResult loginGuest(GuestUserLoginCommand command);

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

    record GuestUserLoginCommand(
            String name,
            String phone
    ) {
    }

    record UserStatusUpdateCommand(
            Long userId,
            UserStatus status
    ) {

    }
    record UserLogoutCommand(
            String accessToken,
            Long userId
    ) {
    }

    record ChangePasswordCommand(
            String email,
            String oldPassword,
            String newPassword
    ) {
    }
}
