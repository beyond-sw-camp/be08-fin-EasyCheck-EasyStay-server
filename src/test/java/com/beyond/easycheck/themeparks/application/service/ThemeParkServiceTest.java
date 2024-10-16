package com.beyond.easycheck.themeparks.application.service;

import com.amazonaws.SdkClientException;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.s3.application.service.S3Service;
import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase.ThemeParkCreateCommand;
import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase.ThemeParkUpdateCommand;
import com.beyond.easycheck.themeparks.application.service.ThemeParkReadUseCase.FindThemeParkResult;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.beyond.easycheck.accomodations.exception.AccommodationMessageType.ACCOMMODATION_NOT_FOUND;
import static com.beyond.easycheck.common.exception.CommonMessageType.IMAGE_UPDATE_FAILED;
import static com.beyond.easycheck.themeparks.exception.ThemeParkMessageType.*;
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

    @Mock
    private S3Service s3Service;


    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // 자원을 해제
    }

    // 테마파크 저장 성공 테스트
    @Test
    void shouldSaveThemeParkSuccessfully() {
        ThemeParkCreateCommand command = ThemeParkCreateCommand.builder()
                .name("테마파크 1")
                .description("설명")
                .location("서울")
                .build();

        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        List<MultipartFile> mockFiles = Collections.emptyList();
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationEntity));
        when(themeParkRepository.existsByNameAndLocation(command.getName(), command.getLocation())).thenReturn(false);
        when(s3Service.uploadFiles(any(), any())).thenReturn(Collections.emptyList());
        when(themeParkRepository.save(any())).thenReturn(ThemeParkEntity.createThemePark(command, accommodationEntity));

        FindThemeParkResult result = themeParkService.saveThemePark(command, 1L, mockFiles);

        assertNotNull(result);
        verify(themeParkRepository, times(1)).save(any(ThemeParkEntity.class));
    }

    // 숙소를 찾지 못할 때 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenAccommodationNotFound() {
        ThemeParkCreateCommand command = ThemeParkCreateCommand.builder()
                .name("테마파크 1")
                .description("설명")
                .location("서울")
                .build();
        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.empty());

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.saveThemePark(command, 1L, Collections.emptyList()));
        assertEquals(ACCOMMODATION_NOT_FOUND.getMessage(), exception.getMessage());
    }

    // 테마파크 중복 시 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenThemeParkAlreadyExists() {
        ThemeParkCreateCommand command = ThemeParkCreateCommand.builder()
                .name("테마파크 1")
                .description("설명")
                .location("서울")
                .build();

        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationEntity));
        when(themeParkRepository.existsByNameAndLocation(command.getName(), command.getLocation())).thenReturn(true);

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.saveThemePark(command, 1L, Collections.emptyList()));
        assertEquals(DUPLICATE_THEME_PARK.getMessage(), exception.getMessage());
    }

    // 데이터베이스 오류 시 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenDatabaseFails() {
        ThemeParkCreateCommand command = ThemeParkCreateCommand.builder()
                .name("테마파크 1")
                .description("설명")
                .location("서울")
                .build();
        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationEntity));
        when(themeParkRepository.existsByNameAndLocation(any(), any())).thenReturn(false);
        when(themeParkRepository.save(any())).thenThrow(new DataAccessException("DB 오류") {});

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.saveThemePark(command, 1L, Collections.emptyList()));
        assertEquals(DATABASE_CONNECTION_FAILED.getMessage(), exception.getMessage());
    }

    // 테마파크 업데이트 성공 테스트
    @Test
    void shouldUpdateThemeParkSuccessfully() {
        ThemeParkUpdateCommand UpdateCommand = ThemeParkUpdateCommand.builder()
                .name("업데이트된 테마파크")
                .description("업데이트된 설명")
                .location("부산")
                .build();

        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        ThemeParkCreateCommand CreateCommand = ThemeParkCreateCommand.builder()
                .name("테마파크 1")
                .description("설명")
                .location("서울")
                .build();

        ThemeParkEntity themeParkEntity = ThemeParkEntity.createThemePark(
                CreateCommand, accommodationEntity
        );

        when(themeParkRepository.findById(1L)).thenReturn(Optional.of(themeParkEntity));
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationEntity));
        when(themeParkRepository.save(any())).thenReturn(themeParkEntity);

        FindThemeParkResult result = themeParkService.updateThemePark(1L, UpdateCommand, 1L);

        assertNotNull(result);
        verify(themeParkRepository, times(1)).save(any(ThemeParkEntity.class));
    }

    // 테마파크 삭제 성공 테스트
    @Test
    void shouldDeleteThemeParkSuccessfully() {
        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        ThemeParkCreateCommand command = ThemeParkCreateCommand.builder()
                .name("테마파크 1")
                .description("설명")
                .location("서울")
                .build();

        ThemeParkEntity themeParkEntity = ThemeParkEntity.createThemePark(
                command, accommodationEntity
        );

        when(themeParkRepository.findById(1L)).thenReturn(Optional.of(themeParkEntity));

        themeParkService.deleteThemePark(1L, 1L);

        verify(themeParkRepository, times(1)).deleteById(1L);
    }

    // 테마파크 삭제 시 테마파크를 찾지 못할 때 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenThemeParkNotFoundOnDelete() {
        when(themeParkRepository.findById(1L)).thenReturn(Optional.empty());

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.deleteThemePark(1L, 1L));
        assertEquals(THEME_PARK_NOT_FOUND.getMessage(), exception.getMessage());
    }

    // 테마파크가 해당 숙소에 속하지 않을 때 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenThemeParkDoesNotBelongToAccommodation() {
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

        ThemeParkCreateCommand command = ThemeParkCreateCommand.builder()
                .name("테마파크 1")
                .description("설명")
                .location("서울")
                .build();

        ThemeParkEntity themeParkEntity = ThemeParkEntity.createThemePark(
                command, anotherAccommodationEntity
        );

        when(themeParkRepository.findById(1L)).thenReturn(Optional.of(themeParkEntity));

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.deleteThemePark(1L, 1L));
        assertEquals(THEME_PARK_DOES_NOT_BELONG_TO_ACCOMMODATION.getMessage(), exception.getMessage());
    }

    // S3 이미지 업로드 실패 시 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenS3ImageUploadFails() {
        // Given
        ThemeParkCreateCommand command = ThemeParkCreateCommand.builder()
                .name("테마파크 1")
                .description("설명")
                .location("서울")
                .build();

        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationEntity));
        when(themeParkRepository.existsByNameAndLocation(command.getName(), command.getLocation())).thenReturn(false);

        // S3 업로드 실패 시 SdkClientException 발생
        when(s3Service.uploadFiles(any(), any())).thenThrow(SdkClientException.class);

        // When
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.saveThemePark(command, 1L, List.of(mock(MultipartFile.class))));

        // Then
        assertEquals(IMAGE_UPDATE_FAILED.getMessage(), exception.getMessage());
    }

    // 입력값 검증 실패 시 예외 처리 테스트
    @Test
    void shouldThrowExceptionWhenValidationFails() {
        ThemeParkCreateCommand invalidCommand = ThemeParkCreateCommand.builder()
                .name("") // 빈 이름으로 잘못된 값 설정
                .description("설명")
                .location("서울")
                .build();

        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .name("숙소 이름")
                .address("숙소 주소")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationEntity));

        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> themeParkService.saveThemePark(invalidCommand, 1L, Collections.emptyList()));
        assertEquals(VALIDATION_FAILED.getMessage(), exception.getMessage());
    }
}
