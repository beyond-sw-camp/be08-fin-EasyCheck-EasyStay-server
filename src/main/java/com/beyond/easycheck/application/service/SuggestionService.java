package com.beyond.easycheck.application.service;

import com.beyond.easycheck.infrastructure.persistence.entity.SuggestionEntity;
import com.beyond.easycheck.infrastructure.persistence.repository.SuggestionsRepository;
import com.beyond.easycheck.ui.requestbody.SuggestionCreateRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class SuggestionService {

    @Autowired
    private final SuggestionsRepository suggestionsRepository;

    @Transactional
    public Optional<SuggestionEntity> createSuggestion(SuggestionCreateRequest suggestionCreateRequest) {

        SuggestionEntity suggestion = SuggestionEntity.builder()
                .type(suggestionCreateRequest.getType())
                .subject(suggestionCreateRequest.getSubject())
                .name(suggestionCreateRequest.getName())
                .email(suggestionCreateRequest.getEmail())
                .title(suggestionCreateRequest.getTitle())
                .content(suggestionCreateRequest.getContent())
                .attachmentPath(suggestionCreateRequest.getAttachmentPath())
                .agreementType(suggestionCreateRequest.getAgreementType())
                .build();

        return Optional.of(suggestionsRepository.save(suggestion));

    }
}
