package com.beyond.easycheck.user.application.service;

import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;

import java.sql.Timestamp;

public interface UserReadUseCase {

    record FindUserResult(
            Long id,
            String email,
            String name,
            String phone,
            String addr,
            String addrDetail,
            String status,
            char marketingConsent,
            int point,
            Timestamp createdDate,
            Timestamp updatedDate
    ) {
        public static FindUserResult findByUserEntity(UserEntity userEntity) {
            return new FindUserResult(
                    userEntity.getId(),
                    userEntity.getEmail(),
                    userEntity.getName(),
                    userEntity.getPhone(),
                    userEntity.getAddr(),
                    userEntity.getAddrDetail(),
                    userEntity.getStatus(),
                    userEntity.getMarketingConsent(),
                    userEntity.getPoint(),
                    userEntity.getCreatedDate(),
                    userEntity.getUpdatedDate()
            );
        }
    }

    record FindJwtResult(
            String accessToken,
            String refreshToken
    ) {
        public static FindJwtResult findByTokenString(String accessToken, String refreshToken) {
            return new FindJwtResult(accessToken, refreshToken);
        }
    }
}
