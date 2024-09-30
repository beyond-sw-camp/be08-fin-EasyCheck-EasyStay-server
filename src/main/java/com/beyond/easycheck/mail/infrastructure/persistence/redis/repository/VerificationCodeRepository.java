package com.beyond.easycheck.mail.infrastructure.persistence.redis.repository;

import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerificationCodeEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends CrudRepository<VerificationCodeEntity, String> {
    Optional<VerificationCodeEntity> findByCode(String code);
}
