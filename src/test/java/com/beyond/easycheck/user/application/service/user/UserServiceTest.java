package com.beyond.easycheck.user.application.service.user;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.common.security.utils.JwtUtil;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerifiedEmailEntity;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerifiedEmailRepository;
import com.beyond.easycheck.sms.infrastructure.persistence.redis.entity.VerifiedPhone;
import com.beyond.easycheck.sms.infrastructure.persistence.redis.repository.SmsVerifiedPhoneRepository;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.RoleJpaRepository;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.beyond.easycheck.user.application.service.user.UserOperationUseCase.*;
import static com.beyond.easycheck.user.application.service.user.UserReadUseCase.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = {"test"})
class UserServiceTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserReadUseCase userReadUseCase;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private RoleJpaRepository roleJpaRepository;

    @Autowired
    private UserOperationUseCase userOperationUseCase;

    @Autowired
    private VerifiedEmailRepository verifiedEmailRepository;

    @Autowired
    private SmsVerifiedPhoneRepository smsVerifiedPhoneRepository;

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }

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
        // 휴대폰 인증했다고 가정
        smsVerifiedPhoneRepository.save(VerifiedPhone.createVerifiedPhone(command.phone()));

        // when
        userOperationUseCase.registerUser(command);
        Optional<UserEntity> result = userJpaRepository.findUserEntityByEmail(command.email());
        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getPassword()).isNotEqualTo(command.password());
    }

    @Test
    @Transactional
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
                .hasMessage(UserMessageType.USER_ALREADY_EXISTS.getMessage());
    }

    @Test
    @Transactional
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
                .hasMessage(UserMessageType.EMAIL_NOT_VERIFIED.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("[회원가입] - 실패 - 인증되지 휴대폰")
    void registerUser_fail_unauthorizedPhone() {
        // given
        UserRegisterCommand command = new UserRegisterCommand(
                "test@example.com",
                "1234",
                "hello",
                "010-2222-3333",
                "서울시",
                "동작구",
                'Y'
        );
        // 이메일 인증했다고 가정
        verifiedEmailRepository.save(VerifiedEmailEntity.createVerifiedEmail(command.email()));

        // when & then
        assertThatThrownBy(() -> userOperationUseCase.registerUser(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(UserMessageType.PHONE_NOT_VERIFIED.getMessage());
    }

    @Test
    @Transactional
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
        assertThat(jwtUtil.parseUserId(result.accessToken())).isEqualTo(user.getId());
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

    @Test
    @Transactional
    @DisplayName("[비밀번호 변경] - 성공")
    void changePassword() {
        // given
        final String email = "john.doe@example.com";
        final String oldPassword = "password123";
        final String newPassword = "qwer123";

        verifiedEmailRepository.save(VerifiedEmailEntity.createVerifiedEmail(email));

        ChangePasswordCommand command = new ChangePasswordCommand(email, oldPassword, newPassword);

        // when
        assertThatCode(() -> userOperationUseCase.changePassword(command))
                .doesNotThrowAnyException();
        Optional<UserEntity> userEntity = userJpaRepository.findUserEntityByEmail(email);
        // then
        assertThat(userEntity.isPresent()).isTrue();
        assertThat(passwordEncoder.matches(newPassword, userEntity.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("[유저 정보 불러오기] - 성공")
    void getUserInfo_success() {
        // given
        UserFindQuery query = UserFindQuery.builder()
                .userId(1L)
                .build();
        // when
        FindUserResult result = userReadUseCase.getUserInfo(query);
        // then
        assertThat(result.role()).isEqualTo("USER");
    }



}