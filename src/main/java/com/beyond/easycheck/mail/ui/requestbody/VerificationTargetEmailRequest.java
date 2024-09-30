package com.beyond.easycheck.mail.ui.requestbody;

import jakarta.validation.constraints.NotEmpty;

public record VerificationTargetEmailRequest(
        @NotEmpty
        String targetEmail
) {
}
