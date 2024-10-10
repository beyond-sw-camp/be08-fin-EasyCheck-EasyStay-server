package com.beyond.easycheck.user.application.service.admin;

import com.beyond.easycheck.user.application.domain.UserStatus;
import com.beyond.easycheck.user.application.service.user.UserReadUseCase.FindUserResult;

public interface AdminOperationUseCase {

    FindUserResult updateUserStatus(UserStatusUpdateCommand command);

    record UserStatusUpdateCommand(
            Long userId,
            UserStatus status
    ) {
    }

}
