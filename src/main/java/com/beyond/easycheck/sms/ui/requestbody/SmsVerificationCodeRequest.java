package com.beyond.easycheck.sms.ui.requestbody;

import jakarta.validation.constraints.NotBlank;

public record SmsVerificationCodeRequest(
        @NotBlank
        String receivingPhoneNumber
) {
}
