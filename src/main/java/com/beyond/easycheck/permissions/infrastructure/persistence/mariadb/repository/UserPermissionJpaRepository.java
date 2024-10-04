package com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.repository;

import com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.entity.PermissionEntity;
import com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.entity.UserPermissionEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserPermissionJpaRepository extends CrudRepository<UserPermissionEntity, Long> {

    boolean existsByUserAndPermission(UserEntity user, PermissionEntity permission);

    void deleteByPermissionId(Long permissionId);

    void deleteByUserAndPermission(UserEntity user, PermissionEntity permission);

}
