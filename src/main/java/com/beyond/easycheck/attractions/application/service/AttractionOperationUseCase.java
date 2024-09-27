package com.beyond.easycheck.attractions.application.service;

import com.beyond.easycheck.attractions.infrastructure.entity.AttractionEntity;
import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase.FindAttractionResult;
import lombok.Builder;
import lombok.Getter;

public interface AttractionOperationUseCase {

    FindAttractionResult createAttraction(AttractionCreateCommand command);

    FindAttractionResult updateAttraction(Long attractionId, AttractionUpdateCommand command);

    void deleteAttraction(Long attractionId);

    @Getter
    @Builder
    class AttractionCreateCommand {
        private Long themeParkId;
        private String name;
        private String description;
        private String image;
    }

    @Getter
    @Builder
    class AttractionUpdateCommand {
        private final String name;
        private final String description;
        private final String image;
    }
}
