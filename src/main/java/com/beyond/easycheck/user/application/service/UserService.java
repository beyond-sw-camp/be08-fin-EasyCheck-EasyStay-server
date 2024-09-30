package com.beyond.easycheck.user.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.common.security.infrastructure.persistence.entity.ExpiredAccessToken;
import com.beyond.easycheck.common.security.infrastructure.persistence.repository.ExpiredAccessTokenJpaRepository;
import com.beyond.easycheck.common.security.utils.JwtUtil;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerifiedEmailRepository;
import com.beyond.easycheck.user.application.domain.EasyCheckUserDetails;
import com.beyond.easycheck.user.application.domain.UserRole;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.role.RoleEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.RoleJpaRepository;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.beyond.easycheck.user.application.service.UserReadUseCase.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserOperationUseCase {

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    private final RoleJpaRepository roleJpaRepository;

    private final UserJpaRepository userJpaRepository;

    private final VerifiedEmailRepository verifiedEmailRepository;
    private final ExpiredAccessTokenJpaRepository expiredAccessTokenJpaRepository;


    @Override
    @Transactional
    public void registerUser(UserRegisterCommand command) {
        log.info("[registerUser] - command = {}", command);
        // 회원가입 전 이메일 인증 과정을 거쳐야 한다.
        checkEmailIsDuplicated(command);

        UserEntity user = UserEntity.createUser(command);
        log.info("[registerUser] - userEntity after createUser = {}", user);

        // 이메일 인증을 했는지 확인
        checkEmailIsVerified(command.email());

        // 회원 저장하기 전에 비밀번호 암호화
        String securePassword = passwordEncoder.encode(command.password());
        user.setSecurePassword(securePassword);

        // 회원의 역할 설정
        RoleEntity role = retrieveRoleByName(UserRole.USER.name());
        user.setRole(role);

        // 회원 저장
        UserEntity result = userJpaRepository.save(user);
        log.info("[registerUser] - userEntity save result = {}", result);
    }

    @Override
    public FindJwtResult login(UserLoginCommand command) {

        log.info("[login] - login command = {}", command);
        UserEntity user = retrieveUserByEmail(command.email());

        log.info("[login] - user find result = {}", user);
        if (passwordIncorrect(command, user)) {
            throw new EasyCheckException(UserMessageType.USER_NOT_FOUND);
        }

        EasyCheckUserDetails userDetails = new EasyCheckUserDetails(user);
        log.info("[login] - user details = {}", userDetails);

        return generateJwt(userDetails);
    }

    @Override
    @Transactional
    public void logout(UserLogoutCommand command) {

        // 현재 로그아웃 하는 accessToken 만료 토큰으로 등록
        ExpiredAccessToken expiredAccessToken = ExpiredAccessToken.createExpiredAccessToken(
                command.accessToken()
        );

        expiredAccessTokenJpaRepository.save(expiredAccessToken);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordCommand command) {

        // 비밀번호 변경 전 이메일 인증 과정을 거쳐야 한다.
        checkEmailIsVerified(command.email());

        UserEntity user = retrieveUserByEmail(command.email());

        String newSecurePassword = passwordEncoder.encode(command.newPassword());
        user.setSecurePassword(newSecurePassword);

    }

    private FindJwtResult generateJwt(EasyCheckUserDetails userDetails) {
        return FindJwtResult.findByTokenString(
                jwtUtil.createAccessToken(userDetails),
                jwtUtil.createRefreshToken(userDetails)
        );
    }

    private boolean passwordIncorrect(UserLoginCommand command, UserEntity user) {
        log.info(user.getPassword());
        return !passwordEncoder.matches(command.password(), user.getPassword());
    }

    private void checkEmailIsVerified(String email) {
        verifiedEmailRepository.findById(email)
                .orElseThrow(() -> new EasyCheckException(UserMessageType.EMAIL_UNAUTHORIZED));
    }

    private void checkEmailIsDuplicated(UserRegisterCommand command) {
        userJpaRepository.findUserEntityByEmail(command.email())
                .ifPresent(userEntity -> {
                    throw new EasyCheckException(UserMessageType.USER_ALREADY_REGISTERED);
                });
    }


    private UserEntity retrieveUserByEmail(String email) {
        return userJpaRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new EasyCheckException(UserMessageType.USER_NOT_FOUND));
    }

    private RoleEntity retrieveRoleByName(String name) {
        return roleJpaRepository.findRoleEntityByName(name)
                .orElseThrow(() -> new EasyCheckException(UserMessageType.USER_ROLE_NOT_FOUND));
    }
}
