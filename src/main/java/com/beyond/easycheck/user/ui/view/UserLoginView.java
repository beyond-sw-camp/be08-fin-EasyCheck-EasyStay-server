package com.beyond.easycheck.user.ui.view;

import com.beyond.easycheck.user.application.service.UserReadUseCase;
import lombok.Getter;
import lombok.ToString;

import static com.beyond.easycheck.user.application.service.UserReadUseCase.*;

@Getter
@ToString
public class UserLoginView {
    private final String accessToken;
    private final String refreshToken;

    public UserLoginView(FindJwtResult findJwtResult) {
        this.accessToken = findJwtResult.accessToken();
        this.refreshToken = findJwtResult.refreshToken();
    }

}
