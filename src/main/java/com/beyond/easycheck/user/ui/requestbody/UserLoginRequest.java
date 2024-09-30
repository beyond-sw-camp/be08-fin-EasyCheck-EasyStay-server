package com.beyond.easycheck.user.ui.requestbody;

import jakarta.validation.constraints.NotEmpty;

public record UserLoginRequest(
        @NotEmpty
        String email,
        @NotEmpty
        String password
) {
}
