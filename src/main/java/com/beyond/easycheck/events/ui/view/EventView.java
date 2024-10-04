package com.beyond.easycheck.events.ui.view;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
public class EventView {

    private Long id;

    private String accommodationName;

    private String eventName;

    private String image;

    private String detail;

    private LocalDate startDate;

    private LocalDate endDate;

    public EventView(Long id, String accommodationName, String eventName, String image, String detail, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.accommodationName = accommodationName;
        this.eventName = eventName;
        this.image = image;
        this.detail = detail;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
