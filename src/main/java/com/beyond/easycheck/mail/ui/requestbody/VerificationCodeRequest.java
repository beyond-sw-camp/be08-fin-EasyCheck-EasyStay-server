package com.beyond.easycheck.mail.ui.requestbody;

import jakarta.validation.constraints.NotEmpty;

public record VerificationCodeRequest(
        @NotEmpty
        String code
) {
}
