package com.beyond.easycheck.mail.infrastructure.persistence.redis.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor
@RedisHash(value = "verified_email")
public class VerifiedEmailEntity {

    @Id
    private String email;

    @TimeToLive
    private Long ttl;

    private VerifiedEmailEntity(String email) {
        // 만료시간 10분
        this.ttl = 600L;
        this.email = email;
    }

    public static VerifiedEmailEntity createVerifiedEmail(String email) {
        return new VerifiedEmailEntity(email);
    }

}
