package com.beyond.easycheck.room.ui.requestbody.Room;

import com.beyond.easycheck.room.infrastructure.persistence.entity.RoomStatus;
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

}
