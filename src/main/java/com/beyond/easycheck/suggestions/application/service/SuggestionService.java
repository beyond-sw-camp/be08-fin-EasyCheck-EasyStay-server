package com.beyond.easycheck.suggestions.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.suggestions.exception.SuggestionMessageType;
import com.beyond.easycheck.suggestions.infrastructure.persistence.entity.SuggestionEntity;
import com.beyond.easycheck.suggestions.infrastructure.persistence.repository.SuggestionsRepository;
import com.beyond.easycheck.suggestions.ui.requestbody.SuggestionCreateRequest;
import com.beyond.easycheck.suggestions.ui.view.SuggestionView;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class SuggestionService {

    @Autowired
    private final SuggestionsRepository suggestionsRepository;

//    건의사항 등록하기
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


//    건의사항 리스트 불러오기
    @Transactional(readOnly = true)
    public List<SuggestionView> getAllsuggestions(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SuggestionEntity> suggestions = suggestionsRepository.findAll(pageable);

        return suggestions.getContent().stream()
                .map(SuggestionView::of)
                .collect(Collectors.toList());
    }

    public SuggestionView getSuggestionById(Long id) {

        SuggestionEntity suggestionEntity = suggestionsRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(SuggestionMessageType.SUGGESTION_NOT_FOUND)
        );
        return SuggestionView.of(suggestionEntity);
    }
}
