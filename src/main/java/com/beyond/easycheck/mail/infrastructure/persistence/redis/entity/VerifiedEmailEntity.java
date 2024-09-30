package com.beyond.easycheck.mail.infrastructure.persistence.redis.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@RedisHash(value = "verified_email")
public class VerifiedEmailEntity {

    @Id
    private String email;

    private VerifiedEmailEntity(String email) {
        this.email = email;
    }

    public static VerifiedEmailEntity createVerifiedEmail(String email) {
        return new VerifiedEmailEntity(email);
    }

}
