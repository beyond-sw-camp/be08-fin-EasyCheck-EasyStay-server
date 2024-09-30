package com.beyond.easycheck.suggestion.ui.view;

import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.AgreementType;
import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.SuggestionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SuggestionView {

    private Long id;

    private String accommodationName;

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
                suggestionEntity.getAccommodationEntity().getName(),
                suggestionEntity.getType(),
                suggestionEntity.getSubject(),
                suggestionEntity.getSuggesterName(),
                suggestionEntity.getEmail(),
                suggestionEntity.getTitle(),
                suggestionEntity.getContent(),
                suggestionEntity.getAttachmentPath(),
                suggestionEntity.getAgreementType()
        );
    }
}
