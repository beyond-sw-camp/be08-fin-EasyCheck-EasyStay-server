package com.beyond.easycheck.suggestions.ui.view;

import com.beyond.easycheck.suggestions.infrastructure.persistence.entity.AgreementType;
import com.beyond.easycheck.suggestions.infrastructure.persistence.entity.SuggestionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SuggestionView {

    private Long id;

    private String type;

    private String subject;

    private String name;

    private String email;

    private String title;

    private String content;

    private String attachmentPath;

    private AgreementType agreementType;

    public static SuggestionView of(SuggestionEntity suggestionEntity) {
        return new SuggestionView(
                suggestionEntity.getId(),
                suggestionEntity.getType(),
                suggestionEntity.getSubject(),
                suggestionEntity.getName(),
                suggestionEntity.getEmail(),
                suggestionEntity.getTitle(),
                suggestionEntity.getContent(),
                suggestionEntity.getAttachmentPath(),
                suggestionEntity.getAgreementType()
        );
    }
}
