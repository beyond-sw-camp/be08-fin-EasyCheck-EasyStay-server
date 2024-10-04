package com.beyond.easycheck.permissions.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.permissions.exception.PermissionMessageType;
import com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.entity.PermissionEntity;
import com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.entity.UserPermissionEntity;
import com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.repository.PermissionJpaRepository;
import com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.repository.UserPermissionJpaRepository;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService implements PermissionOperationUseCase, PermissionReadUseCase {

    private final UserJpaRepository userJpaRepository;

    private final PermissionJpaRepository permissionJpaRepository;

    private final UserPermissionJpaRepository userPermissionJpaRepository;

    @Override
    @Transactional
    public void createPermission(PermissionCreateCommand command) {

        if (permissionJpaRepository.existsByName(command.name())) {
            throw new EasyCheckException(PermissionMessageType.PERMISSION_ALREADY_EXISTS);
        }

        PermissionEntity permissionEntity = PermissionEntity.createPermissionEntity(
                command.name(),
                command.description()
        );

        permissionJpaRepository.save(permissionEntity);
    }

    @Override
    @Transactional
    public void grantPermission(PermissionGrantCommand command) {

        UserEntity grantor = findUserByIdOrThrowNotFound(command.grantorUserId());
        UserEntity grantee = findUserByIdOrThrowNotFound(command.granteeUserId());
        
        PermissionEntity permission = permissionJpaRepository.findById(command.permissionId())
                .orElseThrow(() -> new EasyCheckException(PermissionMessageType.PERMISSION_NOT_FOUND));

        if (hasPermission(grantee, permission)) {
            throw new EasyCheckException(PermissionMessageType.PERMISSION_ALREADY_GRANTED);
        }

        UserPermissionEntity userPermissionEntity = UserPermissionEntity
                .grantPermission(grantor.getName(), grantee, permission);

        userPermissionJpaRepository.save(userPermissionEntity);
    }

    @Override
    @Transactional
    public void revokePermission(PermissionRevokeCommand command) {
        UserEntity targetUser = findUserByIdOrThrowNotFound(command.targetUserId());

        PermissionEntity permission = permissionJpaRepository.findById(command.permissionId())
                .orElseThrow(() -> new EasyCheckException(PermissionMessageType.PERMISSION_NOT_FOUND));

        if (!hasPermission(targetUser, permission)) {
            throw new EasyCheckException(PermissionMessageType.CANNOT_REVOKE_NONEXISTENT_PERMISSION);
        }

        userPermissionJpaRepository.deleteByUserAndPermission(targetUser, permission);
    }

    @Override
    @Transactional
    public void deletePermission(Long permissionId) {

        if (notExistPermission(permissionId)) {
            throw new EasyCheckException(PermissionMessageType.PERMISSION_NOT_FOUND);
        }

        // 먼저 해당 permission을 부여받은 기록 제거
        userPermissionJpaRepository.deleteByPermissionId(permissionId);

        permissionJpaRepository.deleteById(permissionId);
    }

    @Override
    public FindPermissionResult getAllPermission() {
        return null;
    }

    private boolean hasPermission(UserEntity grantTo, PermissionEntity permission) {
        return userPermissionJpaRepository.existsByUserAndPermission(grantTo, permission);
    }

    private boolean notExistPermission(Long permissionId) {
        return !permissionJpaRepository.existsById(permissionId);
    }

    private UserEntity findUserByIdOrThrowNotFound(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new EasyCheckException(UserMessageType.USER_NOT_FOUND));
    }
}
