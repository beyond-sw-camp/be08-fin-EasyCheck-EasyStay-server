package com.beyond.easycheck.sms.infrastructure.persistence.redis.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@ToString
@NoArgsConstructor
@RedisHash(value = "sms_verification_code")
public class SmsVerificationCode {
    @Id
    private String code;

    @Indexed
    private String phone;

    @TimeToLive
    private long ttl;

    private SmsVerificationCode(String phone, String code, long ttl) {
        this.phone = phone;
        this.code = code;
        this.ttl = ttl;
    }

    public static SmsVerificationCode createSmsVerificationCode(String phone, String code, long ttl) {
        return new SmsVerificationCode(phone, code, ttl);
    }
}
