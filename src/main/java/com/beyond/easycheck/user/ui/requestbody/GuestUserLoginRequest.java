package com.beyond.easycheck.user.ui.requestbody;

import jakarta.validation.constraints.NotBlank;

public record GuestUserLoginRequest(
        @NotBlank
        String name,
        @NotBlank
        String phone
) {
}
