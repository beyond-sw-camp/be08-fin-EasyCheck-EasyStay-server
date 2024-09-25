package com.beyond.easycheck.themeparks.ui.view;

import com.beyond.easycheck.themeparks.application.service.ThemeParkReadUseCase.FindThemeParkResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThemeParkView {

    private final Long id;
    private final String name;
    private final String description;
    private final String location;
    private final String image;

    public ThemeParkView(FindThemeParkResult themePark) {
        this.id = themePark.getId();
        this.name = themePark.getName();
        this.description = themePark.getDescription();
        this.location = themePark.getLocation();
        this.image = themePark.getImage();
    }
}
