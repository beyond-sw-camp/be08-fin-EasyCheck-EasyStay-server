package com.beyond.easycheck.corporate.ui.requestbody;

public record CorporateCreateRequest(
        Long userId,
        String name,
        String businessLicenseNumber
) {
}
