package com.beyond.easycheck.themeparks.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.themeparks.exception.ThemeParkMessageType;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkRepository;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.beyond.easycheck.accomodations.exception.AccommodationMessageType.ACCOMMODATION_NOT_FOUND;
import static com.beyond.easycheck.themeparks.exception.ThemeParkMessageType.THEME_PARK_DOES_NOT_BELONG_TO_ACCOMMODATION;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeParkService implements ThemeParkReadUseCase, ThemeParkOperationUseCase {

    private final ThemeParkRepository themeParkRepository;
    private final AccommodationRepository accommodationRepository;

    @Override
    @Transactional
    public FindThemeParkResult saveThemePark(ThemeParkCreateCommand command, Long accommodationId) {

        command.validate();

        AccommodationEntity accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new EasyCheckException(ACCOMMODATION_NOT_FOUND));

        boolean exists = themeParkRepository.existsByNameAndLocation(command.getName(), command.getLocation());
        if (exists) {
            throw new EasyCheckException(ThemeParkMessageType.DUPLICATE_THEME_PARK);
        }

        try {
            log.info("[ThemeParkService - saveThemePark] command = {}", command);

            return FindThemeParkResult.findByThemeParkEntity(
                    themeParkRepository.save(ThemeParkEntity.createThemePark(command, accommodation))
            );
        } catch (DataAccessException | PersistenceException e) {
            throw new EasyCheckException(ThemeParkMessageType.DATABASE_CONNECTION_FAILED);
        } catch (Exception e) {
            throw new EasyCheckException(ThemeParkMessageType.UNKNOWN_ERROR);
        }
    }

    @Override
    @Transactional
    public FindThemeParkResult updateThemePark(Long id, ThemeParkUpdateCommand command, Long accommodationId) {

        ThemeParkEntity themeParkEntity = themeParkRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(ThemeParkMessageType.THEME_PARK_NOT_FOUND));

        validateThemeParkBelongsToAccommodation(themeParkEntity, accommodationId);

        themeParkEntity.update(command.getName(), command.getDescription(), command.getLocation(), command.getImage());

        return FindThemeParkResult.findByThemeParkEntity(themeParkEntity);
    }

    @Override
    @Transactional
    public void deleteThemePark(Long id, Long accommodationId) {
        ThemeParkEntity themeParkEntity = themeParkRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(ThemeParkMessageType.THEME_PARK_NOT_FOUND));

        validateThemeParkBelongsToAccommodation(themeParkEntity, accommodationId);

        themeParkRepository.deleteById(id);
    }

    @Override
    public List<FindThemeParkResult> getThemeParks(Long accommodationId) {
        log.info("[ThemeParkService - getThemeParks]");

        accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new EasyCheckException(ACCOMMODATION_NOT_FOUND));

        List<ThemeParkEntity> results = themeParkRepository.findAll();

        return results.stream()
                .filter(themeParkEntity -> themeParkEntity.getAccommodation().getId().equals(accommodationId)) // 해당 숙박 시설에 속하는 테마파크만 필터링
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
