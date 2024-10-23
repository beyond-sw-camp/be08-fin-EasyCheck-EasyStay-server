package com.beyond.easycheck.admin.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import static com.beyond.easycheck.admin.application.service.AdminReadUseCase.FindAttractionResult;

@Getter
@ToString
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
        this.id = result.id();
        this.name = result.name();
        this.introduction = result.information();
        this.information = result.information();
        this.standardUse = result.standardUse();
        this.themeParkId = result.themeParkId();
        this.imageUrl = result.imageUrl();
    }
}
