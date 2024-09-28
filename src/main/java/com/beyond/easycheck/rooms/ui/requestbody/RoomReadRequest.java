package com.beyond.easycheck.rooms.ui.requestbody;

import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class RoomReadRequest {

    @NotNull
    private Long roomId;

    @NotBlank
    private String roomNumber;

    @NotBlank
    private String roomPic;

    @NotBlank
    private RoomStatus status;

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

    public RoomReadRequest(Long roomId, String roomNumber, String roomPic, RoomStatus status, Long roomTypeId, Long accomodationId, String typeName, String description, int maxOccupancy) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomPic = roomPic;
        this.status = status;
        this.roomTypeId = roomTypeId;
        this.accomodationId = accomodationId;
        this.typeName = typeName;
        this.description = description;
        this.maxOccupancy = maxOccupancy;
    }
}