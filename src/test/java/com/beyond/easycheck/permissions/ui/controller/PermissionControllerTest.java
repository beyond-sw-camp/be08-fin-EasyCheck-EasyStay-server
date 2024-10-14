package com.beyond.easycheck.permissions.ui.controller;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.permissions.application.service.PermissionOperationUseCase;
import com.beyond.easycheck.permissions.application.service.PermissionService;
import com.beyond.easycheck.permissions.exception.PermissionMessageType;
import com.beyond.easycheck.permissions.ui.requestbody.PermissionCreateRequest;
import com.beyond.easycheck.permissions.ui.requestbody.PermissionGrantRequest;
import com.beyond.easycheck.permissions.ui.requestbody.PermissionRevokeRequest;
import com.beyond.easycheck.user.application.mock.WithEasyCheckMockUser;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PermissionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PermissionService permissionService;

    @Test
    @DisplayName("[권한생성 요청] 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createPermission_success() throws Exception {
        PermissionCreateRequest request = new PermissionCreateRequest("NEW", "테스트용 새로운거");

        doNothing().when(permissionService).createPermission(any());

        mvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(permissionService).createPermission(any());
    }

    @Test
    @DisplayName("[권한생성 요청] 실패 - 권한없음")
    @WithEasyCheckMockUser(role = "GUEST")
    void createPermission_failedByUnauthorized() throws Exception {
        PermissionCreateRequest request = new PermissionCreateRequest("NEW", "테스트용 새로운거");

        mvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(permissionService, never()).createPermission(any());
    }

    @Test
    @DisplayName("[권한생성 요청] 실패 - 이미 존재하는 권한")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createPermission_failedByConflict() throws Exception {
        PermissionCreateRequest request = new PermissionCreateRequest("THEME_PARK_CREATE", "테스트용 새로운거");

        doThrow(new EasyCheckException(PermissionMessageType.PERMISSION_ALREADY_EXISTS))
                .when(permissionService).createPermission(any());

        mvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].errorType").value(PermissionMessageType.PERMISSION_ALREADY_EXISTS.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(PermissionMessageType.PERMISSION_ALREADY_EXISTS.getMessage()));

        verify(permissionService).createPermission(any());
    }

    @Test
    @DisplayName("[권한부여 요청] 성공")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void grantPermission_success() throws Exception {
        PermissionGrantRequest command = new PermissionGrantRequest(4L, 7L);

        doNothing().when(permissionService).grantPermission(any());

        mvc.perform(patch("/api/v1/permissions/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());

        verify(permissionService).grantPermission(any());
    }

    @Test
    @DisplayName("[권한부여 요청] 실패 - 권한 없음")
    @WithEasyCheckMockUser(id = 5L, role = "GUEST")
    void grantPermission_failedWithForbidden() throws Exception {
        PermissionGrantRequest command = new PermissionGrantRequest(4L, 7L);

        mvc.perform(patch("/api/v1/permissions/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isForbidden());

        verify(permissionService, never()).grantPermission(any());
    }

    @Test
    @DisplayName("[권한부여 요청] 실패 - 존재하지 않은 사용자")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void grantPermission_failedWithUserNotFound() throws Exception {
        PermissionGrantRequest command = new PermissionGrantRequest(100L, 7L);

        doThrow(new EasyCheckException(UserMessageType.USER_NOT_FOUND))
                .when(permissionService).grantPermission(any());

        mvc.perform(patch("/api/v1/permissions/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(UserMessageType.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(UserMessageType.USER_NOT_FOUND.getMessage()));

        verify(permissionService).grantPermission(any());
    }

    @Test
    @DisplayName("[권한회수 요청] 성공")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void revokePermission_success() throws Exception {
        PermissionRevokeRequest request = new PermissionRevokeRequest(4L, 2L);

        doNothing().when(permissionService).revokePermission(any());

        mvc.perform(patch("/api/v1/permissions/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(permissionService).revokePermission(any());
    }

    @Test
    @DisplayName("[권한회수 요청] 실패 - 존재하지 않은 유저")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void revokePermission_failedWithUserNotFound() throws Exception {
        PermissionRevokeRequest request = new PermissionRevokeRequest(10000L, 2L);

        doThrow(new EasyCheckException(UserMessageType.USER_NOT_FOUND))
                .when(permissionService).revokePermission(any());

        mvc.perform(patch("/api/v1/permissions/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(UserMessageType.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(UserMessageType.USER_NOT_FOUND.getMessage()));

        verify(permissionService).revokePermission(any());
    }

    @Test
    @DisplayName("[권한회수 요청] 실패 - 부여받지 않은 권한 회수 요청")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void revokePermission_failedWithPermissionNotFound() throws Exception {
        PermissionRevokeRequest request = new PermissionRevokeRequest(4L, 4L);

        doThrow(new EasyCheckException(PermissionMessageType.CANNOT_REVOKE_NONEXISTENT_PERMISSION))
                .when(permissionService).revokePermission(any());

        mvc.perform(patch("/api/v1/permissions/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorType").value(PermissionMessageType.CANNOT_REVOKE_NONEXISTENT_PERMISSION.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(PermissionMessageType.CANNOT_REVOKE_NONEXISTENT_PERMISSION.getMessage()));

        verify(permissionService).revokePermission(any());
    }
}