package com.beyond.easycheck.sms.infrastructure.persistence.redis.repository;

import com.beyond.easycheck.sms.infrastructure.persistence.redis.entity.SmsVerificationCode;
import org.springframework.data.repository.CrudRepository;

public interface SmsVerificationCodeRepository extends CrudRepository<SmsVerificationCode, String> {
}
