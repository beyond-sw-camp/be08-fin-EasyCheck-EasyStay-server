package com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.repository;

import com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.entity.PermissionEntity;
import org.springframework.data.repository.CrudRepository;

public interface PermissionJpaRepository extends CrudRepository<PermissionEntity, Long> {

    boolean existsByName(String name);
}
