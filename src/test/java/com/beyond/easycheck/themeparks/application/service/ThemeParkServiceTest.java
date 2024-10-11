package com.beyond.easycheck.themeparks.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase.ThemeParkCreateCommand;
import com.beyond.easycheck.themeparks.application.service.ThemeParkReadUseCase.FindThemeParkResult;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

import static com.beyond.easycheck.accomodations.exception.AccommodationMessageType.ACCOMMODATION_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ThemeParkServiceTest {

    @InjectMocks
    private ThemeParkService themeParkService;

    @Mock
    private ThemeParkRepository themeParkRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 테마파크 생성 테스트
    @Test
    void shouldSaveThemeParkSuccessfully() {
        // Given
        ThemeParkCreateCommand command = new ThemeParkCreateCommand("테마파크 1", "설명", "서울", "이미지_주소");
        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationEntity));
        when(themeParkRepository.existsByNameAndLocation(command.getName(), command.getLocation())).thenReturn(false);
        when(themeParkRepository.save(any())).thenReturn(ThemeParkEntity.createThemePark(command, accommodationEntity));

        // When
        FindThemeParkResult result = themeParkService.saveThemePark(command, 1L);

        // Then
        assertNotNull(result);
        verify(themeParkRepository, times(1)).save(any(ThemeParkEntity.class));
    }

    // 숙소가 없을 때 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenAccommodationNotFound() {
        // Given
        ThemeParkCreateCommand command = new ThemeParkCreateCommand("테마파크 1", "설명", "서울", "이미지_주소");
        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.saveThemePark(command, 1L));
        assertEquals(ACCOMMODATION_NOT_FOUND.getMessage(), exception.getMessage());
    }

    // 테마파크 중복 시 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenThemeParkAlreadyExists() {
        // Given
        ThemeParkCreateCommand command = new ThemeParkCreateCommand("테마파크 1", "설명", "서울", "이미지_주소");
        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationEntity));
        when(themeParkRepository.existsByNameAndLocation(command.getName(), command.getLocation())).thenReturn(true);

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.saveThemePark(command, 1L));
        assertEquals("중복된 테마파크가 존재합니다.", exception.getMessage());
    }

    // 데이터베이스 오류 처리 테스트
    @Test
    void shouldThrowExceptionWhenDatabaseFails() {
        // Given
        ThemeParkCreateCommand command = new ThemeParkCreateCommand("테마파크 1", "설명", "서울", "이미지_주소");
        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationEntity));
        when(themeParkRepository.existsByNameAndLocation(any(), any())).thenReturn(false);
        when(themeParkRepository.save(any())).thenThrow(new DataAccessException("DB 오류") {});

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.saveThemePark(command, 1L));
        assertEquals("데이터베이스 연결에 실패했습니다.", exception.getMessage());
    }

    // 유효성 검사 실패 시 예외 처리 테스트 (VALIDATION_FAILED)
    @Test
    void shouldThrowValidationFailedExceptionWhenCommandIsInvalid() {
        // Given: 유효하지 않은 입력값
        ThemeParkCreateCommand command = new ThemeParkCreateCommand("", "", "서울", "이미지_주소"); // 빈 값으로 생성

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.saveThemePark(command, 1L));
        assertEquals("잘못된 입력값이 있습니다.", exception.getMessage());
    }

    // 테마파크 삭제 테스트
    @Test
    void shouldDeleteThemeParkSuccessfully() {
        // Given
        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        ThemeParkEntity themeParkEntity = new ThemeParkEntity(
                1L, "테마파크 이름", "설명", "서울", "이미지_주소", accommodationEntity
        );

        when(themeParkRepository.findById(1L)).thenReturn(Optional.of(themeParkEntity));

        // When
        themeParkService.deleteThemePark(1L, 1L);

        // Then
        verify(themeParkRepository, times(1)).deleteById(1L);
    }

    // 삭제 시 테마파크가 없을 때 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenThemeParkNotFoundOnDelete() {
        // Given
        when(themeParkRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.deleteThemePark(1L, 1L));
        assertEquals("테마파크를 찾을 수 없습니다.", exception.getMessage());
    }

    // 테마파크가 해당 숙소에 속하지 않을 때 예외 처리 테스트 (THEME_PARK_DOES_NOT_BELONG_TO_ACCOMMODATION)
    @Test
    void shouldThrowExceptionWhenThemeParkDoesNotBelongToAccommodation() {
        // Given
        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        AccommodationEntity anotherAccommodationEntity = AccommodationEntity.builder()
                .id(2L)
                .name("다른 숙소 이름")
                .address("다른 숙소 주소")
                .accommodationType(AccommodationType.RESORT)
                .build();

        ThemeParkEntity themeParkEntity = new ThemeParkEntity(
                1L, "테마파크 이름", "설명", "서울", "이미지_주소", anotherAccommodationEntity
        );

        when(themeParkRepository.findById(1L)).thenReturn(Optional.of(themeParkEntity));

        // When & Then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.deleteThemePark(1L, 1L));
        assertEquals("해당 테마파크는 이 사업장에 속해 있지 않습니다.", exception.getMessage());
    }
}
