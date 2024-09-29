package com.beyond.easycheck.user.application.service;

public interface UserOperationUseCase {


    void registerUser(UserRegisterCommand command);

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
}
