package com.beyond.easycheck.adasfas.application.service;

import com.amazonaws.SdkClientException;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.s3.application.domain.FileManagementCategory;
import com.beyond.easycheck.s3.application.service.S3Service;
import com.beyond.easycheck.adasfas.exception.ThemeParkMessageType;
import com.beyond.easycheck.adasfas.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.adasfas.infrastructure.repository.ThemeParkRepository;
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
import static com.beyond.easycheck.adasfas.exception.ThemeParkMessageType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeParkService implements ThemeParkReadUseCase, ThemeParkOperationUseCase {

    private final ThemeParkRepository themeParkRepository;
    private final AccommodationRepository accommodationRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public FindThemeParkResult saveThemePark(ThemeParkCreateCommand command, Long accommodationId, List<MultipartFile> imageFiles) {

        command.validate();

        AccommodationEntity accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new EasyCheckException(ACCOMMODATION_NOT_FOUND));

        boolean exists = themeParkRepository.existsByName(command.getName());
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
            log.error("데이터베이스 오류", e);
            throw new EasyCheckException(DATABASE_CONNECTION_FAILED);
        } catch (SdkClientException e) {
            log.error("S3 이미지 삭제/업로드 오류", e);
            throw new EasyCheckException(IMAGE_UPDATE_FAILED);
        } catch (Exception e) {
            log.error("알 수 없는 오류", e);
            throw new EasyCheckException(UNKNOWN_ERROR);
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
            themeParkEntity.update(command.getName(), command.getDescription());

            return FindThemeParkResult.findByThemeParkEntity(themeParkRepository.save(themeParkEntity));

        } catch (DataAccessException | PersistenceException e) {
            log.error("데이터베이스 오류", e);
            throw new EasyCheckException(DATABASE_CONNECTION_FAILED);
        } catch (Exception e) {
            log.error("알 수 없는 오류", e);
            throw new EasyCheckException(UNKNOWN_ERROR);
        }
    }

    @Override
    @Transactional
    public void updateThemeParkImages(Long id, List<MultipartFile> imageFiles, List<Long> imageIdsToDelete) {
        ThemeParkEntity themeParkEntity = themeParkRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(ThemeParkMessageType.THEME_PARK_NOT_FOUND));

        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new EasyCheckException(NO_IMAGES_PROVIDED);
        }

        // 기존 이미지 삭제
        List<ThemeParkEntity.ImageEntity> existingImages = themeParkEntity.getImages();
        log.info("기존 이미지 URL 목록: {}", existingImages.stream().map(ThemeParkEntity.ImageEntity::getUrl).toList());

        try {
            if (imageIdsToDelete != null && !imageIdsToDelete.isEmpty()) {
                List<ThemeParkEntity.ImageEntity> imagesToDelete = existingImages.stream()
                        .filter(image -> imageIdsToDelete.contains(image.getId()))
                        .toList();

                log.info("삭제할 이미지 목록: {}", imagesToDelete.stream().map(ThemeParkEntity.ImageEntity::getUrl).toList());

                // 이미지 삭제
                for (ThemeParkEntity.ImageEntity imageToDelete : imagesToDelete) {
                    String fileNameToDelete = extractFileNameFromUrl(imageToDelete.getUrl());
                    s3Service.deleteFile(fileNameToDelete);
                    themeParkEntity.getImages().remove(imageToDelete);
                }
            }

            // 새 이미지 업로드
            List<String> newImageUrls = s3Service.uploadFiles(imageFiles, FileManagementCategory.THEME_PARK);
            log.info("새로 업로드된 이미지 URL 목록: {}", newImageUrls);

            for (String newImageUrl : newImageUrls) {
                ThemeParkEntity.ImageEntity newImageEntity = ThemeParkEntity.ImageEntity.createImage(newImageUrl, themeParkEntity);
                themeParkEntity.addImage(newImageEntity);
            }

            themeParkRepository.save(themeParkEntity);
            log.info("테마파크 이미지 업데이트 완료");

        } catch (DataAccessException | PersistenceException e) {
            log.error("데이터베이스 오류", e);
            throw new EasyCheckException(DATABASE_CONNECTION_FAILED);
        } catch (SdkClientException e) {
            log.error("S3 이미지 삭제/업로드 오류", e);
            throw new EasyCheckException(IMAGE_UPDATE_FAILED);
        } catch (Exception e) {
            log.error("알 수 없는 오류", e);
            throw new EasyCheckException(UNKNOWN_ERROR);
        }
    }


    private String extractFileNameFromUrl(String url) {
        String[] parts = url.split("/");
        String fileName = String.join("/", Arrays.copyOfRange(parts, 3, parts.length));
        log.info("추출한 파일 이름: {}", fileName);
        return fileName;
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
        }catch (DataAccessException | PersistenceException e) {
            log.error("데이터베이스 오류", e);
            throw new EasyCheckException(DATABASE_CONNECTION_FAILED);
        } catch (Exception e) {
            log.error("알 수 없는 오류", e);
            throw new EasyCheckException(UNKNOWN_ERROR);
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

}


