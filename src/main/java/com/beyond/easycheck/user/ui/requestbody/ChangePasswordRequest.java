package com.beyond.easycheck.user.ui.requestbody;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank
        String email,
        @NotBlank
        String newPassword
) {
}
