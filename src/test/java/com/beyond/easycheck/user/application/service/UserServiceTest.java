package com.beyond.easycheck.user.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerifiedEmailEntity;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerifiedEmailRepository;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.beyond.easycheck.user.application.service.UserOperationUseCase.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = {"test"})
class UserServiceTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserOperationUseCase userOperationUseCase;

    @Autowired
    private VerifiedEmailRepository verifiedEmailRepository;



    @Test
    @Transactional
    @DisplayName("[회원가입] - 성공")
    void registerUser_success() {
        // given
        UserRegisterCommand command = new UserRegisterCommand(
                "test@gmail.com",
                "1234",
                "hello",
                "010-1111-2222",
                "서울시",
                "동작구",
                'Y'
        );

        // 이메일 인증했다고 가정
        verifiedEmailRepository.save(VerifiedEmailEntity.createVerifiedEmail("test@gmail.com"));

        // when
        userOperationUseCase.registerUser(command);
        Optional<UserEntity> result = userJpaRepository.findUserEntityByEmail(command.email());
        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getPassword()).isNotEqualTo(command.password());
    }

    @Test
    @DisplayName("[회원가입] - 실패 - 중복된 이메일")
    void registerUser_fail_duplicated() {
        // given
        UserRegisterCommand command = new UserRegisterCommand(
                "john.doe@example.com",
                "1234",
                "hello",
                "010-1111-2222",
                "서울시",
                "동작구",
                'Y'
        );
        // when & then
        assertThatThrownBy(() -> userOperationUseCase.registerUser(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(UserMessageType.USER_ALREADY_REGISTERED.getMessage());
    }

    @Test
    @DisplayName("[회원가입] - 실패 - 인증되지 않은 이메일")
    void registerUser_fail_unauthorizedEmail() {
        // given
        UserRegisterCommand command = new UserRegisterCommand(
                "test@example.com",
                "1234",
                "hello",
                "010-1111-2222",
                "서울시",
                "동작구",
                'Y'
        );
        // when & then
        assertThatThrownBy(() -> userOperationUseCase.registerUser(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(UserMessageType.EMAIL_UNAUTHORIZED.getMessage());
    }
}