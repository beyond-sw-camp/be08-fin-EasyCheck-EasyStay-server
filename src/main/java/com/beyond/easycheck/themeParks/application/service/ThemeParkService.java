package com.beyond.easycheck.themeparks.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.themeparks.exception.ThemeParkMessageType;
import com.beyond.easycheck.themeparks.infrastructure.persistence.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.persistence.repository.ThemeParkRepository;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeParkService implements ThemeParkReadUseCase, ThemeParkOperationUseCase {

    private final ThemeParkRepository themeParkRepository;

    @Override
    @Transactional
    public FindThemeParkResult saveThemePark(ThemeParkCreateCommand command) {

        boolean exists = themeParkRepository.existsByNameAndLocation(command.getName(), command.getLocation());
        if (exists) {
            throw new EasyCheckException(ThemeParkMessageType.DUPLICATE_THEME_PARK);
        }


        try {
            log.info("[ThemeParkService - saveThemePark] command = {}", command);
            return FindThemeParkResult.findByThemeParkEntity(
                    themeParkRepository.save(ThemeParkEntity.createThemePark(command))
            );
        } catch (DataAccessException | PersistenceException e) {
            throw new EasyCheckException(ThemeParkMessageType.DATABASE_CONNECTION_FAILED);
        } catch (Exception e) {
            throw new EasyCheckException(ThemeParkMessageType.UNKNOWN_ERROR);
        }

    }

    @Override
    public List<FindThemeParkResult> getThemeParks() {
        log.info("[ThemeParkService - getThemeParks]");

        List<ThemeParkEntity> results = themeParkRepository.findAll();

        return results.stream()
                .map(FindThemeParkResult::findByThemeParkEntity)
                .toList();
    }

    @Override
    public FindThemeParkResult getFindThemePark(Long id) {
        log.info("[ThemeParkService - getThemePark] id = {}", id);

        return FindThemeParkResult.findByThemeParkEntity(
                retrieveThemeParkEntityById(id)
        );
    }


    private ThemeParkEntity retrieveThemeParkEntityById(Long id) {
        return themeParkRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(ThemeParkMessageType.THEME_PARK_NOT_FOUND));
    }
}
