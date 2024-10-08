package com.beyond.easycheck.corporate.ui.requestbody;

import jakarta.validation.constraints.NotBlank;

public record CorporateCreateRequest(
        @NotBlank
        String name,
        @NotBlank
        String businessLicenseNumber
) {
}
