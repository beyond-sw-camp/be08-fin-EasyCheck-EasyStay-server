package com.beyond.easycheck.attractions.ui.view;

import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase.FindAttractionResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttractionView {
    private final Long id;
    private final String name;
    private final String description;
    private final Long themeParkId;
    private final List<String> imageUrls;

    public AttractionView(FindAttractionResult result) {
        this.id = result.getId();
        this.name = result.getName();
        this.description = result.getDescription();
        this.themeParkId = result.getThemeParkId();
        this.imageUrls = result.getImageUrls();
    }
}