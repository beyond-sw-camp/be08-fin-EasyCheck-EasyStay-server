package com.beyond.easycheck.permissions.application.service;

import com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.entity.PermissionEntity;

public interface PermissionReadUseCase {

    FindPermissionResult getAllPermission();

    record FindPermissionResult(
            Long permissionId,
            String name,
            String description
    ) {
        public static FindPermissionResult findByPermissionEntity(PermissionEntity permission) {
            return new FindPermissionResult(permission.getId(), permission.getName(), permission.getDescription());
        }
    }
}
