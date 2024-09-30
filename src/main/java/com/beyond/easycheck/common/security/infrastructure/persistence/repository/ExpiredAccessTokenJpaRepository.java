package com.beyond.easycheck.common.security.infrastructure.persistence.repository;

import com.beyond.easycheck.common.security.infrastructure.persistence.entity.ExpiredAccessToken;
import org.springframework.data.repository.CrudRepository;

public interface ExpiredAccessTokenJpaRepository extends CrudRepository<ExpiredAccessToken, String> {
}
