package com.beyond.easycheck.mail.infrastructure.persistence.redis.repository;

import com.beyond.easycheck.mail.infrastructure.persistence.redis.entity.VerifiedEmailEntity;
import org.springframework.data.repository.CrudRepository;

public interface VerifiedEmailRepository extends CrudRepository<VerifiedEmailEntity, String> {
}
