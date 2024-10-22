package com.beyond.easycheck.admin.ui.view;

import com.beyond.easycheck.admin.application.service.AdminReadUseCase;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

import static com.beyond.easycheck.admin.application.service.AdminReadUseCase.*;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventView {
    private Long id;

    private String accommodationName;

    private List<String> images;

    private String eventName;

    private String detail;

    private LocalDate startDate;

    private LocalDate endDate;

    public EventView(FindEventResult result) {
        this.id = result.id();
        this.accommodationName = result.accommodationName();
        this.images = result.images();
        this.eventName = result.eventName();
        this.detail = result.detail();
        this.startDate = result.startDate();
        this.endDate = result.endDate();
    }
}
