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
        private final String introduction;
        private final String information;
        private final String standardUse;

        public void validate() {
            if (themeParkId == null || themeParkId <= 0) {
                throw new IllegalArgumentException("유효하지 않은 테마파크 ID입니다.");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 이름은 필수 입력 항목입니다.");
            }
            if (introduction == null || introduction.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 소개는 필수 입력 항목입니다.");
            }
            if (information == null || information.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 시설 정보는 필수 입력 항목입니다.");
            }
            if (standardUse == null || standardUse.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 이용 기준은 필수 입력 항목입니다.");
            }
        }
    }

    @Getter
    @Builder
    class AttractionUpdateCommand {
        private final String name;
        private final String introduction;
        private final String information;
        private final String standardUse;

        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 이름은 필수 입력 항목입니다.");
            }
            if (introduction == null || introduction.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 소개는 필수 입력 항목입니다.");
            }
            if (information == null || information.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 시설 정보는 필수 입력 항목입니다.");
            }
            if (standardUse == null || standardUse.trim().isEmpty()) {
                throw new IllegalArgumentException("어트랙션 이용 기준은 필수 입력 항목입니다.");
            }
        }
    }
}
