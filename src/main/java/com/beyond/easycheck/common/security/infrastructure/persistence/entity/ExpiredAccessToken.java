package com.beyond.easycheck.common.security.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash(value = "accessToken")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpiredAccessToken {
    @Id
    private String accessToken;

    @TimeToLive
    private long ttl;

    private ExpiredAccessToken(String accessToken) {
        this.accessToken = accessToken;
        this.ttl = 480L;
    }

    public static ExpiredAccessToken createExpiredAccessToken(String accessToken) {
        return new ExpiredAccessToken(accessToken);
    }
}

