package com.beyond.easycheck.admin.application.service;

import com.beyond.easycheck.user.application.domain.UserStatus;
import com.beyond.easycheck.user.application.service.UserReadUseCase.FindUserResult;

public interface AdminOperationUseCase {

    FindUserResult updateUserStatus(UserStatusUpdateCommand command);

    record UserStatusUpdateCommand(
            Long userId,
            UserStatus status
    ) {
    }

}
