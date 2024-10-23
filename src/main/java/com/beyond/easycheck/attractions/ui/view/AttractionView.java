package com.beyond.easycheck.attractions.ui.view;

import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase.FindAttractionResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttractionView {
    private final Long id;
    private final String name;
    private final String introduction;
    private final String information;
    private final String standardUse;
    private final Long themeParkId;
    private final String imageUrl;

    public AttractionView(FindAttractionResult result) {
        this.id = result.getId();
        this.name = result.getName();
        this.introduction = result.getIntroduction();
        this.information = result.getInformation();
        this.standardUse = result.getStandardUse();
        this.themeParkId = result.getThemeParkId();
        this.imageUrl = result.getImageUrl();
    }
}
