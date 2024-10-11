package com.beyond.easycheck.user.application.service.admin;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.user.application.service.user.UserReadUseCase;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.beyond.easycheck.user.application.service.user.UserReadUseCase.FindUserResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService implements AdminOperationUseCase, AdminReadUseCase {

    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public FindUserResult updateUserStatus(UserStatusUpdateCommand command) {

        UserEntity userEntity = userJpaRepository.findById(command.userId())
                .orElseThrow(() -> new EasyCheckException(UserMessageType.USER_NOT_FOUND));

        userEntity.setUserStatus(command.status());

        return FindUserResult.findByUserEntity(userEntity);
    }

    @Override
    public List<FindUserResult> getAllUsers(UserReadUseCase.UserFindQuery query) {
        return List.of();
    }

    @Override
    public FindUserResult getUserDetails(UserReadUseCase.UserFindQuery query) {
        return null;
    }
}
