package com.beyond.easycheck.reservationrooms.ui.view;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class DayRoomAvailabilityView {

    private LocalDate date;
    private String dayOfWeek;
    private List<RoomAvailabilityView> rooms;
}