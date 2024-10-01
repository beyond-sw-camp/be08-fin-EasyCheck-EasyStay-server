package com.beyond.easycheck.sms.infrastructure.persistence.redis.repository;

import com.beyond.easycheck.sms.infrastructure.persistence.redis.entity.VerifiedPhone;
import org.springframework.data.repository.CrudRepository;

public interface SmsVerifiedPhoneRepository extends CrudRepository<VerifiedPhone, String> {
}
