package com.beyond.easycheck.rooms.ui.requestbody;

import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomCreateRequest {

    @NotNull
    private Long roomTypeId;

    @NotNull
    private String roomNumber;

    @NotNull
    private String roomPic;

    @NotNull
    private RoomStatus status;

    @NotNull @Max(10)
    private int roomAmount;

}
