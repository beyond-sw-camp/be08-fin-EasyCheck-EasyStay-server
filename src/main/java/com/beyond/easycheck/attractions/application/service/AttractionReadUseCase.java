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
        private final String introduction;
        private final String information;
        private final String standardUse;
        private final Long themeParkId;
        private final String imageUrl;

        public static FindAttractionResult fromEntity(AttractionEntity attraction) {
            return FindAttractionResult.builder()
                    .id(attraction.getId())
                    .name(attraction.getName())
                    .themeParkId(attraction.getThemePark().getId())
                    .introduction(attraction.getIntroduction())
                    .information(attraction.getInformation())
                    .standardUse(attraction.getStandardUse())
                    .imageUrl(attraction.getImageUrl())
                    .build();
        }
    }
}
