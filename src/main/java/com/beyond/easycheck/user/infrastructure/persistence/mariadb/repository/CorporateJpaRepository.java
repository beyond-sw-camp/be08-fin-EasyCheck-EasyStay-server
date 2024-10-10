package com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository;

import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.corporate.CorporateEntity;
import org.springframework.data.repository.CrudRepository;

public interface CorporateJpaRepository extends CrudRepository<CorporateEntity, Long> {
}
