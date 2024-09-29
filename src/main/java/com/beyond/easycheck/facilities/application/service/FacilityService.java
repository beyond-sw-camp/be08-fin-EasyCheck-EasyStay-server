package com.beyond.easycheck.facilities.application.service;

import com.beyond.easycheck.accomodations.exception.AccommodationMessageType;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.additionalservices.exception.AdditionalServiceMessageType;
import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceUpdateRequest;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.facilities.exception.FacilityMessageType;
import com.beyond.easycheck.facilities.infrastructure.entity.FacilityEntity;
import com.beyond.easycheck.facilities.infrastructure.repository.FacilityRepository;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityCreateRequest;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityUpdateRequest;
import com.beyond.easycheck.facilities.ui.view.FacilityView;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final AccommodationRepository accommodationRepository;

    @Transactional
    public Optional<FacilityEntity> createFacility(FacilityCreateRequest facilityCreateRequest) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(facilityCreateRequest.getAccommodationId()).orElseThrow(
                () -> new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND)
        );

        FacilityEntity facilityEntity = FacilityEntity.builder()
                .accommodationEntity(accommodationEntity)
                .name(facilityCreateRequest.getName())
                .description(facilityCreateRequest.getDescription())
                .availableStatus(facilityCreateRequest.getAvailableStatus())
                .build();

        return Optional.of(facilityRepository.save(facilityEntity));
    }

    @Transactional(readOnly = true)
    public List<FacilityView> getAllFacilities(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FacilityEntity> facilitiePage = facilityRepository.findAll(pageable);

        return facilitiePage.getContent().stream()
                .map(FacilityView::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FacilityView getFacilityById(Long id) {

        FacilityEntity facilityEntity = facilityRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(FacilityMessageType.FACILITY_NOT_FOUND)
        );

        return FacilityView.of(facilityEntity);
    }

    @Transactional
    public void updateFacility(Long id, FacilityUpdateRequest facilityUpdateRequest) {

        FacilityEntity facilityEntity = facilityRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(FacilityMessageType.FACILITY_NOT_FOUND)
        );

        facilityEntity.updateFacility(facilityUpdateRequest);

        facilityRepository.save(facilityEntity);
    }
}
