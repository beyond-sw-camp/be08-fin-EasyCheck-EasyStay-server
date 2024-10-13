package com.beyond.easycheck.facilities.application.service;

import com.amazonaws.SdkClientException;
import com.beyond.easycheck.accomodations.exception.AccommodationMessageType;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.facilities.exception.FacilityMessageType;
import com.beyond.easycheck.facilities.infrastructure.entity.FacilityEntity;
import com.beyond.easycheck.facilities.infrastructure.repository.FacilityRepository;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityCreateRequest;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityUpdateRequest;
import com.beyond.easycheck.facilities.ui.view.FacilityView;
import com.beyond.easycheck.s3.application.domain.FileManagementCategory;
import com.beyond.easycheck.s3.application.service.S3Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.beyond.easycheck.facilities.exception.FacilityMessageType.NO_IMAGES_PROVIDED;
import static com.beyond.easycheck.s3.application.domain.FileManagementCategory.FACILITY;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class FacilityService {

    private static final Logger log = LoggerFactory.getLogger(FacilityService.class);
    private final FacilityRepository facilityRepository;
    private final AccommodationRepository accommodationRepository;

    private final S3Service s3Service;

    private void addImagesToFacility(FacilityEntity facilityEntity, List<String> imageUrls) {
        List<FacilityEntity.ImageEntity> newImageEntities = imageUrls.stream()
                .map(url -> FacilityEntity.ImageEntity.createImage(url, facilityEntity))
                .toList();

        if (facilityEntity.getImages() == null) {
            facilityEntity.setImages(new ArrayList<>());
        }

        for (FacilityEntity.ImageEntity newImage : newImageEntities) {
            if (!facilityEntity.getImages().contains(newImage)) {
                facilityEntity.addImage(newImage);
            }
        }
    }

    @Transactional
    public FacilityEntity createFacility(FacilityCreateRequest facilityCreateRequest, List<MultipartFile> imageFiles) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(facilityCreateRequest.getAccommodationId()).orElseThrow(
                () -> new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND)
        );

        List<String> imageUrls = s3Service.uploadFiles(imageFiles, FACILITY);

        FacilityEntity facilityEntity = FacilityEntity.builder()
                .accommodationEntity(accommodationEntity)
                .name(facilityCreateRequest.getName())
                .description(facilityCreateRequest.getDescription())
                .availableStatus(facilityCreateRequest.getAvailableStatus())
                .build();

        facilityRepository.save(facilityEntity);
        addImagesToFacility(facilityEntity, imageUrls);

        return facilityRepository.save(facilityEntity);
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

//    @Transactional
//    public void updateFacilityImages(Long id, List<MultipartFile> imageFiles, List<Long> imageIdsToDelete) {
//
//        FacilityEntity facilityEntity = facilityRepository.findById(id).orElseThrow(
//                () -> new EasyCheckException(FacilityMessageType.FACILITY_NOT_FOUND)
//        );
//
//        if (imageFiles == null || imageFiles.isEmpty()) {
//            throw new EasyCheckException(FacilityMessageType.NO_IMAGES_PROVIDED);
//        }
//
//        List<FacilityEntity.ImageEntity> existingImages = facilityEntity.getImages();
//
//        log.info("기존 이미지 URL 목록: {}", existingImages.stream().map(FacilityEntity.ImageEntity::getUrl).toList());
//
//        try {
//            if (imageIdsToDelete != null && !imageIdsToDelete.isEmpty()) {
//                List<FacilityEntity.ImageEntity> imagesToDelete = existingImages.stream()
//                        .filter(image -> imageIdsToDelete.contains(image.getId()))
//                        .toList();
//
//                log.info("삭제할 이미지 목록: {}", imagesToDelete.stream().map(FacilityEntity.ImageEntity::getUrl).toList());
//
//                for (FacilityEntity.ImageEntity imageToDelete : imagesToDelete) {
//                    String fileNameToDelete = extractFileNameFromUrl(imageToDelete.getUrl());
//                    s3Service.deleteFile(fileNameToDelete);
//                    facilityEntity.getImages().remove(imageToDelete);
//                }
//            }
//
//            List<String> newImageUrls = s3Service.uploadFiles(imageFiles, FileManagementCategory.THEME_PARK);
//            log.info("새로 업로드된 이미지 URL 목록: {}", newImageUrls);
//
//            for (String newImageUrl : newImageUrls) {
//                FacilityEntity.ImageEntity newImageEntity = FacilityEntity.ImageEntity.createImage(newImageUrl, facilityEntity);
//                facilityEntity.addImage(newImageEntity);
//            }
//
//            facilityRepository.save(facilityEntity);
//
//        } catch (SdkClientException e) {
//            throw new EasyCheckException(FacilityMessageType.IMAGE_UPDATE_FAILED);
//        }
//    }
//
//    private String extractFileNameFromUrl(String url) {
//        String[] parts = url.split("/");
//        String fileName = String.join("/", Arrays.copyOfRange(parts, 3, parts.length));
//        return fileName;
//    }

    @Transactional
    public void deleteFacility(Long id) {

        FacilityEntity facilityEntity = facilityRepository.findById(id).orElseThrow(
                () -> new EasyCheckException(FacilityMessageType.FACILITY_NOT_FOUND)
        );

        facilityRepository.delete(facilityEntity);
    }
}
