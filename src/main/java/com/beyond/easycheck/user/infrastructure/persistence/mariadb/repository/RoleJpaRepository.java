package com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository;

import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.role.RoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleJpaRepository extends CrudRepository<RoleEntity, Long> {

    Optional<RoleEntity> findRoleEntityByName(String name);

}
