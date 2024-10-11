package com.beyond.easycheck.attractions.application.service;

import com.beyond.easycheck.attractions.infrastructure.entity.AttractionEntity;
import com.beyond.easycheck.attractions.infrastructure.repository.AttractionRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

import static com.beyond.easycheck.attractions.exception.AttractionMessageType.ATTRACTION_NOT_FOUND;
import static com.beyond.easycheck.themeparks.exception.ThemeParkMessageType.THEME_PARK_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AttractionServiceTest {

    @InjectMocks
    private AttractionService attractionService;

    @Mock
    private AttractionRepository attractionRepository;

    @Mock
    private ThemeParkRepository themeParkRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 어트랙션 생성 테스트
    @Test
    void shouldCreateAttractionSuccessfully() {
        // Given
        AttractionOperationUseCase.AttractionCreateCommand command = new AttractionOperationUseCase.AttractionCreateCommand(
                1L, "어트랙션 이름", "설명", "이미지_주소");

        ThemeParkEntity themeParkEntity = new ThemeParkEntity(1L, "테마파크", "설명", "서울", "이미지_주소", null);
        AttractionEntity attractionEntity = new AttractionEntity(1L, "어트랙션 이름", "설명", "이미지_주소", themeParkEntity);

        when(themeParkRepository.findById(1L)).thenReturn(Optional.of(themeParkEntity));
        when(attractionRepository.save(any())).thenReturn(attractionEntity);

        // When
        AttractionReadUseCase.FindAttractionResult result = attractionService.createAttraction(command);

        // Then
        assertNotNull(result);
        assertEquals("어트랙션 이름", result.getName());
        verify(attractionRepository, times(1)).save(any(AttractionEntity.class));
    }

    // 테마파크가 없을 때 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenThemeParkNotFound() {
        // Given
        AttractionOperationUseCase.AttractionCreateCommand command = new AttractionOperationUseCase.AttractionCreateCommand(
                1L, "어트랙션 이름", "설명", "이미지_주소");

        when(themeParkRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> attractionService.createAttraction(command));
        assertEquals(THEME_PARK_NOT_FOUND.getMessage(), exception.getMessage());
    }

    // 어트랙션 업데이트 테스트
    @Test
    void shouldUpdateAttractionSuccessfully() {
        // Given
        AttractionOperationUseCase.AttractionUpdateCommand command = new AttractionOperationUseCase.AttractionUpdateCommand(
                "업데이트된 이름", "업데이트된 설명", "업데이트된 이미지");

        ThemeParkEntity themeParkEntity = new ThemeParkEntity(1L, "테마파크", "설명", "서울", "이미지_주소", null);
        AttractionEntity attractionEntity = new AttractionEntity(1L, "어트랙션 이름", "설명", "이미지_주소", themeParkEntity);

        when(attractionRepository.findById(1L)).thenReturn(Optional.of(attractionEntity));

        // When
        AttractionReadUseCase.FindAttractionResult result = attractionService.updateAttraction(1L, command);

        // Then
        assertNotNull(result);
        assertEquals("업데이트된 이름", result.getName());
        verify(attractionRepository, times(1)).findById(1L);
    }

    // 어트랙션 업데이트 시 어트랙션이 없을 때 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenAttractionNotFoundOnUpdate() {
        // Given
        AttractionOperationUseCase.AttractionUpdateCommand command = new AttractionOperationUseCase.AttractionUpdateCommand(
                "업데이트된 이름", "업데이트된 설명", "업데이트된 이미지");

        when(attractionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> attractionService.updateAttraction(1L, command));
        assertEquals(ATTRACTION_NOT_FOUND.getMessage(), exception.getMessage());
    }

    // 어트랙션 삭제 테스트
    @Test
    void shouldDeleteAttractionSuccessfully() {
        // Given
        when(attractionRepository.existsById(1L)).thenReturn(true);

        // When
        attractionService.deleteAttraction(1L);

        // Then
        verify(attractionRepository, times(1)).deleteById(1L);
    }

    // 어트랙션 삭제 시 어트랙션이 없을 때 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenAttractionNotFoundOnDelete() {
        // Given
        when(attractionRepository.existsById(1L)).thenReturn(false);

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> attractionService.deleteAttraction(1L));
        assertEquals(ATTRACTION_NOT_FOUND.getMessage(), exception.getMessage());
    }
}
