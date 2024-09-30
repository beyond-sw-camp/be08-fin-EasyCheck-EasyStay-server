package com.beyond.easycheck.roomtypes.ui.view;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class RoomtypeView {

    @NotNull
    private Long roomTypeId;

    @NotNull
    private Long accomodationId;

    @NotBlank
    private String typeName;

    @NotBlank
    private String description;

    @Min(1)
    private int maxOccupancy;

    public RoomtypeView(Long roomTypeId, Long accomodationId, String typeName, String description, int maxOccupancy) {
        this.roomTypeId = roomTypeId;
        this.accomodationId = accomodationId;
        this.typeName = typeName;
        this.description = description;
        this.maxOccupancy = maxOccupancy;
    }
}
