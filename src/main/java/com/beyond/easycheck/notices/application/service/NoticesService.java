package com.beyond.easycheck.notices.application.service;

import com.beyond.easycheck.notices.infrastructure.persistence.entity.NoticesEntity;
import com.beyond.easycheck.notices.infrastructure.persistence.repository.NoticesRepository;
import com.beyond.easycheck.notices.ui.requestbody.NoticesCreateRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class NoticesService {

    @Autowired
    private final NoticesRepository noticesRepository;

    @Transactional
    public Optional<NoticesEntity> createNotices(NoticesCreateRequest suggestionCreateRequest) {

        NoticesEntity notices = NoticesEntity.builder()
                .title(suggestionCreateRequest.getTitle())
                .content(suggestionCreateRequest.getContent())
                .build();

        return Optional.of(noticesRepository.save(notices));

    }

}
