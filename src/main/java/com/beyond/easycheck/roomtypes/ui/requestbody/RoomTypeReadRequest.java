package com.beyond.easycheck.roomtypes.ui.requestbody;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class RoomTypeReadRequest {

    private Long roomTypeId;

    private Long accomodationId;

    private String typeName;

    private String description;

    private int maxOccupancy;

    public RoomTypeReadRequest(Long roomTypeId, Long accomodationId, String typeName, String description, int maxOccupancy) {
        this.roomTypeId = roomTypeId;
        this.accomodationId = accomodationId;
        this.typeName = typeName;
        this.description = description;
        this.maxOccupancy = maxOccupancy;
    }
}
