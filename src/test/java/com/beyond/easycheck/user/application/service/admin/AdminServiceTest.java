package com.beyond.easycheck.user.application.service.admin;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.user.application.domain.UserStatus;
import com.beyond.easycheck.user.application.service.user.UserReadUseCase;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.beyond.easycheck.user.application.service.admin.AdminOperationUseCase.UserStatusUpdateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class AdminServiceTest {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    AdminOperationUseCase adminOperationUseCase;

    @Test
    @Transactional
    @DisplayName("[계정 상태 변경] - 성공")
    void updateStatus_success() {
        // given
        UserStatusUpdateCommand command = new UserStatusUpdateCommand(1L, UserStatus.BANNED);
        // when
        UserReadUseCase.FindUserResult result = adminOperationUseCase.updateUserStatus(command);
        Optional<UserEntity> userEntity = userJpaRepository.findById(result.id());
        // then
        assertThat(result.status()).isEqualTo(UserStatus.BANNED.name());
        assertThat(userEntity.isPresent()).isTrue();
        assertThat(userEntity.get().getStatus()).isEqualTo(UserStatus.BANNED);
    }

    @Test
    @Transactional
    @DisplayName("[계정 상태 변경] - 실패 - 유저를 찾지 못함")
    void updateStatus_failed() {
        // given
        UserStatusUpdateCommand command = new UserStatusUpdateCommand(9999L, UserStatus.BANNED);
        // when & then
        assertThatThrownBy(() -> adminOperationUseCase.updateUserStatus(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(UserMessageType.USER_NOT_FOUND.getMessage());
    }
}
