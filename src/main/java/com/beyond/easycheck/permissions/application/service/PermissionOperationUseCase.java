package com.beyond.easycheck.permissions.application.service;

public interface PermissionOperationUseCase {

    void createPermission(PermissionCreateCommand command);

    void grantPermission(PermissionGrantCommand command);

    void revokePermission(PermissionRevokeCommand command);

    void deletePermission(Long permissionId);

    record PermissionCreateCommand(
            String name,
            String description
    ) {

    }

    record PermissionGrantCommand(
        Long granteeUserId,
        Long grantorUserId,
        Long permissionId
    ) {
    }

    record PermissionRevokeCommand(
            Long targetUserId,
            Long permissionId
    ) {
    }

}
