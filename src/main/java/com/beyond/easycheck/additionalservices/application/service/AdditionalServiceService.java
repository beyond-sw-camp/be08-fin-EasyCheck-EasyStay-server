package com.beyond.easycheck.additionalservices.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.additionalservices.exception.AdditionalServiceMessageType;
import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.additionalservices.infrastructure.repository.AdditionalServiceRepository;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceCreateRequest;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceUpdateRequest;
import com.beyond.easycheck.additionalservices.ui.view.AdditionalServiceView;
import com.beyond.easycheck.common.exception.EasyCheckException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AdditionalServiceService {

    private final AdditionalServiceRepository additionalServiceRepository;
    private final AccommodationRepository accommodationRepository;

    @Transactional
    public Optional<AdditionalServiceEntity> createAdditionalService(AdditionalServiceCreateRequest additionalServiceCreateRequest) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(additionalServiceCreateRequest.getAccommodationId()).orElseThrow(
                () -> new EasyCheckException(AdditionalServiceMessageType.ADDITIONAL_SERVICE_NOT_FOUND)
        );

        AdditionalServiceEntity additionalServiceEntity = AdditionalServiceEntity.builder()
                .accommodationEntity(accommodationEntity)
                .name(additionalServiceCreateRequest.getName())
                .description(additionalServiceCreateRequest.getDescription())
                .price(additionalServiceCreateRequest.getPrice())
                .build();

        return Optional.of(additionalServiceRepository.save(additionalServiceEntity));
    }

    @Transactional(readOnly = true)
    public List<AdditionalServiceView> getAllAdditionalService(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AdditionalServiceEntity> additionalPage = additionalServiceRepository.findAll(pageable);

        return additionalPage.getContent().stream()
                .map(AdditionalServiceView::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdditionalServiceView getAdditionalServiceById(Long id) {

        AdditionalServiceEntity additionalServiceEntity = additionalServiceRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(AdditionalServiceMessageType.ADDITIONAL_SERVICE_NOT_FOUND)
        );

        return AdditionalServiceView.of(additionalServiceEntity);
    }

    @Transactional
    public void updateAdditionalService(Long id, AdditionalServiceUpdateRequest additionalServiceUpdateRequest) {

        AdditionalServiceEntity additionalServiceEntity = additionalServiceRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(AdditionalServiceMessageType.ADDITIONAL_SERVICE_NOT_FOUND)
        );

        additionalServiceEntity.updateAdditionalService(additionalServiceUpdateRequest);

        additionalServiceRepository.save(additionalServiceEntity);
    }
}
