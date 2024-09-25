package com.beyond.easycheck.additionalservices.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.additionalservices.exception.AdditionalServiceMessageType;
import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.additionalservices.infrastructure.repository.AdditionalServiceRepository;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceCreateRequest;
import com.beyond.easycheck.common.exception.EasyCheckException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AdditionalServiceService {

    private final AdditionalServiceRepository additionalServiceRepository;
    private final AccommodationRepository accommodationRepository;

    @Transactional
    public Optional<AdditionalServiceEntity> createAdditionalService(AdditionalServiceCreateRequest additionalServiceCreateRequest) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(additionalServiceCreateRequest.getAdditionalServiceId()).orElseThrow(
                () -> new EasyCheckException(AdditionalServiceMessageType.ADDITIONAL_SERVICE_NOT_FOUND)
        );

        AdditionalServiceEntity additionalServiceEntity = AdditionalServiceEntity.builder()
                .accommodationId(accommodationEntity)
                .name(additionalServiceCreateRequest.getName())
                .description(additionalServiceCreateRequest.getDescription())
                .price(additionalServiceCreateRequest.getPrice())
                .build();

        return Optional.of(additionalServiceRepository.save(additionalServiceEntity));
    }
}
