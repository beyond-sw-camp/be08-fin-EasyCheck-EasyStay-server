package com.beyond.easycheck.attractions.application.service;

import com.amazonaws.SdkClientException;
import com.beyond.easycheck.attractions.exception.AttractionMessageType;
import com.beyond.easycheck.attractions.infrastructure.entity.AttractionEntity;
import com.beyond.easycheck.attractions.infrastructure.repository.AttractionRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.s3.application.domain.FileManagementCategory;
import com.beyond.easycheck.s3.application.service.S3Service;
import com.beyond.easycheck.themeParks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.themeParks.infrastructure.repository.ThemeParkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.beyond.easycheck.common.exception.CommonMessageType.IMAGE_UPDATE_FAILED;
import static com.beyond.easycheck.common.exception.CommonMessageType.NO_IMAGES_PROVIDED;
import static com.beyond.easycheck.themeParks.exception.ThemeParkMessageType.THEME_PARK_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttractionService implements AttractionOperationUseCase, AttractionReadUseCase {

    private final AttractionRepository attractionRepository;
    private final ThemeParkRepository themeParkRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public FindAttractionResult createAttraction(AttractionCreateCommand command, List<MultipartFile> imageFiles) {

        command.validate(); // 입력값 검증 로직 추가

        ThemeParkEntity themePark = themeParkRepository.findById(command.getThemeParkId())
                .orElseThrow(() -> new EasyCheckException(THEME_PARK_NOT_FOUND));

        AttractionEntity attraction = AttractionEntity.createAttraction(command.getName(), command.getDescription(), themePark);

        try {
            List<String> imageUrls = s3Service.uploadFiles(imageFiles, FileManagementCategory.ATTRACTION);
            addImagesToAttraction(attraction, imageUrls);
        } catch (SdkClientException e) {
            log.error("S3 이미지 업로드 실패: {}", e.getMessage(), e);
            throw new EasyCheckException(IMAGE_UPDATE_FAILED); // S3 이미지 업로드 실패 시 예외 처리
        }

        AttractionEntity savedAttraction = attractionRepository.save(attraction);
        return FindAttractionResult.fromEntity(savedAttraction);
    }

    @Override
    @Transactional
    public FindAttractionResult updateAttraction(Long attractionId, AttractionUpdateCommand command) {
        command.validate(); // 입력값 검증 로직 추가

        AttractionEntity attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new EasyCheckException(AttractionMessageType.ATTRACTION_NOT_FOUND));

        attraction.update(command.getName(), command.getDescription());
        return FindAttractionResult.fromEntity(attraction);
    }

    @Override
    @Transactional
    public void updateAttractionImages(Long attractionId, List<MultipartFile> imageFiles, List<Long> imageIdsToDelete) {
        AttractionEntity attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new EasyCheckException(AttractionMessageType.ATTRACTION_NOT_FOUND));

        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new EasyCheckException(NO_IMAGES_PROVIDED);
        }

        List<AttractionEntity.ImageEntity> existingImages = attraction.getImages();

        try {
            List<String> newImageUrls = s3Service.uploadFiles(imageFiles, FileManagementCategory.ATTRACTION);
            List<String> imagesToDelete = existingImages.stream()
                    .filter(image -> imageIdsToDelete.contains(image.getId()))
                    .map(AttractionEntity.ImageEntity::getUrl)
                    .toList();

            s3Service.deleteFiles(imagesToDelete.stream().map(this::extractFileNameFromUrl).collect(Collectors.toList()));

            attraction.getImages().removeIf(image -> imagesToDelete.contains(image.getUrl()));
            addImagesToAttraction(attraction, newImageUrls);

        } catch (SdkClientException e) {
            log.error("S3 이미지 삭제/업로드 오류", e);
            throw new EasyCheckException(IMAGE_UPDATE_FAILED); // S3 업로드/삭제 실패 시 예외 처리
        }
    }

    private void addImagesToAttraction(AttractionEntity attraction, List<String> imageUrls) {
        List<AttractionEntity.ImageEntity> newImageEntities = imageUrls.stream()
                .map(url -> AttractionEntity.ImageEntity.createImage(url, attraction))
                .toList();

        for (AttractionEntity.ImageEntity newImage : newImageEntities) {
            if (!attraction.getImages().contains(newImage)) {
                attraction.addImage(newImage);
            }
        }
    }

    private String extractFileNameFromUrl(String url) {
        String[] parts = url.split("/");
        return String.join("/", java.util.Arrays.copyOfRange(parts, 3, parts.length));
    }

    @Override
    @Transactional
    public void deleteAttraction(Long attractionId) {
        if (!attractionRepository.existsById(attractionId)) {
            throw new EasyCheckException(AttractionMessageType.ATTRACTION_NOT_FOUND);
        }

        attractionRepository.deleteById(attractionId);
    }

    @Override
    public List<FindAttractionResult> getAttractionsByThemePark(Long themeParkId) {
        List<AttractionEntity> attractions = attractionRepository.findByThemeParkId(themeParkId);
        return attractions.stream().map(FindAttractionResult::fromEntity).collect(Collectors.toList());
    }

    @Override
    public FindAttractionResult getAttractionById(Long attractionId) {
        AttractionEntity attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new EasyCheckException(AttractionMessageType.ATTRACTION_NOT_FOUND));
        return FindAttractionResult.fromEntity(attraction);
    }
}