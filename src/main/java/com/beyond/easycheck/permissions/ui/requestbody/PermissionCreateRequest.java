package com.beyond.easycheck.permissions.ui.requestbody;

import jakarta.validation.constraints.NotBlank;

public record PermissionCreateRequest(
        @NotBlank
        String name,
        @NotBlank
        String description
) {
}
