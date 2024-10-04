package com.beyond.easycheck.suggestion.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.suggestion.exception.SuggestionMessageType;
import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.SuggestionEntity;
import com.beyond.easycheck.suggestion.infrastructure.persistence.repository.SuggestionsRepository;
import com.beyond.easycheck.suggestion.ui.requestbody.SuggestionCreateRequest;
import com.beyond.easycheck.suggestion.ui.view.SuggestionView;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final AccommodationRepository accommodationRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional
    public Optional<SuggestionEntity> createSuggestion(Long userId, SuggestionCreateRequest suggestionCreateRequest) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(suggestionCreateRequest.getAccommodationId()).orElseThrow(
                () -> new EasyCheckException(SuggestionMessageType.SUGGESTION_NOT_FOUND)
        );

        UserEntity userEntity = userJpaRepository.findById(userId).orElseThrow(
                () -> new EasyCheckException(SuggestionMessageType.SUGGESTION_NOT_FOUND)
        );


        SuggestionEntity suggestion = SuggestionEntity.builder()
                .accommodationEntity(accommodationEntity)
                .userEntity(userEntity)
                .type(suggestionCreateRequest.getType())
                .subject(suggestionCreateRequest.getSubject())
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
