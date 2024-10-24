package com.beyond.easycheck.attractions.application.service;

import com.amazonaws.SdkClientException;
import com.beyond.easycheck.attractions.exception.AttractionMessageType;
import com.beyond.easycheck.attractions.infrastructure.entity.AttractionEntity;
import com.beyond.easycheck.attractions.infrastructure.repository.AttractionRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.s3.application.domain.FileManagementCategory;
import com.beyond.easycheck.s3.application.service.S3Service;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.beyond.easycheck.common.exception.CommonMessageType.IMAGE_UPDATE_FAILED;
import static com.beyond.easycheck.common.exception.CommonMessageType.NO_IMAGES_PROVIDED;
import static com.beyond.easycheck.themeparks.exception.ThemeParkMessageType.THEME_PARK_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttractionService implements AttractionOperationUseCase, AttractionReadUseCase {

    private final AttractionRepository attractionRepository;
    private final ThemeParkRepository themeParkRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public FindAttractionResult createAttraction(AttractionCreateCommand command, MultipartFile imageFile) {

        command.validate();

        ThemeParkEntity themePark = themeParkRepository.findById(command.getThemeParkId())
                .orElseThrow(() -> new EasyCheckException(THEME_PARK_NOT_FOUND));

        AttractionEntity attraction = AttractionEntity.createAttraction(command.getName(), command.getIntroduction(), command.getInformation(), command.getStandardUse(), themePark, null);

        try {
            String imageUrl = s3Service.uploadFile(imageFile, FileManagementCategory.ATTRACTION);
            attraction.updateImage(imageUrl); // 이미지 URL 업데이트
        } catch (SdkClientException e) {
            log.error("S3 이미지 업로드 실패: {}", e.getMessage(), e);
            throw new EasyCheckException(IMAGE_UPDATE_FAILED); // S3 이미지 업로드 실패 시 예외 처리
        }

        AttractionEntity savedAttraction = attractionRepository.save(attraction);
        return FindAttractionResult.fromEntity(savedAttraction);
    }

    @Override
    @Transactional
    public FindAttractionResult updateAttraction(Long attractionId, AttractionUpdateCommand command) {
        command.validate(); // 입력값 검증 로직 추가

        AttractionEntity attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new EasyCheckException(AttractionMessageType.ATTRACTION_NOT_FOUND));

        attraction.update(command.getName(), command.getIntroduction(), command.getInformation(), command.getStandardUse());
        return FindAttractionResult.fromEntity(attraction);
    }

    @Override
    @Transactional
    public void updateAttractionImage(Long attractionId, MultipartFile imageFile) {
        AttractionEntity attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new EasyCheckException(AttractionMessageType.ATTRACTION_NOT_FOUND));

        if (imageFile == null || imageFile.isEmpty()) {
            throw new EasyCheckException(NO_IMAGES_PROVIDED);
        }

        try {
            String newImageUrl = s3Service.uploadFile(imageFile, FileManagementCategory.ATTRACTION);

            String existingImageUrl = attraction.getImageUrl();
            if (existingImageUrl != null) {
                s3Service.deleteFile(extractFileNameFromUrl(existingImageUrl));
            }
            attraction.updateImage(newImageUrl);

        } catch (SdkClientException e) {
            log.error("S3 이미지 삭제/업로드 오류", e);
            throw new EasyCheckException(IMAGE_UPDATE_FAILED); // S3 업로드/삭제 실패 시 예외 처리
        }
    }

    private String extractFileNameFromUrl(String url) {
        String[] parts = url.split("/");
        return String.join("/", java.util.Arrays.copyOfRange(parts, 3, parts.length));
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
        return attractions.stream().map(FindAttractionResult::fromEntity).collect(Collectors.toList());
    }

    @Override
    public FindAttractionResult getAttractionById(Long attractionId) {
        AttractionEntity attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new EasyCheckException(AttractionMessageType.ATTRACTION_NOT_FOUND));
        return FindAttractionResult.fromEntity(attraction);
    }
}
