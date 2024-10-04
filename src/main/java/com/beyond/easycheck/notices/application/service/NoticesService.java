package com.beyond.easycheck.notices.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.additionalservices.exception.AdditionalServiceMessageType;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.notices.exception.NoticesMessageType;
import com.beyond.easycheck.notices.infrastructure.persistence.entity.NoticesEntity;
import com.beyond.easycheck.notices.infrastructure.persistence.repository.NoticesRepository;
import com.beyond.easycheck.notices.ui.requestbody.NoticesCreateRequest;
import com.beyond.easycheck.notices.ui.requestbody.NoticesUpdateRequest;
import com.beyond.easycheck.notices.ui.view.NoticesView;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
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
@Transactional
public class NoticesService {

    private final NoticesRepository noticesRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserJpaRepository userJpaRepository;

    public Optional<NoticesEntity> createNotices(Long userId ,NoticesCreateRequest noticesCreateRequest) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(noticesCreateRequest.getAccommodationId()).orElseThrow(
                () -> new EasyCheckException(NoticesMessageType.NOTICES_NOT_FOUND)
        );

        UserEntity userEntity = userJpaRepository.findById(userId).orElseThrow(
                () -> new EasyCheckException(NoticesMessageType.NOTICES_NOT_FOUND)
        );


        NoticesEntity notices = NoticesEntity.builder()
                .accommodationEntity(accommodationEntity)
                .userEntity(userEntity)
                .title(noticesCreateRequest.getTitle())
                .content(noticesCreateRequest.getContent())
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

    public Optional<Void> updateNotices(Long id, NoticesUpdateRequest noticesUpdateRequest) {

        // 공지사항을 ID로 조회
        NoticesEntity noticesEntity = noticesRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(NoticesMessageType.NOTICES_NOT_FOUND)
        );

        // 업데이트할 필드 설정
        noticesEntity.updateNotices(noticesUpdateRequest);

        return Optional.empty();
    }
}
