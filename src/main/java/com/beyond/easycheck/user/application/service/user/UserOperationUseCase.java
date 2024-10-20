package com.beyond.easycheck.user.application.service.user;

import com.beyond.easycheck.corporate.ui.requestbody.CorporateCreateRequest;
import com.beyond.easycheck.user.application.service.user.UserReadUseCase.FindJwtResult;
import com.beyond.easycheck.user.application.service.user.UserReadUseCase.FindUserResult;
import org.springframework.web.multipart.MultipartFile;

public interface UserOperationUseCase {

    FindUserResult registerUser(UserRegisterCommand command);

    FindUserResult registerCorporateUser(UserRegisterCommand command, CorporateCreateRequest corporateCreateRequest, MultipartFile verificationFilesZip);

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
