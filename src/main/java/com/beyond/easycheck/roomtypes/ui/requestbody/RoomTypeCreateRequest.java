package com.beyond.easycheck.roomtypes.ui.requestbody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@ToString
public class RoomTypeCreateRequest {

    @NotNull
    private Long accommodationId;

    @NotBlank
    private String typeName;

    @NotBlank
    private String description;

    @NotNull
    private Integer maxOccupancy;

}
