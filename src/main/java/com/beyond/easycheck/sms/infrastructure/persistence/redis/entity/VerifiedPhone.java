package com.beyond.easycheck.sms.infrastructure.persistence.redis.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@ToString
@NoArgsConstructor
@RedisHash(value = "sms_verified_phone")
public class VerifiedPhone {
    @Id
    private String phone;

    @TimeToLive
    private Long ttl;

    private VerifiedPhone(String phone) {
        // 만료기간 10분
        this.ttl = 600L;
        this.phone = phone;
    }

    public static VerifiedPhone createVerifiedPhone(String phone) {
        return new VerifiedPhone(phone);
    }
}
