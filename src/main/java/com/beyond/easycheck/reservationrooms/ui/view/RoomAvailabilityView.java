package com.beyond.easycheck.reservationrooms.ui.view;

import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoomAvailabilityView {

    private Long roomId;
    private String roomTypeName;
    private String roomNumber;
    private int remainingRoom;
    private RoomStatus status;
    private List<String> imageUrls;
}
