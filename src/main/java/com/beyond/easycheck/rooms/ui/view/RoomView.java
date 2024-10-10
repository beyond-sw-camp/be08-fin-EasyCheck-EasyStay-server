package com.beyond.easycheck.rooms.ui.view;

import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class RoomView {

    @NotNull
    private Long roomId;

    @NotBlank
    private String roomNumber;

    @NotBlank
    private String roomPic;

    @NotNull
    private int roomAmount;

    @NotNull
    private int remainingRoom;

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

    public RoomView(Long roomId, String roomNumber, String roomPic, int roomAmount, int remainingRoom, RoomStatus status, Long roomTypeId, Long accomodationId, String typeName, String description, int maxOccupancy) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomPic = roomPic;
        this.roomAmount = roomAmount;
        this.remainingRoom = remainingRoom;
        this.status = status;
        this.roomTypeId = roomTypeId;
        this.accomodationId = accomodationId;
        this.typeName = typeName;
        this.description = description;
        this.maxOccupancy = maxOccupancy;
    }

}
