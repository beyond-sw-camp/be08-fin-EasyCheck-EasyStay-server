package com.beyond.easycheck.facilities.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.facilities.exception.FacilityMessageType;
import com.beyond.easycheck.facilities.infrastructure.entity.AvailableStatus;
import com.beyond.easycheck.facilities.infrastructure.entity.FacilityEntity;
import com.beyond.easycheck.facilities.infrastructure.repository.FacilityRepository;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityCreateRequest;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityUpdateRequest;
import com.beyond.easycheck.facilities.ui.view.FacilityView;
import com.beyond.easycheck.s3.application.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.beyond.easycheck.s3.application.domain.FileManagementCategory.FACILITY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FacilityServiceTest {

    @InjectMocks
    private FacilityService facilityService;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFacility_success() {

        // given
        FacilityCreateRequest request = new FacilityCreateRequest(1L, "New Facility", "Description", AvailableStatus.YES);
        List<MultipartFile> imageFiles = List.of();

        AccommodationEntity accommodation = new AccommodationEntity();
        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.of(accommodation));
        when(s3Service.uploadFiles(imageFiles, FACILITY)).thenReturn(List.of("image1.jpg", "image2.jpg"));

        FacilityEntity facility = mock(FacilityEntity.class);
        when(facilityRepository.save(any(FacilityEntity.class))).thenReturn(facility);

        // when
        FacilityEntity result = facilityService.createFacility(request, imageFiles);

        // then
        assertNotNull(result);
        verify(facilityRepository, times(2)).save(any(FacilityEntity.class));
        verify(s3Service).uploadFiles(imageFiles, FACILITY);
    }

    @Test
    void createFacility_failure_accommodationNotFound() {

        // given
        FacilityCreateRequest request = new FacilityCreateRequest(1L, "New Facility", "Description", AvailableStatus.YES);
        List<MultipartFile> imageFiles = List.of();

        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> {
            facilityService.createFacility(request, imageFiles);
        });

        assertEquals("Accommodation not found", exception.getMessage());
        verify(accommodationRepository).findById(anyLong());
        verify(s3Service, never()).uploadFiles(any(), any());
        verify(facilityRepository, never()).save(any());
    }

    @Test
    void getFacilityById_success() {

        // given
        AccommodationEntity accommodation = AccommodationEntity.builder()
                .name("Test Accommodation")
                .build();

        FacilityEntity facility = FacilityEntity.builder()
                .accommodationEntity(accommodation)
                .images(new ArrayList<>())
                .build();

        when(facilityRepository.findById(anyLong())).thenReturn(Optional.of(facility));

        // when
        FacilityView result = facilityService.getFacilityById(1L);

        // then
        assertNotNull(result);
        verify(facilityRepository).findById(1L);
    }

    @Test
    void getFacilityById_failure_facilityNotFound() {

        // given
        when(facilityRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> {
            facilityService.getFacilityById(1L);
        });

        assertEquals(FacilityMessageType.FACILITY_NOT_FOUND.getMessage(), exception.getMessage());
        verify(facilityRepository).findById(1L);
    }

    @Test
    void updateFacility_success() {

        // given
        FacilityUpdateRequest updateRequest = new FacilityUpdateRequest(
                "Updated Facility",
                "Updated Description",
                AvailableStatus.YES
        );
        FacilityEntity facility = mock(FacilityEntity.class);
        when(facilityRepository.findById(anyLong())).thenReturn(Optional.of(facility));

        // when
        facilityService.updateFacility(1L, updateRequest);

        // then
        verify(facilityRepository).findById(1L);
        verify(facility).updateFacility(updateRequest);
        verify(facilityRepository).save(facility);
    }

    @Test
    void updateFacility_failure_facilityNotFound() {

        // given
        FacilityUpdateRequest updateRequest = new FacilityUpdateRequest(
                "Updated Facility",
                "Updated Description",
                AvailableStatus.YES
        );
        when(facilityRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> {
            facilityService.updateFacility(1L, updateRequest);
        });

        assertEquals("Facility not found", exception.getMessage());
        verify(facilityRepository).findById(1L);
        verify(facilityRepository, never()).save(any());
    }
}