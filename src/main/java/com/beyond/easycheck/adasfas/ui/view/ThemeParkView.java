package com.beyond.easycheck.adasfas.ui.view;

import com.beyond.easycheck.adasfas.application.service.ThemeParkReadUseCase.FindThemeParkResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThemeParkView {

    private final Long id;
    private final String name;
    private final String guidePageName;
    private final String description;
    private final String ticketAvailable;
    private final List<String> imageUrls;

    public ThemeParkView(FindThemeParkResult themePark) {
        this.id = themePark.getId();
        this.name = themePark.getName();
        this.guidePageName = themePark.getGuidePageName();
        this.description = themePark.getDescription();
        this.imageUrls = themePark.getImageUrls();
        this.ticketAvailable = themePark.getTicketAvailable();
    }
}
