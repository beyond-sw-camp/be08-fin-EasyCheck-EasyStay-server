package com.beyond.easycheck.permissions.ui.requestbody;

import jakarta.validation.constraints.NotNull;

public record PermissionRevokeRequest(
        @NotNull Long targetUserId,
        @NotNull Long permissionId
) {
}
