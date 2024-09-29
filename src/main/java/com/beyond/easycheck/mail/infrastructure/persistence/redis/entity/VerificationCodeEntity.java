package com.beyond.easycheck.mail.infrastructure.persistence.redis.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "email_verification_code")
@NoArgsConstructor
public class VerificationCodeEntity {
    @Id
    private String email;

    @Indexed
    private String code;

    @TimeToLive
    private long ttl;

    private VerificationCodeEntity(String email, String code, long ttl) {
        this.email = email;
        this.code = code;
        this.ttl = ttl;
    }

    public static VerificationCodeEntity createVerificationCode(String email, String code, long ttl) {
        return new VerificationCodeEntity(email, code, ttl);
    }
}
