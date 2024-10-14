package com.beyond.easycheck.events.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.events.infrastructure.entity.EventEntity;
import com.beyond.easycheck.events.infrastructure.repository.EventImageRepository;
import com.beyond.easycheck.events.infrastructure.repository.EventRepository;
import com.beyond.easycheck.events.ui.requestbody.EventCreateRequest;
import com.beyond.easycheck.events.ui.requestbody.EventUpdateRequest;
import com.beyond.easycheck.events.ui.view.EventView;
import com.beyond.easycheck.s3.application.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.beyond.easycheck.accomodations.exception.AccommodationMessageType.ACCOMMODATION_NOT_FOUND;
import static com.beyond.easycheck.events.exception.EventMessageType.*;
import static com.beyond.easycheck.s3.application.domain.FileManagementCategory.EVENT;
import static com.beyond.easycheck.s3.application.domain.FileManagementCategory.ROOM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class EventServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventImageRepository eventImageRepository;

    @InjectMocks
    private EventService eventService;

    AccommodationEntity accommodationEntity;

    @BeforeEach
    void setUp() {
        accommodationEntity = new AccommodationEntity(
                1L,
                "선셋 리조트",
                "123 해변로, 오션 시티",
                AccommodationType.RESORT
        );

        when(accommodationRepository.findById(accommodationEntity.getId())).thenReturn(Optional.of(accommodationEntity));

    }

    @Test
    @DisplayName("이벤트 생성 성공")
    void createEvent_success() {
        // Given
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationEntity));

        EventCreateRequest eventCreateRequest = new EventCreateRequest(
                accommodationEntity.getId(),
                "수영장 파티",
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        List<MultipartFile> imageFiles = new ArrayList<>();
        imageFiles.add(mock(MultipartFile.class));
        imageFiles.add(mock(MultipartFile.class));

        List<String> imageUrls = List.of("url1", "url2");
        when(s3Service.uploadFiles(imageFiles, EVENT)).thenReturn(imageUrls);

        List<EventEntity.ImageEntity> eventImages = new ArrayList<>();
        for (String url : imageUrls) {
            eventImages.add(new EventEntity.ImageEntity(null, url, null));
        }

        EventEntity eventEntity = EventEntity.builder()
                .id(1L)
                .accommodationEntity(accommodationEntity)
                .images(eventImages)
                .eventName("수영장 파티")
                .detail("여름을 맞아 즐기는 시원한 수영장 파티")
                .startDate(LocalDate.of(2024, 6, 1))
                .endDate(LocalDate.of(2024, 8, 31))
                .build();

        for (EventEntity.ImageEntity image : eventImages) {
            image.setEvent(eventEntity);
        }

        when(eventRepository.save(any(EventEntity.class))).thenReturn(eventEntity);

        // When
        EventEntity createdEvent = eventService.createEvent(eventCreateRequest, imageFiles);

        // Then
        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getImages()).hasSize(2);
        assertThat(createdEvent.getImages()).extracting("url").containsExactlyInAnyOrderElementsOf(imageUrls);

        // Verify
        verify(accommodationRepository).findById(1L);
        verify(s3Service).uploadFiles(imageFiles, EVENT);
        verify(eventRepository, times(1)).save(any(EventEntity.class));

    }

    @Test
    @DisplayName("이벤트 생성 실패 - 잘못된 accommodationId")
    void createEvent_fail_wrongAccommodationId() {
        // Given
        EventCreateRequest eventCreateRequest = new EventCreateRequest(
                999L,
                "수영장 파티",
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        List<MultipartFile> imageFiles = new ArrayList<>();
        imageFiles.add(mock(MultipartFile.class));
        imageFiles.add(mock(MultipartFile.class));

        // When & Then
        assertThatThrownBy(() -> eventService.createEvent(eventCreateRequest, imageFiles))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(ACCOMMODATION_NOT_FOUND.getMessage());

        // Verify
        verify(accommodationRepository).findById(999L);
        verify(s3Service, never()).uploadFiles(anyList(), any());
        verify(eventRepository, never()).save(any(EventEntity.class));
    }

    @Test
    @DisplayName("이벤트 생성 실패 - 잘못된 입력값")
    void createEvent_fail_wrongValue() {
        // Given
        EventCreateRequest eventCreateRequest = new EventCreateRequest(
                1L,
                null,
                null,
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        List<MultipartFile> imageFiles = new ArrayList<>();
        imageFiles.add(mock(MultipartFile.class));
        imageFiles.add(mock(MultipartFile.class));

        // When & Then
        assertThatThrownBy(() -> eventService.createEvent(eventCreateRequest, imageFiles))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(ARGUMENT_NOT_VALID.getMessage());

        // Verify
        verify(accommodationRepository).findById(1L);
        verify(s3Service, never()).uploadFiles(anyList(), any());
        verify(eventRepository, never()).save(any(EventEntity.class));
    }

    @Test
    @DisplayName("이벤트 단일 조회 성공")
    void readEvent_success() {
        // Given
        EventEntity eventEntity = new EventEntity(
                1L,
                accommodationEntity,
                "수영장 파티",
                new ArrayList<>(),
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        EventEntity.ImageEntity image1 = new EventEntity.ImageEntity(1L, "url1", eventEntity);
        EventEntity.ImageEntity image2 = new EventEntity.ImageEntity(2L, "url2", eventEntity);
        eventEntity.addImage(image1);
        eventEntity.addImage(image2);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(eventEntity));

        // When
        EventView readEvent = eventService.readEvent(1L);

        // Then
        assertThat(readEvent).isNotNull();
        assertThat(readEvent.getImages()).hasSize(2);
        assertThat(readEvent.getImages()).containsExactlyInAnyOrder("url1", "url2");
    }

    @Test
    @DisplayName("이벤트 단일 조회 실패 - 존재하지 않는 eventId")
    void readEvent_fail() {
        // Given
        Long eventId = 999L;

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationEntity));
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.readEvent(eventId))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(EVENT_NOT_FOUND.getMessage());

        verify(eventRepository).findById(eventId);

    }

    @Test
    @DisplayName("이벤트 전체 조회 성공")
    void readEvents_success() {
        // Given
        EventEntity event1 = new EventEntity(
                1L,
                accommodationEntity,
                "수영장 파티",
                new ArrayList<>(),
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        EventEntity event2 = new EventEntity(
                2L,
                accommodationEntity,
                "스파 체험 주간",
                new ArrayList<>(),
                "전문가와 함께하는 특별한 스파 체험",
                LocalDate.of(2024, 7, 10),
                LocalDate.of(2024, 7, 17)
        );

        List<EventEntity> eventEntities = Arrays.asList(event1, event2);
        when(eventRepository.findAll()).thenReturn(eventEntities);

        // When
        List<EventView> eventViews = eventService.readEvents();

        // Then
        assertThat(eventViews).hasSize(2);
        assertThat(eventViews.get(0).getId()).isEqualTo(event1.getId());
        assertThat(eventViews.get(1).getId()).isEqualTo(event2.getId());

        // Verify
        verify(eventRepository).findAll();
    }

    @Test
    @DisplayName("이벤트 전체 조회 실패 - 빈 리스트")
    void readEvents_fail() {
        // Given
        when(eventRepository.findAll()).thenThrow(new EasyCheckException(EVENTS_NOT_FOUND));

        // When & Then
        assertThatThrownBy(() -> eventService.readEvents())
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(EVENTS_NOT_FOUND.getMessage());

        // Verify
        verify(eventRepository).findAll();
    }

    @Test
    @DisplayName("이벤트 정보 수정 성공")
    void updateEvent_success() {
        // Given
        EventEntity existingEvent = new EventEntity(
                1L,
                accommodationEntity,
                "수영장 파티",
                new ArrayList<>(),
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        EventUpdateRequest updateEvent = new EventUpdateRequest(
                accommodationEntity.getId(),
                "와인 시음회",
                "세계 각국의 와인을 즐길 수 있는 시음회",
                LocalDate.of(2024, 8, 15),
                LocalDate.of(2024, 8, 20)
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));

        // When
        eventService.updateEvent(1L, updateEvent);

        // Then
        assertThat(existingEvent.getAccommodationEntity().getId()).isEqualTo(accommodationEntity.getId());
        assertThat(existingEvent.getEventName()).isEqualTo("와인 시음회");
        assertThat(existingEvent.getDetail()).isEqualTo("세계 각국의 와인을 즐길 수 있는 시음회");
        assertThat(existingEvent.getStartDate()).isEqualTo(LocalDate.of(2024, 8, 15));
        assertThat(existingEvent.getEndDate()).isEqualTo(LocalDate.of(2024, 8, 20));

        // Verify
        verify(eventRepository).findById(1L);
    }

    @Test
    @DisplayName("이벤트 수정 실패 - 잘못된 accommodationId")
    void updateEvent_fail_wrongAccommodationId() {
        // Given
        EventEntity eventEntity = new EventEntity(
                1L,
                accommodationEntity,
                "수영장 파티",
                new ArrayList<>(),
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        EventUpdateRequest eventUpdateRequest = new EventUpdateRequest(
                999L,
                "와인 시음회",
                "세계 각국의 와인을 즐길 수 있는 시음회",
                LocalDate.of(2024, 8, 15),
                LocalDate.of(2024, 8, 20)
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(eventEntity));

        // When & Then
        assertThatThrownBy(() -> eventService.updateEvent(1L, eventUpdateRequest))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(ACCOMMODATION_NOT_FOUND.getMessage());

        verify(eventRepository).findById(1L);
        verify(eventRepository, never()).save(any(EventEntity.class));
    }

    @Test
    @DisplayName("이벤트 수정 실패 - 잘못된 입력값")
    void updateEvent_fail_wrongValue() {
        // Given
        EventEntity eventEntity = new EventEntity(
                1L,
                accommodationEntity,
                "수영장 파티",
                new ArrayList<>(),
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        EventUpdateRequest eventUpdateRequest = new EventUpdateRequest(
                1L,
                null,
                null,
                null,
                null
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(eventEntity));

        // When & Then
        assertThatThrownBy(() -> eventService.updateEvent(1L, eventUpdateRequest))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(ARGUMENT_NOT_VALID.getMessage());

        // Verify
        verify(eventRepository).findById(1L);
        verify(eventRepository, never()).save(any(EventEntity.class));
    }

    @Test
    @DisplayName("이벤트 사진 수정 성공")
    void updateEventImage_success() {
        // Given
        Long imageId = 1L;
        MultipartFile newImageFile = mock(MultipartFile.class);
        String oldImageUrl = "s3://bucket/old/image.jpg";
        String newImageUrl = "s3://bucket/new/image.jpg";

        EventEntity.ImageEntity imageEntity = new EventEntity.ImageEntity(imageId, oldImageUrl, null);

        when(eventImageRepository.findById(imageId)).thenReturn(Optional.of(imageEntity));
        when(s3Service.uploadFile(newImageFile, ROOM)).thenReturn(newImageUrl);

        // When
        eventService.updateEventImage(imageId, newImageFile);

        // Then
        assertThat(imageEntity.getUrl()).isEqualTo(newImageUrl);
        verify(s3Service).deleteFile("old/image.jpg");
        verify(eventImageRepository).findById(imageId);
    }

    @Test
    @DisplayName("이벤트 사진 수정 실패 - 존재하지 않는 imageID")
    void updateEventImage_fail() {
        // Given
        Long imageId = 999L;
        MultipartFile newImageFile = mock(MultipartFile.class);

        when(eventImageRepository.findById(imageId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.updateEventImage(imageId, newImageFile))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(IMAGE_NOT_FOUND.getMessage());

        verify(eventImageRepository).findById(imageId);
        verify(s3Service, never()).deleteFile(anyString());
        verify(s3Service, never()).uploadFile(any(MultipartFile.class), eq(EVENT));
    }

    @Test
    @DisplayName("이벤트 삭제 성공")
    void deleteEvent_success() {
        // Given
        Long eventId = 1L;
        String oldImageUrl1 = "s3://bucket/old/image1.jpg";
        String oldImageUrl2 = "s3://bucket/old/image2.jpg";

        EventEntity.ImageEntity image1 = new EventEntity.ImageEntity(1L, oldImageUrl1, null);
        EventEntity.ImageEntity image2 = new EventEntity.ImageEntity(2L, oldImageUrl2, null);

        List<EventEntity.ImageEntity> images = Arrays.asList(image1, image2);
        EventEntity eventEntity = new EventEntity(
                eventId,
                accommodationEntity,
                "수영장 파티",
                images,
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventEntity));

        // When
        eventService.deleteEvent(eventId);

        // Then
        verify(s3Service).deleteFile("old/image1.jpg");
        verify(s3Service).deleteFile("old/image2.jpg");
        verify(eventRepository).delete(eventEntity);
    }

    @Test
    @DisplayName("이벤트 정보 삭제 실패 - 잘못된 eventID")
    void deleteEvent_fail() {
        // Given
        Long eventId = 999L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.deleteEvent(eventId))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(EVENT_NOT_FOUND.getMessage());

        // Verify
        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).delete(any(EventEntity.class));
    }

}
