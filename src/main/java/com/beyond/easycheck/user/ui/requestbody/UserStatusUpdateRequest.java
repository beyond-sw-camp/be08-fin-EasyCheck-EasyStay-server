package com.beyond.easycheck.user.ui.requestbody;

import com.beyond.easycheck.user.application.domain.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
        @NotNull
        UserStatus status
) {
}
