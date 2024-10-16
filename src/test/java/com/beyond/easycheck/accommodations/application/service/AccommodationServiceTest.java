//package com.beyond.easycheck.accommodations.application.service;
//
//import com.beyond.easycheck.accomodations.application.service.AccommodationService;
//import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
//import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
//import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
//import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationCreateRequest;
//import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationUpdateRequest;
//import com.beyond.easycheck.accomodations.ui.view.AccommodationView;
//import com.beyond.easycheck.common.exception.EasyCheckException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//
//public class AccommodationServiceTest {
//
//    @Mock
//    private AccommodationRepository accommodationRepository;
//
//    @InjectMocks
//    private AccommodationService accommodationService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testCreateAccommodation() {
//        // given
//        AccommodationCreateRequest request = new AccommodationCreateRequest("Test Resort", "Test Address", AccommodationType.RESORT);
//        AccommodationEntity accommodationEntity = AccommodationEntity.builder()
//                .name("Test Resort")
//                .address("Test Address")
//                .accommodationType(AccommodationType.RESORT)
//                .build();
//
//        given(accommodationRepository.save(any(AccommodationEntity.class))).willReturn(accommodationEntity);
//
//        // when
//        Optional<AccommodationEntity> result = accommodationService.createAccommodation(request);
//
//        // then
//        assertThat(result.isPresent()).isTrue();
//        assertThat(result.get().getName()).isEqualTo("Test Resort");
//        verify(accommodationRepository).save(any(AccommodationEntity.class));
//    }
//
//    @Test
//    public void testCreateAccommodation_WithInvalidData() {
//        // given
//        AccommodationCreateRequest request = new AccommodationCreateRequest("", "", null); // 잘못된 요청
//        given(accommodationRepository.save(any(AccommodationEntity.class)))
//                .willThrow(new DataIntegrityViolationException("Invalid data"));
//
//        // when & then
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            accommodationService.createAccommodation(request);
//        });
//    }
//
//    @Test
//    public void testGetAllAccommodations() {
//        // given
//        AccommodationEntity accommodation1 = AccommodationEntity.builder().id(1L).name("Resort 1").build();
//        AccommodationEntity accommodation2 = AccommodationEntity.builder().id(2L).name("Resort 2").build();
//        List<AccommodationEntity> accommodations = Arrays.asList(accommodation1, accommodation2);
//        Page<AccommodationEntity> accommodationPage = new PageImpl<>(accommodations);
//
//        Pageable pageable = PageRequest.of(0, 10);
//
//        given(accommodationRepository.findAll(pageable)).willReturn(accommodationPage);
//
//        // when
//        List<AccommodationView> result = accommodationService.getAllAccommodations(0, 10);
//
//        // then
//        assertThat(result.size()).isEqualTo(2);
//        assertThat(result.get(0).getName()).isEqualTo("Resort 1");
//        verify(accommodationRepository).findAll(pageable);
//    }
//
//    @Test
//    public void testGetAccommodationById_ExistingId() {
//        // given
//        AccommodationEntity accommodationEntity = AccommodationEntity.builder().id(1L).name("Test Resort").build();
//        given(accommodationRepository.findById(1L)).willReturn(Optional.of(accommodationEntity));
//
//        // when
//        AccommodationView result = accommodationService.getAccommodationById(1L);
//
//        // then
//        assertThat(result.getName()).isEqualTo("Test Resort");
//        verify(accommodationRepository).findById(1L);
//    }
//
//    @Test
//    public void testGetAccommodationById_NonExistingId() {
//        // given
//        given(accommodationRepository.findById(1L)).willReturn(Optional.empty());
//
//        // when & then
//        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> {
//            accommodationService.getAccommodationById(1L);
//        });
//        assertThat(exception.getMessage()).isEqualTo("Accommodation not found");
//        verify(accommodationRepository).findById(1L);
//    }
//
//    @Test
//    public void testUpdateAccommodation() {
//        // given
//        AccommodationEntity existingAccommodation = AccommodationEntity.builder().id(1L).name("Old Name").build();
//        AccommodationUpdateRequest updateRequest = new AccommodationUpdateRequest("New Name", "New Address", AccommodationType.HOTEL);
//
//        given(accommodationRepository.findById(1L)).willReturn(Optional.of(existingAccommodation));
//
//        // when
//        accommodationService.updateAccommodation(1L, updateRequest);
//
//        // then
//        assertThat(existingAccommodation.getName()).isEqualTo("New Name");
//        verify(accommodationRepository).save(existingAccommodation);
//    }
//
//    @Test
//    public void testUpdateAccommodation_NonExistingId() {
//        // given
//        AccommodationUpdateRequest updateRequest = new AccommodationUpdateRequest("New Name", "New Address", AccommodationType.HOTEL);
//        given(accommodationRepository.findById(1L)).willReturn(Optional.empty());
//
//        // when & then
//        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> {
//            accommodationService.updateAccommodation(1L, updateRequest);
//        });
//        assertThat(exception.getMessage()).isEqualTo("Accommodation not found");
//        verify(accommodationRepository).findById(1L);
//    }
//
//    @Test
//    public void testDeleteAccommodation() {
//        // given
//        AccommodationEntity accommodationEntity = AccommodationEntity.builder().id(1L).build();
//        given(accommodationRepository.findById(1L)).willReturn(Optional.of(accommodationEntity));
//
//        // when
//        accommodationService.deleteAccommodation(1L);
//
//        // then
//        verify(accommodationRepository).delete(accommodationEntity);
//    }
//
//    @Test
//    public void testDeleteAccommodation_NonExistingId() {
//        // given
//        given(accommodationRepository.findById(1L)).willReturn(Optional.empty());
//
//        // when & then
//        EasyCheckException exception = assertThrows(EasyCheckException.class, () -> {
//            accommodationService.deleteAccommodation(1L);
//        });
//        assertThat(exception.getMessage()).isEqualTo("Accommodation not found");
//        verify(accommodationRepository).findById(1L);
//    }
//}