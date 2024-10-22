package com.beyond.easycheck.admin.ui.view;

import com.beyond.easycheck.admin.application.service.AdminReadUseCase;
import com.beyond.easycheck.admin.application.service.AdminReadUseCase.FindThemeParkResult;
import com.beyond.easycheck.themeParks.application.service.ThemeParkReadUseCase;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThemeParkView {
    private final Long id;
    private final String name;
    private final String description;
    private final List<String> imageUrls;

    public ThemeParkView(FindThemeParkResult themePark) {
        this.id = themePark.id();
        this.name = themePark.name();
        this.description = themePark.description();
        this.imageUrls = themePark.images();
    }
}
