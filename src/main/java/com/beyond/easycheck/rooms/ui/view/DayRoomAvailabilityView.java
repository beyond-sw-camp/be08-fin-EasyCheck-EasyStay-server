package com.beyond.easycheck.rooms.ui.view;

import com.beyond.easycheck.reservationroom.ui.view.RoomAvailabilityView;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class DayRoomAvailabilityView {

    private LocalDate date;
    private List<RoomAvailabilityView> roomAvailabilityList;
}