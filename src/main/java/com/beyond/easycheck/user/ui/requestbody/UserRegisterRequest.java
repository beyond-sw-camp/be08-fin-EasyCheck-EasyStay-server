package com.beyond.easycheck.user.ui.requestbody;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRegisterRequest(
        @Email @NotBlank
        String email,
        @NotBlank
        String password,
        @NotBlank
        String name,
        @NotBlank
        String phone,
        @NotBlank
        String addr,
        @NotBlank
        String addrDetail,
        @NotNull
        char marketingConsent
) {
}
