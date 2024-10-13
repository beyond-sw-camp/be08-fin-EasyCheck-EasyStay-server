package com.beyond.easycheck.facilities.ui.controller;

import com.beyond.easycheck.facilities.application.service.FacilityService;
import com.beyond.easycheck.facilities.infrastructure.entity.AvailableStatus;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityCreateRequest;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityUpdateRequest;
import com.beyond.easycheck.facilities.ui.view.FacilityView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class FacilityControllerTest {

    @Mock
    private FacilityService facilityService;

    @InjectMocks
    private FacilityController facilityController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFacility_success() {
        // given
        FacilityCreateRequest request = new FacilityCreateRequest(1L, "Pool", "Nice swimming pool", AvailableStatus.YES);
        List<MultipartFile> imageFiles = new ArrayList<>();
        imageFiles.add(new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{}));

        // when
        ResponseEntity<Void> response = facilityController.createFacility(request, imageFiles);

        // then
        verify(facilityService).createFacility(request, imageFiles);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void createFacility_fail() {
        // given
        FacilityCreateRequest request = new FacilityCreateRequest(1L, "Pool", "Nice swimming pool", AvailableStatus.YES);
        List<MultipartFile> imageFiles = new ArrayList<>();
        imageFiles.add(new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{}));

        doThrow(new RuntimeException("Creation Failed")).when(facilityService).createFacility(any(FacilityCreateRequest.class), any(List.class));

        // when
        Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> facilityController.createFacility(request, imageFiles));

        // then
        verify(facilityService).createFacility(request, imageFiles);
        assertThat(thrown).isInstanceOf(RuntimeException.class).hasMessageContaining("Creation Failed");
    }

    @Test
    void getAllFacilities_success() {
        // given
        List<FacilityView> mockFacilities = List.of(
                new FacilityView(1L, "Gym", List.of("image1.jpg"), "Spacious gym", "Hotel", AvailableStatus.YES),
                new FacilityView(2L, "Pool", List.of("image2.jpg"), "Outdoor pool", "Resort", AvailableStatus.YES)
        );
        given(facilityService.getAllFacilities(0, 10)).willReturn(mockFacilities);

        // when
        ResponseEntity<List<FacilityView>> response = facilityController.getAllFacilities(0, 10);

        // then
        verify(facilityService).getAllFacilities(0, 10);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getFacilityById_success() {
        // given
        FacilityView mockFacility = new FacilityView(1L, "Gym", List.of("image1.jpg"), "Spacious gym", "Hotel", AvailableStatus.YES);
        given(facilityService.getFacilityById(1L)).willReturn(mockFacility);

        // when
        ResponseEntity<FacilityView> response = facilityController.getFacilityById(1L);

        // then
        verify(facilityService).getFacilityById(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockFacility);
    }

    @Test
    void getFacilityById_fail() {
        // given
        given(facilityService.getFacilityById(1L)).willThrow(new RuntimeException("Facility Not Found"));

        // when
        Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> facilityController.getFacilityById(1L));

        // then
        verify(facilityService).getFacilityById(1L);
        assertThat(thrown).isInstanceOf(RuntimeException.class).hasMessageContaining("Facility Not Found");
    }

    @Test
    void updateFacility_success() {
        // given
        FacilityUpdateRequest updateRequest = new FacilityUpdateRequest("Updated Gym", "Updated description", AvailableStatus.NO);
        FacilityView updatedFacility = new FacilityView(1L, "Updated Gym", List.of("image1.jpg"), "Updated description", "Hotel", AvailableStatus.NO);

        given(facilityService.getFacilityById(1L)).willReturn(updatedFacility);

        // when
        ResponseEntity<FacilityView> response = facilityController.updateFacility(1L, updateRequest);

        // then
        verify(facilityService).updateFacility(1L, updateRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedFacility);
    }

    @Test
    void updateFacility_fail() {
        // given
        FacilityUpdateRequest updateRequest = new FacilityUpdateRequest("Updated Gym", "Updated description", AvailableStatus.NO);

        doThrow(new RuntimeException("Update Failed")).when(facilityService).updateFacility(eq(1L), any(FacilityUpdateRequest.class));

        // when
        Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> facilityController.updateFacility(1L, updateRequest));

        // then
        verify(facilityService).updateFacility(eq(1L), any(FacilityUpdateRequest.class));
        assertThat(thrown).isInstanceOf(RuntimeException.class).hasMessageContaining("Update Failed");
    }

    @Test
    void deleteFacility_success() {
        // when
        ResponseEntity<Void> response = facilityController.deleteFacility(1L);

        // then
        verify(facilityService).deleteFacility(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteFacility_fail() {
        // given
        doThrow(new RuntimeException("Deletion Failed")).when(facilityService).deleteFacility(1L);

        // when
        Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> facilityController.deleteFacility(1L));

        // then
        verify(facilityService).deleteFacility(1L);
        assertThat(thrown).isInstanceOf(RuntimeException.class).hasMessageContaining("Deletion Failed");
    }
}