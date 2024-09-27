package com.beyond.easycheck.attractions.application.service;

import com.beyond.easycheck.attractions.exception.AttractionMessageType;
import com.beyond.easycheck.attractions.infrastructure.entity.AttractionEntity;
import com.beyond.easycheck.attractions.infrastructure.repository.AttractionRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.themeparks.exception.ThemeParkMessageType;
import com.beyond.easycheck.themeparks.infrastructure.persistence.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.persistence.repository.ThemeParkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttractionService implements AttractionOperationUseCase, AttractionReadUseCase {

    private final AttractionRepository attractionRepository;

    private final ThemeParkRepository themeParkRepository;

    @Override
    @Transactional
    public FindAttractionResult createAttraction(AttractionCreateCommand command) {
        ThemeParkEntity themePark = themeParkRepository.findById(command.getThemeParkId())
                .orElseThrow(() -> new EasyCheckException(ThemeParkMessageType.THEME_PARK_NOT_FOUND));

        AttractionEntity attraction = AttractionEntity.createAttraction(command, themePark);

        AttractionEntity savedAttraction = attractionRepository.save(attraction);

        return FindAttractionResult.fromEntity(savedAttraction);
    }

    @Override
    @Transactional
    public FindAttractionResult updateAttraction(Long attractionId, AttractionUpdateCommand command) {
        AttractionEntity attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new EasyCheckException(AttractionMessageType.ATTRACTION_NOT_FOUND));

        attraction.update(command.getName(), command.getDescription(), command.getImage());

        return FindAttractionResult.fromEntity(attraction);
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

        return attractions.stream()
                .map(FindAttractionResult::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public FindAttractionResult getAttractionById(Long attractionId) {
        AttractionEntity attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new EasyCheckException(AttractionMessageType.ATTRACTION_NOT_FOUND));

        return FindAttractionResult.fromEntity(attraction);
    }
}
