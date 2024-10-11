package com.beyond.easycheck.additionalservices.application.service;

import com.beyond.easycheck.accomodations.exception.AccommodationMessageType;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.additionalservices.exception.AdditionalServiceMessageType;
import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.additionalservices.infrastructure.repository.AdditionalServiceRepository;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceCreateRequest;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceUpdateRequest;
import com.beyond.easycheck.additionalservices.ui.view.AdditionalServiceView;
import com.beyond.easycheck.common.exception.EasyCheckException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AdditionalServiceServiceTest {

    @Mock
    AdditionalServiceRepository additionalServiceRepository;

    @Mock
    AccommodationRepository accommodationRepository;

    @InjectMocks
    AdditionalServiceService additionalServiceService;

    @Test
    @DisplayName("[부가서비스 추가] - 성공")
    void createAdditionalService_success() {
        // given
        final Long accommodationId = 1L;
        AdditionalServiceCreateRequest request = new AdditionalServiceCreateRequest(accommodationId, "qwer", "description", 10000);
        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(request.getAccommodationId())
                .build();
        AdditionalServiceEntity additionalServiceEntity = AdditionalServiceEntity.builder()
                .id(1L)
                .name(request.getName())
                .accommodationEntity(accommodationEntity)
                .description(request.getDescription())
                .price(request.getPrice())
                .build();

        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(mock(AccommodationEntity.class)));
        when(additionalServiceRepository.save(any(AdditionalServiceEntity.class))).thenReturn(additionalServiceEntity);

        // when
        Optional<AdditionalServiceEntity> result = additionalServiceService.createAdditionalService(request);

        // then
        assertTrue(result.isPresent());
        assertEquals(request.getAccommodationId(), result.get().getAccommodationEntity().getId());
        assertEquals(request.getName(), result.get().getName());
        assertEquals(request.getPrice(), result.get().getPrice());
        assertEquals(request.getDescription(), result.get().getDescription());
    }

    @Test
    @DisplayName("[부가서비스 추가] - 실패 - accoommodation을 찾지 못함")
    void createAdditionalService_failedByAccommodationNotFound() {
        // given
        final Long accommodationId = 1L;
        AdditionalServiceCreateRequest request = new AdditionalServiceCreateRequest(accommodationId, "qwer", "description", 10000);

        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> additionalServiceService.createAdditionalService(request))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(AccommodationMessageType.ACCOMMODATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("[부가서비스 리스트 조회] - 성공")
    void getAllAdditionalService() {
// Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .build();

        List<AdditionalServiceEntity> allEntities = new ArrayList<>();
        for (long i = 1; i <= 100; ++i) {
            allEntities.add(
                    AdditionalServiceEntity.builder()
                            .id(i)
                            .price(1000)
                            .description("test" + i)
                            .name("test" + i)
                            .accommodationEntity(accommodationEntity)
                            .build()
            );
        }

        // 첫 페이지의 10개 항목만 포함
        List<AdditionalServiceEntity> firstPageEntities = allEntities.subList(0, 10);
        Page<AdditionalServiceEntity> additionalPage = new PageImpl<>(firstPageEntities, pageable, allEntities.size());

        when(additionalServiceRepository.findAll(pageable)).thenReturn(additionalPage);

        // When
        List<AdditionalServiceView> result = additionalServiceService.getAllAdditionalService(page, size);

        // Then
        assertEquals(10, result.size());
        verify(additionalServiceRepository, times(1)).findAll(pageable);

        // 반환된 항목들의 내용 검증
        for (int i = 0; i < 10; i++) {
            assertEquals("test" + (i + 1), result.get(i).getName());
            assertEquals("test" + (i + 1), result.get(i).getDescription());
            assertEquals(1000, result.get(i).getPrice());
        }
    }

    @Test
    @DisplayName("[부가서비스 단일 조회] - 성공")
    void getAdditionalServiceById() {
        // given
        final Long id = 1L;

        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
                .id(1L)
                .build();

        AdditionalServiceEntity additionalServiceEntity = AdditionalServiceEntity.builder()
                .id(id)
                .price(1000)
                .description("test")
                .name("test")
                .accommodationEntity(accommodationEntity)
                .build();
        when(additionalServiceRepository.findById(id)).thenReturn(Optional.of(additionalServiceEntity));
        // When
        AdditionalServiceView result = additionalServiceService.getAdditionalServiceById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(additionalServiceEntity.getName(), result.getName());
        assertEquals(additionalServiceEntity.getDescription(), result.getDescription());
        assertEquals(additionalServiceEntity.getPrice(), result.getPrice());

        verify(additionalServiceRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("[부가서비스 수정] - 성공")
    void updateAdditionalService_success() {
        // given
        Long id = 1L;
        AdditionalServiceUpdateRequest updateRequest = new AdditionalServiceUpdateRequest();
        // updateRequest 필드 설정

        AdditionalServiceEntity existingEntity = mock(AdditionalServiceEntity.class);
        when(additionalServiceRepository.findById(id)).thenReturn(Optional.of(existingEntity));

        // When
        additionalServiceService.updateAdditionalService(id, updateRequest);

        // Then
        verify(additionalServiceRepository).findById(id);
        verify(existingEntity).updateAdditionalService(updateRequest);
        verify(additionalServiceRepository).save(existingEntity);
    }

    @Test
    @DisplayName("[부가서비스 수정] - 실패 없는 부가서비스 수정 시도")
    void updateAdditionalService_failedByAdditionalServiceNotFound() {
        // given
        Long id = 1L;
        AdditionalServiceUpdateRequest updateRequest = new AdditionalServiceUpdateRequest();
        // updateRequest 필드 설정

        AdditionalServiceEntity existingEntity = mock(AdditionalServiceEntity.class);
        when(additionalServiceRepository.findById(id)).thenReturn(Optional.of(existingEntity));

        // When
        additionalServiceService.updateAdditionalService(id, updateRequest);

        // Then
        verify(additionalServiceRepository).findById(id);
        verify(existingEntity).updateAdditionalService(updateRequest);
        verify(additionalServiceRepository).save(existingEntity);
    }


    @Test
    @DisplayName("[부가서비스 삭제] - 성공")
    void deleteAdditionalService_success() {
        // given
        final Long id = 1L;
        AdditionalServiceEntity additionalServiceEntity = mock(AdditionalServiceEntity.class);

        when(additionalServiceRepository.findById(id)).thenReturn(Optional.of(additionalServiceEntity));
        // when
        additionalServiceService.deleteAdditionalService(id);
        // then
        verify(additionalServiceRepository).delete(additionalServiceEntity);
    }

    @Test
    @DisplayName("[부가서비스 삭제] - 실패")
    void deleteAdditionalService_failedByAdditionalServiceNotFound() {
        // given
        final Long id = 1L;
        when(additionalServiceRepository.findById(id)).thenReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> additionalServiceService.deleteAdditionalService(id))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(AdditionalServiceMessageType.ADDITIONAL_SERVICE_NOT_FOUND.getMessage());


    }
}