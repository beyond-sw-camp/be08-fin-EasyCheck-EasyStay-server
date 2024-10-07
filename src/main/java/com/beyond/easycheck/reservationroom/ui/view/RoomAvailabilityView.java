package com.beyond.easycheck.reservationroom.ui.view;

import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomAvailabilityView {

    private Long roomId;
    private String roomTypeName;
    private int remainingRoom;
    private RoomStatus status;
}
