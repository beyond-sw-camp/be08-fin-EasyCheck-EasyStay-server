package com.beyond.easycheck.permissions.ui.controller;

import com.beyond.easycheck.permissions.exception.PermissionMessageType;
import com.beyond.easycheck.permissions.ui.requestbody.PermissionCreateRequest;
import com.beyond.easycheck.permissions.ui.requestbody.PermissionGrantRequest;
import com.beyond.easycheck.permissions.ui.requestbody.PermissionRevokeRequest;
import com.beyond.easycheck.user.application.mock.WithEasyCheckMockUser;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PermissionControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("[권한생성 요청] 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createPermission_success() throws Exception {
        // given
        PermissionCreateRequest request = new PermissionCreateRequest("NEW", "테스트용 새로운거");
        // when
        ResultActions perform = mvc.perform(
                post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        // then
        perform.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[권한생성 요청] 실패 - 권한없음")
    @WithEasyCheckMockUser(role = "GUEST")
    void createPermission_failedByUnauthorized() throws Exception {
        // given
        PermissionCreateRequest request = new PermissionCreateRequest("NEW", "테스트용 새로운거");
        // when
        ResultActions perform = mvc.perform(
                post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        // then
        perform.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[권한생성 요청] 실패 - 이미 존재하는 권한")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createPermission_failedByConflict() throws Exception {
        // given
        PermissionCreateRequest request = new PermissionCreateRequest("THEME_PARK_CREATE", "테스트용 새로운거");
        // when
        ResultActions perform = mvc.perform(
                post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        // then
        perform.andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].errorType").value(PermissionMessageType.PERMISSION_ALREADY_EXISTS.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(PermissionMessageType.PERMISSION_ALREADY_EXISTS.getMessage()));

    }

    @Test
    @DisplayName("[권한부여 요청] 성공")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void grantPermission_success() throws Exception {
        // given
        PermissionGrantRequest command = new PermissionGrantRequest(4L, 7L);
        // when
        ResultActions perform = mvc.perform(
                patch("/api/v1/permissions/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        );
        // then
        perform.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[권한부여 요청] 실패 - 권한 없음 게스트")
    @WithEasyCheckMockUser(id = 5L, role = "GUEST")
    void grantPermission_failedWithForbiddenFromGuest() throws Exception {
        // given
        PermissionGrantRequest command = new PermissionGrantRequest(4L, 7L);
        // when
        ResultActions perform = mvc.perform(
                patch("/api/v1/permissions/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        );
        // then
        perform.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[권한부여 요청] 실패 - 권한 없음 유저")
    @WithEasyCheckMockUser
    void grantPermission_failedWithForbiddenFromUser() throws Exception {
        // given
        PermissionGrantRequest command = new PermissionGrantRequest(4L, 7L);
        // when
        ResultActions perform = mvc.perform(
                patch("/api/v1/permissions/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        );
        // then
        perform.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[권한부여 요청] 실패 - 권한 없음 일반 관리자 ")
    @WithEasyCheckMockUser(id = 4L, role = "ADMIN")
    void grantPermission_failedWithForbiddenFromAdmin() throws Exception {
        // given
        PermissionGrantRequest command = new PermissionGrantRequest(4L, 7L);
        // when
        ResultActions perform = mvc.perform(
                patch("/api/v1/permissions/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        );
        // then
        perform.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[권한부여 요청] 실패 - 존재하지 않은 최종 관리자 ")
    @WithEasyCheckMockUser(id = 100L, role = "SUPER_ADMIN")
    void grantPermission_failedWithSuperAdminNotFound() throws Exception {
        // given
        PermissionGrantRequest command = new PermissionGrantRequest(4L, 7L);
        // when
        ResultActions perform = mvc.perform(
                patch("/api/v1/permissions/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        );
        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(UserMessageType.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(UserMessageType.USER_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("[권한부여 요청] 실패 - 존재하지 않은 관리자")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void grantPermission_failedWithGranteeNotFound() throws Exception {
        // given
        PermissionGrantRequest command = new PermissionGrantRequest(100L, 7L);
        // when
        ResultActions perform = mvc.perform(
                patch("/api/v1/permissions/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        );
        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(UserMessageType.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(UserMessageType.USER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("[권한부여 요청] 실패 - 이미 부여받은 권한")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void grantPermission_permissionConflict() throws Exception {
        // given
        PermissionGrantRequest command = new PermissionGrantRequest(4L, 2L);
        // when
        ResultActions perform = mvc.perform(
                patch("/api/v1/permissions/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        );
        // then
        perform.andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].errorType").value(PermissionMessageType.PERMISSION_ALREADY_GRANTED.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(PermissionMessageType.PERMISSION_ALREADY_GRANTED.getMessage()));
    }

    @Test
    @DisplayName("[권한부여 요청] 실패 - 존재하지 않은 권한")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void grantPermission_permissionNotfound() throws Exception {
        // given
        PermissionGrantRequest command = new PermissionGrantRequest(4L, 10000L);
        // when
        ResultActions perform = mvc.perform(
                patch("/api/v1/permissions/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        );
        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(PermissionMessageType.PERMISSION_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(PermissionMessageType.PERMISSION_NOT_FOUND.getMessage()));
    }


    @Test
    @DisplayName("[권한회수 요청] 성공")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void revokePermission_success() throws Exception {
        // given
        PermissionRevokeRequest request = new PermissionRevokeRequest(4L, 2L);
        // when
        ResultActions perform = mvc.perform(
                patch("/api/v1/permissions/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        // then
        perform.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[권한회수 요청] 실패 존재하지 않은 유저")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void revokePermission_failedWithUserNotFound() throws Exception {
        // given
        PermissionRevokeRequest request = new PermissionRevokeRequest(10000L, 2L);
        // when
        ResultActions perform = mvc.perform(
                patch("/api/v1/permissions/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        // then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(UserMessageType.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(UserMessageType.USER_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("[권한회수 요청] 실패 부여받지 않은 권한 회수 요청")
    @WithEasyCheckMockUser(id = 5L, role = "SUPER_ADMIN")
    void revokePermission_failedWithPermissionNotfound() throws Exception {
        // given
        PermissionRevokeRequest request = new PermissionRevokeRequest(4L, 4L);
        // when
        ResultActions perform = mvc.perform(
                patch("/api/v1/permissions/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        // then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorType").value(PermissionMessageType.CANNOT_REVOKE_NONEXISTENT_PERMISSION.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(PermissionMessageType.CANNOT_REVOKE_NONEXISTENT_PERMISSION.getMessage()));
    }

}