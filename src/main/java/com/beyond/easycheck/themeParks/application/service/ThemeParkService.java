package com.beyond.easycheck.themeparks.application.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.s3.application.domain.FileManagementCategory;
import com.beyond.easycheck.s3.application.service.S3Service;
import com.beyond.easycheck.themeparks.exception.ThemeParkMessageType;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkImageRepository;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkRepository;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.beyond.easycheck.accomodations.exception.AccommodationMessageType.ACCOMMODATION_NOT_FOUND;
import static com.beyond.easycheck.common.exception.CommonMessageType.IMAGE_UPDATE_FAILED;
import static com.beyond.easycheck.common.exception.CommonMessageType.NO_IMAGES_PROVIDED;
import static com.beyond.easycheck.rooms.exception.RoomMessageType.IMAGE_NOT_FOUND;
import static com.beyond.easycheck.themeparks.exception.ThemeParkMessageType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeParkService implements ThemeParkReadUseCase, ThemeParkOperationUseCase {

    private final ThemeParkRepository themeParkRepository;
    private final ThemeParkImageRepository themeParkImageRepository;
    private final AccommodationRepository accommodationRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public FindThemeParkResult saveThemePark(ThemeParkCreateCommand command, Long accommodationId, List<MultipartFile> imageFiles) {

        command.validate();

        AccommodationEntity accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new EasyCheckException(ACCOMMODATION_NOT_FOUND));

        boolean exists = themeParkRepository.existsByNameAndLocation(command.getName(), command.getLocation());
        if (exists) {
            throw new EasyCheckException(ThemeParkMessageType.DUPLICATE_THEME_PARK);
        }

        try {
            log.info("[ThemeParkService - saveThemePark] command = {}", command);

            List<String> imageUrls = s3Service.uploadFiles(imageFiles, FileManagementCategory.THEME_PARK);
            ThemeParkEntity themeParkEntity = ThemeParkEntity.createThemePark(command, accommodation);

            addImagesToThemePark(themeParkEntity, imageUrls);

            return FindThemeParkResult.findByThemeParkEntity(themeParkRepository.save(themeParkEntity));
        } catch (DataAccessException | PersistenceException e) {
            log.error("Database error while saving theme park", e);
            throw new EasyCheckException(DATABASE_CONNECTION_FAILED);
        } catch (Exception e) {
            log.error("Unknown error while saving theme park", e);
            throw new EasyCheckException(ThemeParkMessageType.UNKNOWN_ERROR);
        }
    }

    @Override
    @Transactional
    public FindThemeParkResult updateThemePark(Long id, ThemeParkUpdateCommand command, Long accommodationId) {

        ThemeParkEntity themeParkEntity = themeParkRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(THEME_PARK_NOT_FOUND));

        validateThemeParkBelongsToAccommodation(themeParkEntity, accommodationId);
        command.validate();

        try {
            themeParkEntity.update(command.getName(), command.getDescription(), command.getLocation());

            return FindThemeParkResult.findByThemeParkEntity(themeParkRepository.save(themeParkEntity));

        } catch (DataAccessException | PersistenceException e) {
            log.error("Database error while updating theme park", e);
            throw new EasyCheckException(DATABASE_CONNECTION_FAILED);
        } catch (Exception e) {
            log.error("Unknown error while updating theme park", e);
            throw new EasyCheckException(UNKNOWN_ERROR);
        }
    }

    @Override
    @Transactional
    public void updateThemeParkImages(Long id, List<MultipartFile> imageFiles) {
        ThemeParkEntity themeParkEntity = themeParkRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(ThemeParkMessageType.THEME_PARK_NOT_FOUND));

        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new EasyCheckException(NO_IMAGES_PROVIDED);
        }

        List<ThemeParkEntity.ImageEntity> existingImages = themeParkEntity.getImages();

        try {
            List<String> newImageUrls = s3Service.uploadFiles(imageFiles, FileManagementCategory.THEME_PARK);

            List<String> oldImageUrls = existingImages.stream()
                    .map(ThemeParkEntity.ImageEntity::getUrl)
                    .toList();

            List<String> imagesToDelete = oldImageUrls.stream()
                    .filter(url -> !newImageUrls.contains(url))
                    .toList();

            List<String> fileNamesToDelete = imagesToDelete.stream()
                    .map(this::extractFileNameFromUrl)
                    .toList();

            s3Service.deleteFiles(fileNamesToDelete);

            themeParkEntity.getImages().removeIf(image -> imagesToDelete.contains(image.getUrl()));

            for (String newImageUrl : newImageUrls) {
                ThemeParkEntity.ImageEntity newImageEntity = ThemeParkEntity.ImageEntity.createImage(newImageUrl, themeParkEntity);
                themeParkEntity.addImage(newImageEntity);
            }

            themeParkRepository.save(themeParkEntity);

        } catch (SdkClientException e) {
            log.error("S3 이미지 삭제/업로드 오류", e);
            throw new EasyCheckException(IMAGE_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteThemePark(Long id, Long accommodationId) {
        ThemeParkEntity themeParkEntity = themeParkRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(THEME_PARK_NOT_FOUND));

        validateThemeParkBelongsToAccommodation(themeParkEntity, accommodationId);

        try {
            List<String> imageUrls = themeParkEntity.getImages().stream()
                    .map(ThemeParkEntity.ImageEntity::getUrl)
                    .collect(Collectors.toList());
            s3Service.deleteFiles(imageUrls);

            themeParkRepository.deleteById(id);
        } catch (DataAccessException | PersistenceException e) {
            log.error("Database error while deleting theme park", e);
            throw new EasyCheckException(DATABASE_CONNECTION_FAILED);
        } catch (Exception e) {
            log.error("Unknown error while deleting theme park", e);
            throw new EasyCheckException(ThemeParkMessageType.UNKNOWN_ERROR);
        }
    }

    @Override
    public List<FindThemeParkResult> getThemeParks(Long accommodationId) {
        log.info("[ThemeParkService - getThemeParks]");

        accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new EasyCheckException(ACCOMMODATION_NOT_FOUND));

        List<ThemeParkEntity> results = themeParkRepository.findByAccommodationId(accommodationId);

        return results.stream()
                .map(FindThemeParkResult::findByThemeParkEntity)
                .toList();
    }

    @Override
    public FindThemeParkResult getFindThemePark(Long id, Long accommodationId) {
        log.info("[ThemeParkService - getThemePark] id = {}", id);

        ThemeParkEntity themeParkEntity = retrieveThemeParkEntityById(id);
        validateThemeParkBelongsToAccommodation(themeParkEntity, accommodationId);

        return FindThemeParkResult.findByThemeParkEntity(themeParkEntity);
    }

    private void addImagesToThemePark(ThemeParkEntity themeParkEntity, List<String> imageUrls) {
        List<ThemeParkEntity.ImageEntity> newImageEntities = imageUrls.stream()
                .map(url -> ThemeParkEntity.ImageEntity.createImage(url, themeParkEntity))
                .toList();

        for (ThemeParkEntity.ImageEntity newImage : newImageEntities) {
            if (!themeParkEntity.getImages().contains(newImage)) {
                themeParkEntity.addImage(newImage);
            }
        }
    }

    private void validateThemeParkBelongsToAccommodation(ThemeParkEntity themeParkEntity, Long accommodationId) {
        if (!themeParkEntity.getAccommodation().getId().equals(accommodationId)) {
            throw new EasyCheckException(THEME_PARK_DOES_NOT_BELONG_TO_ACCOMMODATION);
        }
    }

    private ThemeParkEntity retrieveThemeParkEntityById(Long id) {
        return themeParkRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(THEME_PARK_NOT_FOUND));
    }

    private String extractFileNameFromUrl(String url) {
        String[] parts = url.split("/");
        return String.join("/", Arrays.copyOfRange(parts, 3, parts.length));
    }
}


