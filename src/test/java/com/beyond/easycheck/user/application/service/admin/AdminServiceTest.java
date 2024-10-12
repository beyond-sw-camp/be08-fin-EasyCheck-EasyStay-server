package com.beyond.easycheck.user.application.service.admin;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.user.application.domain.UserStatus;
import com.beyond.easycheck.user.application.service.user.UserReadUseCase;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.role.RoleEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.beyond.easycheck.user.application.service.admin.AdminOperationUseCase.UserStatusUpdateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    @DisplayName("[계정 상태 변경] - 성공")
    void updateStatus_success() {
        // given
        Long userId = 1L;
        UserStatusUpdateCommand command = new UserStatusUpdateCommand(userId, UserStatus.BANNED);
        UserEntity mockUser = mock(UserEntity.class);
        RoleEntity mockRole = mock(RoleEntity.class);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getStatus()).thenReturn(UserStatus.BANNED);
        when(mockUser.getRole()).thenReturn(mockRole);
        when(mockRole.getName()).thenReturn("USER");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getName()).thenReturn("Test User");

        // when
        UserReadUseCase.FindUserResult result = adminService.updateUserStatus(command);

        // then
        assertThat(result.status()).isEqualTo(UserStatus.BANNED.name());
        assertThat(result.role()).isEqualTo("USER");
        verify(mockUser).setUserStatus(UserStatus.BANNED);
    }

    @Test
    @DisplayName("[계정 상태 변경] - 실패 - 유저를 찾지 못함")
    void updateStatus_failed() {
        // given
        Long userId = 9999L;
        UserStatusUpdateCommand command = new UserStatusUpdateCommand(userId, UserStatus.BANNED);
        when(userJpaRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminService.updateUserStatus(command))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(UserMessageType.USER_NOT_FOUND.getMessage());
    }

    // getAllUsers와 getUserDetails 메서드에 대한 테스트를 추가할 수 있습니다.
    // 현재 이 메서드들은 구현되지 않았으므로, 구현 후 테스트를 작성하는 것이 좋습니다.
}