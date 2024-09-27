package com.beyond.easycheck.attractions.application.service;

import com.beyond.easycheck.attractions.infrastructure.entity.AttractionEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

public interface AttractionReadUseCase {

    List<FindAttractionResult> getAttractionsByThemePark(Long themeParkId);

    FindAttractionResult getAttractionById(Long attractionId);

    @Getter
    @Builder
    @EqualsAndHashCode
    class FindAttractionResult {
        private final Long id;
        private final String name;
        private final String description;
        private final String image;
        private final String themeParkId;

        public static FindAttractionResult fromEntity(AttractionEntity attraction) {
            return FindAttractionResult.builder()
                    .id(attraction.getId())
                    .name(attraction.getName())
                    .description(attraction.getDescription())
                    .image(attraction.getImage())
                    .build();
        }
    }
}

