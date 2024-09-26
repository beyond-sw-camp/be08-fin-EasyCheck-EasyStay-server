package com.beyond.easycheck.roomType.ui.requestbody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = lombok.AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
public class RoomTypeCreateRequest {

    @NotNull
    private Long accommodationId;

    @NotBlank
    private String typeName;

    @NotBlank
    private String description;

    @NotNull
    private int maxOccupancy;

}
