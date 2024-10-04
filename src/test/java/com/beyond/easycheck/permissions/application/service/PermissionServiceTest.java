package com.beyond.easycheck.permissions.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.permissions.application.service.PermissionOperationUseCase.PermissionCreateCommand;
import com.beyond.easycheck.permissions.exception.PermissionMessageType;
import com.beyond.easycheck.user.exception.UserMessageType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.beyond.easycheck.permissions.application.service.PermissionOperationUseCase.PermissionGrantCommand;
import static com.beyond.easycheck.permissions.application.service.PermissionOperationUseCase.PermissionRevokeCommand;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class PermissionServiceTest {

    @Autowired
    PermissionOperationUseCase permissionOperationUseCase;

    @Autowired
    PermissionReadUseCase permissionReadUseCase;

    @Test
    @DisplayName("[권한생성] 성공")
    void createPermission_success() {
        // given
        PermissionCreateCommand command = new PermissionCreateCommand(
                "NEW_PERMISSION",
                "새로운 권한"
        );
        // when & then
        assertThatCode(() -> permissionOperationUseCase.createPermission(command))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("[권한생성] 실패 - 중복된 권한")
    void createPermission_failedByDuplicated() {
        // given
        PermissionCreateCommand command = new PermissionCreateCommand(
                "ADMIN_MANAGER",
                "관리자 권한"
        );
        // when & then
        assertThatThrownBy(() -> permissionOperationUseCase.createPermission(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(PermissionMessageType.PERMISSION_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("[권한부여] 성공")
    void grantPermission_success() {
        // given
        PermissionGrantCommand command = new PermissionGrantCommand(4L, 5L, 7L);
        // when & then
        assertThatCode(() -> permissionOperationUseCase.grantPermission(command))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("[권한부여] 실패 - grantee 존재하지 않음")
    void grantPermission_failedByGranteeNotFound() {
        // given
        final Long notExistUser = 999999L;
        PermissionGrantCommand command = new PermissionGrantCommand(notExistUser, 5L, 7L);
        // when & then
        assertThatThrownBy(() -> permissionOperationUseCase.grantPermission(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(UserMessageType.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("[권한부여] 실패 - grantor 존재하지 않음")
    void grantPermission_failedByGrantorNotFound() {
        // given
        final Long notExistUser = 999999L;
        PermissionGrantCommand command = new PermissionGrantCommand(4L, notExistUser, 7L);
        // when & then
        assertThatThrownBy(() -> permissionOperationUseCase.grantPermission(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(UserMessageType.USER_NOT_FOUND.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("[권한회수] 성공")
    void revokePermission_success() {
        // given
        PermissionRevokeCommand command = new PermissionRevokeCommand(5L, 1L);
        // when & then
        assertThatCode(() -> permissionOperationUseCase.revokePermission(command))
                .doesNotThrowAnyException();

    }

    @Test
    @DisplayName("[권한회수] 실패 - 존재하지 않는 권한")
    void revokePermission_failedByPermissionNotFound() {
        // given
        final Long notExistPermissionId = 999999L;
        PermissionRevokeCommand command = new PermissionRevokeCommand(4L, notExistPermissionId);
        // when
        assertThatThrownBy(() -> permissionOperationUseCase.revokePermission(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(PermissionMessageType.PERMISSION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("[권한회수] 실패 - 존재하지 않는 권한")
    void revokePermission_failedByNoneExistencePermission() {
        // given
        final Long notExistPermissionId = 999999L;
        PermissionRevokeCommand command = new PermissionRevokeCommand(4L, 1L);
        // when
        assertThatThrownBy(() -> permissionOperationUseCase.revokePermission(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(PermissionMessageType.CANNOT_REVOKE_NONEXISTENT_PERMISSION.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("[권한삭제] 성공")
    void deletePermission_success() {
        // given
        final Long permissionId = 1L;
        // when & then
        assertThatCode(() -> permissionOperationUseCase.deletePermission(permissionId))
                .doesNotThrowAnyException();

    }

    @Test
    @DisplayName("[권한삭제] 실패 - 존재하지 않은 권한")
    void deletePermission_failedByPermissionNotFound() {
        // given
        final Long notExistPermissionId = 9999999L;
        // when & then
        assertThatThrownBy(() -> permissionOperationUseCase.deletePermission(notExistPermissionId))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(PermissionMessageType.PERMISSION_NOT_FOUND.getMessage());
    }

}