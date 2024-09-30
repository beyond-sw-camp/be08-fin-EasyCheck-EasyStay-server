package com.beyond.easycheck.user.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.mail.infrastructure.persistence.redis.repository.VerifiedEmailRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserOperationUseCase {

    private final PasswordEncoder passwordEncoder;

    private final RoleJpaRepository roleJpaRepository;

    private final UserJpaRepository userJpaRepository;

    private final VerifiedEmailRepository verifiedEmailRepository;


    @Override
    @Transactional
    public void registerUser(UserRegisterCommand command) {
        log.info("[registerUser] - command = {}", command);
        checkEmailIsDuplicated(command);

        UserEntity user = UserEntity.createUser(command);
        log.info("[registerUser] - userEntity after createUser = {}", user);

        // 이메일 인증을 했는지 확인
        checkEmailIsVerified(command);

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

    private void checkEmailIsVerified(UserRegisterCommand command) {
        verifiedEmailRepository.findById(command.email())
                .orElseThrow(() -> new EasyCheckException(UserMessageType.EMAIL_UNAUTHORIZED));
    }

    private void checkEmailIsDuplicated(UserRegisterCommand command) {
        userJpaRepository.findUserEntityByEmail(command.email())
                .ifPresent(userEntity -> {
                    throw new EasyCheckException(UserMessageType.USER_ALREADY_REGISTERED);
                });
    }

    private RoleEntity retrieveRoleByName(String name) {
        return roleJpaRepository.findRoleEntityByName(name)
                .orElseThrow(() -> new EasyCheckException(UserMessageType.USER_ROLE_NOT_FOUND));
    }
}
