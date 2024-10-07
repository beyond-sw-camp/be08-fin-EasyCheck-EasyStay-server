package com.beyond.easycheck.user.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("활성", "정상적으로 사용 가능한 상태"),
    INACTIVE("비활성", "사용자가 일시적으로 계정을 비활성화한 상태"),
    SUSPENDED("정지", "관리자에 의해 일시적으로 정지된 상태"),
    BANNED("차단", "관리자에 의해 영구적으로 차단된 상태"),
    DORMANT("휴면", "장기간 미사용으로 인한 휴면 상태"),
    PENDING("법인 승인 대기", "법인 회원의 관리자 승인 대기 중"),
    UNDER_REVIEW("검토 중", "관리자 검토 중인 상태"),
    REJECTED("거부", "관리자에 의해 승인이 거부된 상태"),
    DEACTIVATED("탈퇴", "사용자가 탈퇴한 상태");

    private final String displayName;
    private final String description;
}
