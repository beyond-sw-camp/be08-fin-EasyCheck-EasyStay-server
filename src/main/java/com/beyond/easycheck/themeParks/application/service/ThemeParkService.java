package com.beyond.easycheck.themeparks.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.s3.application.domain.FileManagementCategory;
import com.beyond.easycheck.s3.application.service.S3Service;
import com.beyond.easycheck.themeparks.exception.ThemeParkMessageType;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkRepository;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.beyond.easycheck.accomodations.exception.AccommodationMessageType.ACCOMMODATION_NOT_FOUND;
import static com.beyond.easycheck.themeparks.exception.ThemeParkMessageType.THEME_PARK_DOES_NOT_BELONG_TO_ACCOMMODATION;

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
            throw new EasyCheckException(ThemeParkMessageType.DATABASE_CONNECTION_FAILED);
        } catch (Exception e) {
            log.error("Unknown error while saving theme park", e);
            throw new EasyCheckException(ThemeParkMessageType.UNKNOWN_ERROR);
        }
    }

    @Override
    @Transactional
    public FindThemeParkResult updateThemePark(Long id, ThemeParkUpdateCommand command, Long accommodationId, List<MultipartFile> imageFiles) {

        ThemeParkEntity themeParkEntity = themeParkRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(ThemeParkMessageType.THEME_PARK_NOT_FOUND));

        validateThemeParkBelongsToAccommodation(themeParkEntity, accommodationId);
        command.validate();

        try {
            if (imageFiles != null && !imageFiles.isEmpty()) {
                List<String> newImageUrls = s3Service.uploadFiles(imageFiles, FileManagementCategory.THEME_PARK);

                addImagesToThemePark(themeParkEntity, newImageUrls);
            }

            themeParkEntity.update(command.getName(), command.getDescription(), command.getLocation());

            return FindThemeParkResult.findByThemeParkEntity(themeParkRepository.save(themeParkEntity));
        } catch (DataAccessException | PersistenceException e) {
            log.error("Database error while updating theme park", e);
            throw new EasyCheckException(ThemeParkMessageType.DATABASE_CONNECTION_FAILED);
        } catch (Exception e) {
            log.error("Unknown error while updating theme park", e);
            throw new EasyCheckException(ThemeParkMessageType.UNKNOWN_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteThemePark(Long id, Long accommodationId) {
        ThemeParkEntity themeParkEntity = themeParkRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(ThemeParkMessageType.THEME_PARK_NOT_FOUND));

        validateThemeParkBelongsToAccommodation(themeParkEntity, accommodationId);

        try {
            List<String> imageUrls = themeParkEntity.getImages().stream()
                    .map(ThemeParkEntity.ImageEntity::getUrl)
                    .collect(Collectors.toList());
            s3Service.deleteFiles(imageUrls);

            themeParkRepository.deleteById(id);
        } catch (DataAccessException | PersistenceException e) {
            log.error("Database error while deleting theme park", e);
            throw new EasyCheckException(ThemeParkMessageType.DATABASE_CONNECTION_FAILED);
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
                .orElseThrow(() -> new EasyCheckException(ThemeParkMessageType.THEME_PARK_NOT_FOUND));
    }
}
