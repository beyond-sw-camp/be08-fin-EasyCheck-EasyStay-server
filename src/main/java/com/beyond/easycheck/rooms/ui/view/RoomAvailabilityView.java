package com.beyond.easycheck.rooms.ui.view;

import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomAvailabilityView {

    private Long roomId;
    private int remainingRoom;
    private RoomStatus status;
}
