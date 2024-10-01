package com.beyond.easycheck.permissions.ui.requestbody;

import jakarta.validation.constraints.NotNull;

public record PermissionGrantRequest(
        @NotNull Long granteeUserId,
        @NotNull Long permissionId
) {
}
