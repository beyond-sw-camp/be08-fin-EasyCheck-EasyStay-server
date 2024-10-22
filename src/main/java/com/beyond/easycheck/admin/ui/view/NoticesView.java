package com.beyond.easycheck.admin.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import static com.beyond.easycheck.admin.application.service.AdminReadUseCase.FindNoticeResult;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticesView {

    private Long id;

    private String accommodationName;

    private String userName;

    private String title;

    private String content;

    public NoticesView(FindNoticeResult result) {
        this.id = result.id();
        this.accommodationName = result.accommodationName();
        this.userName = result.userName();
        this.title = result.title();
        this.content = result.title();
    }
}
