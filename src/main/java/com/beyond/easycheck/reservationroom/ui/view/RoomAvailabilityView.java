package com.beyond.easycheck.reservationroom.ui.view;

import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomAvailabilityView {

    private Long roomId;
    private String roomName;
    private boolean available;

    public RoomAvailabilityView(RoomEntity room, boolean available) {

        this.roomId = room.getRoomId();
        this.roomName = room.getRoomNumber();
        this.available = available;
    }
}
