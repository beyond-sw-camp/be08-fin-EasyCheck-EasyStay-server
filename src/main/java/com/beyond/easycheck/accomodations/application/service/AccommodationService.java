package com.beyond.easycheck.accomodations.application.service;

import com.beyond.easycheck.accomodations.exception.AccommodationMessageType;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationCreateRequest;
import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationUpdateRequest;
import com.beyond.easycheck.accomodations.ui.view.AccommodationView;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.s3.application.domain.FileManagementCategory;
import com.beyond.easycheck.s3.application.service.S3Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final S3Service s3Service;

    @Transactional
    public Optional<AccommodationEntity> createAccommodation(AccommodationCreateRequest accommodationCreateRequest, List<MultipartFile> thumbnailFiles, List<MultipartFile> landscapeFiles, MultipartFile directionsFile) {

        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .name(accommodationCreateRequest.getName())
                .address(accommodationCreateRequest.getAddress())
                .latitude(accommodationCreateRequest.getLatitude())
                .longitude(accommodationCreateRequest.getLongitude())
                .responseTime(accommodationCreateRequest.getResponseTime())
                .accommodationType(accommodationCreateRequest.getAccommodationType())
                .build();

        if (thumbnailFiles != null && !thumbnailFiles.isEmpty()) {
            List<String> thumbnailUrls = thumbnailFiles.stream()
                    .map(file -> s3Service.uploadFile(file, FileManagementCategory.ACCOMMODATION_THUMBNAIL))
                    .collect(Collectors.toList());
            accommodationEntity.setThumbnailUrls(thumbnailUrls);
        }

        if (directionsFile != null && !directionsFile.isEmpty()) {
            String landscapeUrl = s3Service.uploadFile(directionsFile, FileManagementCategory.ACCOMMODATION);
            accommodationEntity.setDirectionsUrl(landscapeUrl);
        }

        if (landscapeFiles != null && !landscapeFiles.isEmpty()) {
            List<String> landscapeUrls = landscapeFiles.stream()
                    .map(file -> s3Service.uploadFile(file, FileManagementCategory.ACCOMMODATION_LANDSCAPE))
                    .collect(Collectors.toList());
            accommodationEntity.setLandscapeUrls(landscapeUrls);
        }

        return Optional.of(accommodationRepository.save(accommodationEntity));
    }

    @Transactional(readOnly = true)
    public List<AccommodationView> getAllAccommodations() {

        return accommodationRepository.findAll().stream().map(AccommodationView::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccommodationView getAccommodationById(Long id) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND)
        );

        return AccommodationView.of(accommodationEntity);
    }

    @Transactional
    public void updateAccommodation(Long id, AccommodationUpdateRequest accommodationUpdateRequest) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND)
        );

        accommodationEntity.updateAccommodation(accommodationUpdateRequest);

        accommodationRepository.save(accommodationEntity);
    }

    @Transactional
    public void deleteAccommodation(Long id) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND)
        );

        accommodationRepository.delete(accommodationEntity);
    }
}
