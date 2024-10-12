package com.beyond.easycheck.events.ui.view;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class EventView {

    private Long id;

    private String accommodationName;

    private List<String> images;

    private String eventName;

    private String detail;

    private LocalDate startDate;

    private LocalDate endDate;

    public EventView(Long id, String accommodationName, List<String> images, String eventName, String detail, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.accommodationName = accommodationName;
        this.images = images;
        this.eventName = eventName;
        this.detail = detail;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
