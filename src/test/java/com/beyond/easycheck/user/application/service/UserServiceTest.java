package com.beyond.easycheck.user.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.common.security.utils.JwtUtil;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerifiedEmailEntity;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerifiedEmailRepository;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.RoleJpaRepository;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.beyond.easycheck.user.application.service.UserOperationUseCase.*;
import static com.beyond.easycheck.user.application.service.UserReadUseCase.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = {"test"})
class UserServiceTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private RoleJpaRepository roleJpaRepository;

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

    @Test
    @DisplayName("[로그인] - 성공")
    void login_success() {
        // given
        String encodedPassword = passwordEncoder.encode("password123");
        UserEntity user = UserEntity.createUser(new UserRegisterCommand(
                "test@example.com",
                "password123",
                "Test User",
                "010-1234-5678",
                "서울시 강남구",
                "테헤란로 123",
                'N'
        ));
        user.setRole(roleJpaRepository.findRoleEntityByName("USER").get());
        user.setSecurePassword(encodedPassword);

        userJpaRepository.save(user);

        UserLoginCommand command = new UserLoginCommand("test@example.com", "password123");
        // when
        FindJwtResult result = userOperationUseCase.login(command);

        // then
        assertThat(result.accessToken()).isNotNull();
        assertThat(result.refreshToken()).isNotNull();

        assertThat(jwtUtil.parseEmail(result.accessToken())).isEqualTo(user.getEmail());
        assertThat(Long.parseLong(jwtUtil.parseUserId(result.accessToken()))).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("[로그인] - 실패 - 유저를 찾을 수 없음")
    void login_failedWithNotFound() {
        // given
        UserLoginCommand command = new UserLoginCommand("notExist", "password123");
        // when & then
        assertThatThrownBy(() -> userOperationUseCase.login(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(UserMessageType.USER_NOT_FOUND.getMessage());

    }


}