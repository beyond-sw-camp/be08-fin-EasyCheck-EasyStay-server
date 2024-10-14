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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private PermissionJpaRepository permissionJpaRepository;

    @Mock
    private UserPermissionJpaRepository userPermissionJpaRepository;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    @DisplayName("[권한생성] 성공")
    void createPermission_success() {
        // given
        PermissionOperationUseCase.PermissionCreateCommand command = new PermissionOperationUseCase.PermissionCreateCommand("NEW_PERMISSION", "새로운 권한");
        when(permissionJpaRepository.existsByName(anyString())).thenReturn(false);

        // when & then
        assertThatCode(() -> permissionService.createPermission(command))
                .doesNotThrowAnyException();
        verify(permissionJpaRepository).save(any(PermissionEntity.class));
    }

    @Test
    @DisplayName("[권한생성] 실패 - 중복된 권한")
    void createPermission_failedByDuplicated() {
        // given
        PermissionOperationUseCase.PermissionCreateCommand command = new PermissionOperationUseCase.PermissionCreateCommand("ADMIN_MANAGER", "관리자 권한");
        when(permissionJpaRepository.existsByName(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> permissionService.createPermission(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(PermissionMessageType.PERMISSION_ALREADY_EXISTS.getMessage());
        verify(permissionJpaRepository, never()).save(any(PermissionEntity.class));
    }

    @Test
    @DisplayName("[권한부여] 성공")
    void grantPermission_success() {
        // given
        Long grantorId = 4L;
        Long granteeId = 5L;
        Long permissionId = 7L;
        PermissionOperationUseCase.PermissionGrantCommand command = new PermissionOperationUseCase.PermissionGrantCommand(grantorId, granteeId, permissionId);

        UserEntity grantor = new UserEntity();
        UserEntity grantee = new UserEntity();
        PermissionEntity permission = new PermissionEntity();

        when(userJpaRepository.findById(grantorId)).thenReturn(Optional.of(grantor));
        when(userJpaRepository.findById(granteeId)).thenReturn(Optional.of(grantee));
        when(permissionJpaRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(userPermissionJpaRepository.existsByUserAndPermission(any(UserEntity.class), any(PermissionEntity.class))).thenReturn(false);

        // when & then
        assertThatCode(() -> permissionService.grantPermission(command))
                .doesNotThrowAnyException();

        verify(userPermissionJpaRepository).save(any(UserPermissionEntity.class));
    }
    @Test
    @DisplayName("[권한부여] 실패 - grantee 존재하지 않음")
    void grantPermission_failedByGranteeNotFound() {
        // given
        PermissionOperationUseCase.PermissionGrantCommand command = new PermissionOperationUseCase.PermissionGrantCommand(4L, 999999L, 7L);
        when(userJpaRepository.findById(999999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> permissionService.grantPermission(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(UserMessageType.USER_NOT_FOUND.getMessage());
        verify(userPermissionJpaRepository, never()).save(any(UserPermissionEntity.class));
    }

    @Test
    @DisplayName("[권한회수] 성공")
    void revokePermission_success() {
        // given
        PermissionOperationUseCase.PermissionRevokeCommand command = new PermissionOperationUseCase.PermissionRevokeCommand(5L, 1L);
        UserEntity user = mock(UserEntity.class);
        PermissionEntity permission = mock(PermissionEntity.class);
        when(userJpaRepository.findById(5L)).thenReturn(Optional.of(user));
        when(permissionJpaRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(userPermissionJpaRepository.existsByUserAndPermission(user, permission)).thenReturn(true);

        // when & then
        assertThatCode(() -> permissionService.revokePermission(command))
                .doesNotThrowAnyException();
        verify(userPermissionJpaRepository).deleteByUserAndPermission(user, permission);
    }

    @Test
    @DisplayName("[권한회수] 실패 - 존재하지 않는 권한")
    void revokePermission_failedByPermissionNotFound() {
        // given
        PermissionOperationUseCase.PermissionRevokeCommand command = new PermissionOperationUseCase.PermissionRevokeCommand(4L, 999999L);
        when(userJpaRepository.findById(4L)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(permissionJpaRepository.findById(999999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> permissionService.revokePermission(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(PermissionMessageType.PERMISSION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("[권한삭제] 성공")
    void deletePermission_success() {
        // given
        Long permissionId = 1L;
        when(permissionJpaRepository.existsById(permissionId)).thenReturn(true);

        // when & then
        assertThatCode(() -> permissionService.deletePermission(permissionId))
                .doesNotThrowAnyException();
        verify(userPermissionJpaRepository).deleteByPermissionId(permissionId);
        verify(permissionJpaRepository).deleteById(permissionId);
    }

    @Test
    @DisplayName("[권한삭제] 실패 - 존재하지 않은 권한")
    void deletePermission_failedByPermissionNotFound() {
        // given
        Long notExistPermissionId = 9999999L;
        when(permissionJpaRepository.existsById(notExistPermissionId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> permissionService.deletePermission(notExistPermissionId))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(PermissionMessageType.PERMISSION_NOT_FOUND.getMessage());
        verify(userPermissionJpaRepository, never()).deleteByPermissionId(anyLong());
        verify(permissionJpaRepository, never()).deleteById(anyLong());
    }
}