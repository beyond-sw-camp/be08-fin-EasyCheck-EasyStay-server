package com.beyond.easycheck.attractions.application.service;

import com.amazonaws.SdkClientException;
import com.beyond.easycheck.attractions.exception.AttractionMessageType;
import com.beyond.easycheck.attractions.infrastructure.entity.AttractionEntity;
import com.beyond.easycheck.attractions.infrastructure.repository.AttractionRepository;
import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase.FindAttractionResult;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.s3.application.service.S3Service;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.beyond.easycheck.common.exception.CommonMessageType.IMAGE_UPDATE_FAILED;
import static com.beyond.easycheck.common.exception.CommonMessageType.NO_IMAGES_PROVIDED;
import static com.beyond.easycheck.themeparks.exception.ThemeParkMessageType.THEME_PARK_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AttractionServiceTest {

    @Mock
    private AttractionRepository attractionRepository;

    @Mock
    private ThemeParkRepository themeParkRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private AttractionService attractionService;

    private ThemeParkEntity themeParkEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        themeParkEntity = new ThemeParkEntity(1L, "Theme Park Name", "Description", "Location", null, List.of());
    }

    // 어트랙션 생성 성공 테스트
    @Test
    void createAttraction_Success() {
        AttractionEntity attraction = mock(AttractionEntity.class);
        when(themeParkRepository.findById(anyLong())).thenReturn(Optional.of(themeParkEntity));
        when(attractionRepository.save(any(AttractionEntity.class))).thenReturn(attraction);
        when(s3Service.uploadFiles(anyList(), any())).thenReturn(List.of("imageUrl"));

        FindAttractionResult result = attractionService.createAttraction(new AttractionOperationUseCase.AttractionCreateCommand(1L, "Attraction", "Desc"), List.of(mock(MultipartFile.class)));

        assertNotNull(result);
        verify(attractionRepository, times(1)).save(any(AttractionEntity.class));
    }

    // 어트랙션 생성 시 테마파크를 찾지 못하는 경우 예외 처리 테스트
    @Test
    void createAttraction_ThemeParkNotFound() {
        when(themeParkRepository.findById(anyLong())).thenReturn(Optional.empty());

        EasyCheckException exception = assertThrows(EasyCheckException.class, () ->
                attractionService.createAttraction(new AttractionOperationUseCase.AttractionCreateCommand(1L, "Attraction", "Desc"), List.of(mock(MultipartFile.class))));
        assertEquals(THEME_PARK_NOT_FOUND.getMessage(), exception.getMessage());
    }

    // S3 이미지 업로드 실패 시 예외 처리 테스트
    @Test
    void createAttraction_S3UploadFailure() {
        when(themeParkRepository.findById(anyLong())).thenReturn(Optional.of(themeParkEntity));
        when(s3Service.uploadFiles(anyList(), any())).thenThrow(SdkClientException.class);

        EasyCheckException exception = assertThrows(EasyCheckException.class, () ->
                attractionService.createAttraction(new AttractionOperationUseCase.AttractionCreateCommand(1L, "Attraction", "Desc"), List.of(mock(MultipartFile.class))));
        assertEquals(IMAGE_UPDATE_FAILED.getMessage(), exception.getMessage());
    }

    // 어트랙션 업데이트 성공 테스트
    @Test
    void updateAttraction_Success() {
        AttractionEntity attraction = new AttractionEntity(1L, "Attraction", "Description", themeParkEntity, null);
        when(attractionRepository.findById(anyLong())).thenReturn(Optional.of(attraction));

        AttractionOperationUseCase.AttractionUpdateCommand command = AttractionOperationUseCase.AttractionUpdateCommand.builder()
                .name("New Name")
                .description("New Description")
                .build();

        FindAttractionResult result = attractionService.updateAttraction(1L, command);

        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        verify(attractionRepository, times(1)).findById(anyLong());
    }

    // 어트랙션 업데이트 시 어트랙션을 찾지 못하는 경우 예외 처리 테스트
    @Test
    void updateAttraction_NotFound() {
        when(attractionRepository.findById(anyLong())).thenReturn(Optional.empty());

        AttractionOperationUseCase.AttractionUpdateCommand command = AttractionOperationUseCase.AttractionUpdateCommand.builder()
                .name("New Name")
                .description("New Description")
                .build();

        EasyCheckException exception = assertThrows(EasyCheckException.class, () ->
                attractionService.updateAttraction(1L, command));
        assertEquals(AttractionMessageType.ATTRACTION_NOT_FOUND.getMessage(), exception.getMessage());
    }

    // 이미지 업데이트 성공 테스트
    @Test
    void updateAttractionImages_Success() {
        AttractionEntity attraction = mock(AttractionEntity.class);
        when(attractionRepository.findById(anyLong())).thenReturn(Optional.of(attraction));
        when(s3Service.uploadFiles(anyList(), any())).thenReturn(List.of("newImageUrl"));

        attractionService.updateAttractionImages(1L, List.of(mock(MultipartFile.class)), List.of(1L));

        verify(attractionRepository, times(1)).findById(anyLong());
        verify(s3Service, times(1)).uploadFiles(anyList(), any());
        verify(s3Service, times(1)).deleteFiles(anyList());
    }

    // 이미지가 제공되지 않을 때 예외 처리 테스트
    @Test
    void updateAttractionImages_NoImagesProvided() {
        AttractionEntity attraction = mock(AttractionEntity.class);
        when(attractionRepository.findById(anyLong())).thenReturn(Optional.of(attraction));

        EasyCheckException exception = assertThrows(EasyCheckException.class, () ->
                attractionService.updateAttractionImages(1L, List.of(), List.of(1L)));
        assertEquals(NO_IMAGES_PROVIDED.getMessage(), exception.getMessage());
    }

    // 어트랙션 삭제 성공 테스트
    @Test
    void deleteAttraction_Success() {
        when(attractionRepository.existsById(anyLong())).thenReturn(true);

        attractionService.deleteAttraction(1L);

        verify(attractionRepository, times(1)).deleteById(anyLong());
    }

    // 어트랙션 삭제 시 어트랙션을 찾지 못하는 경우 예외 처리 테스트
    @Test
    void deleteAttraction_NotFound() {
        when(attractionRepository.existsById(anyLong())).thenReturn(false);

        EasyCheckException exception = assertThrows(EasyCheckException.class, () ->
                attractionService.deleteAttraction(1L));
        assertEquals(AttractionMessageType.ATTRACTION_NOT_FOUND.getMessage(), exception.getMessage());
    }
}
