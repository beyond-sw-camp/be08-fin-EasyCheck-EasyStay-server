package com.beyond.easycheck.admin.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import static com.beyond.easycheck.admin.application.service.AdminReadUseCase.FindSuggestionResult;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuggestionCompactView {

    private final Long id;
    private final String type;
    private final String subject;
    private final String email;
    private final String title;
    private final String content;
    private final String userName;

    public SuggestionCompactView(FindSuggestionResult result) {
        this.id = result.id();
        this.type = result.type();
        this.subject = result.subject();
        this.email = result.email();
        this.title = result.title();
        this.content = result.content();
        userName = result.user() == null ? null :
                result.user().name();

    }
}
