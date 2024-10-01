package com.beyond.easycheck.sms.ui.requestbody;

import jakarta.validation.constraints.NotEmpty;

public record SmsCodeVerifyRequest(
        @NotEmpty
        String phone,
        @NotEmpty
        String code
) {
}
