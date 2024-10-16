package com.beyond.easycheck.s3.application.domain;

import lombok.Getter;

@Getter
public enum FileManagementCategory {
    ACCOMMODATION_THUMBNAIL("accommodation_thumbmail", "사업장 썸네일"),
    ACCOMMODATION_LANDSCAPE("accommodation_landscape", "사업장 풍경"),
    RESORT("resort", "리조트"),
    THEME_PARK("theme-park", "테마파크"),
    USER("user", "사용자"),
    SUGGESTION("suggestion", "건의사항"),
    RESERVATION("reservation", "예약"),
    FACILITY("facility", "시설"),
    EVENT("event", "이벤트"),
    ROOM("room", "객실")
    ;

    private final String folderName;
    private final String displayName;

    FileManagementCategory(String folderName, String displayName) {
        this.folderName = folderName;
        this.displayName = displayName;
    }
}
