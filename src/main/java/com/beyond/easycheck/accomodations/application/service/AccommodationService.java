package com.beyond.easycheck.accomodations.application.service;

import com.beyond.easycheck.accomodations.exception.AccommodationMessageType;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationCreateRequest;
import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationUpdateRequest;
import com.beyond.easycheck.accomodations.ui.view.AccommodationView;
import com.beyond.easycheck.common.exception.EasyCheckException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    @Transactional
    public Optional<AccommodationEntity> createAccommodation(AccommodationCreateRequest accommodationCreateRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .name(accommodationCreateRequest.getName())
                .address(accommodationCreateRequest.getAddress())
                .accommodationType(accommodationCreateRequest.getAccommodationType())
                .build();

        return Optional.of(accommodationRepository.save(accommodationEntity));
    }

    @Transactional(readOnly = true)
    public List<AccommodationView> getAllAccommodations(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AccommodationEntity> accommodationPage = accommodationRepository.findAll(pageable);

        return accommodationPage.getContent().stream()
                .map(AccommodationView::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccommodationView getAccommodationById(Long id) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND)
        );

        return AccommodationView.of(accommodationEntity);
    }

    @Transactional
    public void updateAccommodation(Long userId, Long id, AccommodationUpdateRequest accommodationUpdateRequest) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND)
        );

        accommodationEntity.updateAccommodation(accommodationUpdateRequest);

        accommodationRepository.save(accommodationEntity);
    }

    @Transactional
    public void deleteAccommodation(Long userId, Long id) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND)
        );

        accommodationRepository.delete(accommodationEntity);
    }
}
