package com.beyond.easycheck.sms.infrastructure.persistence.redis.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@ToString
@NoArgsConstructor
@RedisHash(value = "sms_verified_phone")
public class VerifiedPhone {
    @Id
    private String phone;

    private VerifiedPhone(String phone) {
        this.phone = phone;
    }

    public static VerifiedPhone createVerifiedPhone(String phone) {
        return new VerifiedPhone(phone);
    }
}
