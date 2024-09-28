package com.beyond.easycheck.notices.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.notices.exception.NoticesMessageType;
import com.beyond.easycheck.notices.infrastructure.persistence.entity.NoticesEntity;
import com.beyond.easycheck.notices.infrastructure.persistence.repository.NoticesRepository;
import com.beyond.easycheck.notices.ui.requestbody.NoticesCreateRequest;
import com.beyond.easycheck.notices.ui.view.NoticesView;
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

    public List<NoticesView> getAllNotices(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NoticesEntity> noticesPage = noticesRepository.findAll(pageable);

        return noticesPage.getContent().stream()
                .map(NoticesView::of)
                .collect(Collectors.toList());
    }

    public NoticesView getNotices(Long id) {

        NoticesEntity noticesEntity = noticesRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(NoticesMessageType.NOTICES_NOT_FOUND)
        );

        return NoticesView.of(noticesEntity);
    }

    public void deleteNotices(Long id) {

        NoticesEntity noticesEntity = noticesRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(NoticesMessageType.NOTICES_NOT_FOUND)
        );

        noticesRepository.delete(noticesEntity);
    }
}
