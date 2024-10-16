package com.beyond.easycheck.attractions.application.service;

import com.beyond.easycheck.attractions.infrastructure.entity.AttractionEntity;
import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase.FindAttractionResult;
import lombok.Builder;
import lombok.Getter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttractionOperationUseCase {

    FindAttractionResult createAttraction(AttractionCreateCommand command, List<MultipartFile> imageFiles);

    FindAttractionResult updateAttraction(Long attractionId, AttractionUpdateCommand command);

    void deleteAttraction(Long attractionId);

    @Transactional
    void updateAttractionImages(Long attractionId, List<MultipartFile> imageFiles, List<Long> imageIdsToDelete);

    @Getter
    @Builder
    class AttractionCreateCommand {
        private Long themeParkId;
        private String name;
        private String description;

        public void validate() {
            if (themeParkId == null || themeParkId <= 0) {
                throw new IllegalArgumentException("유효하지 않은 테마파크 ID입니다.");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 이름은 필수 입력 항목입니다.");
            }
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 설명은 필수 입력 항목입니다.");
            }
        }
    }

    @Getter
    @Builder
    class AttractionUpdateCommand {
        private final String name;
        private final String description;

        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 이름은 필수 입력 항목입니다.");
            }
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 설명은 필수 입력 항목입니다.");
            }
        }
    }
}
