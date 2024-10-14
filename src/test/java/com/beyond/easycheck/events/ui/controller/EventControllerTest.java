package com.beyond.easycheck.events.ui.controller;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.events.application.service.EventService;
import com.beyond.easycheck.events.infrastructure.entity.EventEntity;
import com.beyond.easycheck.events.infrastructure.repository.EventImageRepository;
import com.beyond.easycheck.events.infrastructure.repository.EventRepository;
import com.beyond.easycheck.events.ui.requestbody.EventCreateRequest;
import com.beyond.easycheck.events.ui.requestbody.EventUpdateRequest;
import com.beyond.easycheck.events.ui.view.EventView;
import com.beyond.easycheck.s3.application.service.S3Service;
import com.beyond.easycheck.user.application.mock.WithEasyCheckMockUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

import static com.beyond.easycheck.accomodations.exception.AccommodationMessageType.ACCOMMODATION_NOT_FOUND;
import static com.beyond.easycheck.events.exception.EventMessageType.*;
import static com.beyond.easycheck.s3.application.domain.FileManagementCategory.EVENT;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    S3Service s3Service;

    @MockBean
    EventService eventService;

    @MockBean
    EventRepository eventRepository;

    @MockBean
    AccommodationRepository accommodationRepository;

    @MockBean
    EventImageRepository eventImageRepository;

    AccommodationEntity accommodationEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accommodationEntity = new AccommodationEntity(
                1L,
                "선셋 리조트",
                "123 해변로, 오션 시티",
                AccommodationType.RESORT
        );

        Long imageId = 1L;
        EventEntity.ImageEntity imageEntity = new EventEntity.ImageEntity();
        imageEntity.setId(imageId);

        when(eventImageRepository.findById(imageId)).thenReturn(Optional.of(imageEntity));
        when(accommodationRepository.findById(accommodationEntity.getId())).thenReturn(Optional.of(accommodationEntity));

    }

    @Test
    @DisplayName("이벤트 생성 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createEvent() throws Exception {
        // Given
        EventCreateRequest eventCreateRequest = new EventCreateRequest(
                accommodationEntity.getId(),
                "수영장 파티",
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        List<MultipartFile> imageFiles = new ArrayList<>();
        imageFiles.add(new MockMultipartFile("Image", "roomPic1.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3}));
        imageFiles.add(new MockMultipartFile("Image", "roomPic2.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{4, 5, 6}));

        List<String> imageUrls = List.of("url1", "url2");
        when(s3Service.uploadFiles(anyList(), eq(EVENT))).thenReturn(imageUrls);

        EventEntity eventEntity = new EventEntity(
                1L,
                accommodationEntity,
                "수영장 파티",
                new ArrayList<>(),
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        when(eventService.createEvent(any(EventCreateRequest.class), anyList())).thenReturn(eventEntity);

        // When
        ResultActions perform = mockMvc.perform(
                multipart("/api/v1/events")
                        .file("Image", imageFiles.get(0).getBytes())
                        .file("Image", imageFiles.get(1).getBytes())
                        .file("description", objectMapper.writeValueAsBytes(eventCreateRequest))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // Then
        perform.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accommodationEntity.id").value(1L));
    }

    @Test
    @DisplayName("이벤트 생성 실패 - 존재하지 않는 accommodationID")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createEvent_fail_wrongAccommodationId() throws Exception {
        // Given
        Long accommodationId = 999L;

        EventCreateRequest eventCreateRequest = new EventCreateRequest(
                accommodationId,
                "수영장 파티",
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        when(eventService.createEvent(any(EventCreateRequest.class), anyList()))
                .thenThrow(new EasyCheckException(ACCOMMODATION_NOT_FOUND));

        // When
        ResultActions perform = mockMvc.perform(
                multipart("/api/v1/events")
                        .file("Image", new byte[]{1, 2, 3})
                        .file("Image", new byte[]{4, 5, 6})
                        .file("description", objectMapper.writeValueAsBytes(eventCreateRequest))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0].errorType").value(ACCOMMODATION_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(ACCOMMODATION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("이벤트 생성 실패 - 잘못된 입력값")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void createEvent_fail_wrongValue() throws Exception {
        // Given
        Long accommodationId = 1L;
        EventCreateRequest eventCreateRequest = new EventCreateRequest(
                accommodationId,
                null,
                null,
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        // When
        ResultActions perform = mockMvc.perform(multipart("/api/v1/events")
                .file("Image", new byte[]{1, 2, 3})
                .file("Image", new byte[]{4, 5, 6})
                .file("description", objectMapper.writeValueAsBytes(eventCreateRequest))
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0].errorType").value(ARGUMENT_NOT_VALID.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(ARGUMENT_NOT_VALID.getMessage()));

    }

    @Test
    @DisplayName("이벤트 단일 조회 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readEvent_success() throws Exception {
        // Given
        Long id = 1L;

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

        EventView eventView = new EventView(
                id,
                "선셋 리조트",
                List.of("url1", "url2"),
                "수영장 파티",
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        when(eventService.readEvent(id)).thenReturn(eventView);

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/events/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @DisplayName("이벤트 단일 조회 실패 - 존재하지 않는 이벤트 ID")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readEvent_fail() throws Exception {
        // Given
        Long id = 999L;
        when(eventService.readEvent(id)).thenThrow(new EasyCheckException(EVENT_NOT_FOUND));

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/events/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].errorType").value(EVENT_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(EVENT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("이벤트 전체 조회 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readEvents_success() throws Exception {
        // Given
        EventView event1 = new EventView(
                1L,
                "선셋 리조트",
                List.of("url1", "url2"),
                "수영장 파티",
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        EventView event2 = new EventView(
                2L,
                "선셋 리조트",
                List.of("url1", "url2"),
                "스파 체험 주간",
                "전문가와 함께하는 특별한 스파 체험",
                LocalDate.of(2024, 7, 10),
                LocalDate.of(2024, 7, 17)
        );

        List<EventView> eventViews = Arrays.asList(event1, event2);

        when(eventService.readEvents()).thenReturn(eventViews);

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("이벤트 전체 조회 실패 - 빈 리스트")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void readEvents_fail() throws Exception {
        // Given
        when(eventService.readEvents()).thenReturn(Collections.emptyList());

        // When
        ResultActions perform = mockMvc.perform(get("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("이벤트 수정 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateEvent_success() throws Exception {
        // Given
        Long eventId = 1L;
        EventUpdateRequest updateEventRequest = new EventUpdateRequest(
                accommodationEntity.getId(),
                "와인 시음회",
                "세계 각국의 와인을 즐길 수 있는 시음회",
                LocalDate.of(2024, 8, 15),
                LocalDate.of(2024, 8, 20)
        );

        // When
        ResultActions perform = mockMvc.perform(patch("/api/v1/events/{id}", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateEventRequest)));

        // Then
        perform.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("이벤트 수정 실패 - 잘못된 accommodationId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateEvent_fail_wrongAccommodationId() throws Exception {
        // Given
        Long eventId = 1L;
        EventUpdateRequest updateEventRequest = new EventUpdateRequest(
                999L,
                "와인 시음회",
                "세계 각국의 와인을 즐길 수 있는 시음회",
                LocalDate.of(2024, 8, 15),
                LocalDate.of(2024, 8, 20)
        );

        // When
        ResultActions perform = mockMvc.perform(patch("/api/v1/events/{id}", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateEventRequest)));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(ACCOMMODATION_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(ACCOMMODATION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("이벤트 수정 실패 - 잘못된 입력값")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateEvent_fail_wrongValue() throws Exception {
        Long eventId = 1L;
        EventUpdateRequest updateEventRequest = new EventUpdateRequest(
                1L,
                null,
                null,
                LocalDate.of(2024, 8, 15),
                LocalDate.of(2024, 8, 20)
        );

        // When
        ResultActions perform = mockMvc.perform(patch("/api/v1/events/{id}", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateEventRequest)));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorType").value(ARGUMENT_NOT_VALID.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(ARGUMENT_NOT_VALID.getMessage()));
    }

    @Test
    @DisplayName("이벤트 사진 수정 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateEventImage_success() throws Exception {
        // Given
        Long imageId = 1L;
        MockMultipartFile newImageFile = new MockMultipartFile("newImageFile", "newImage.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());

        doNothing().when(eventService).updateEventImage(eq(imageId), any(MultipartFile.class));

        // When
        ResultActions perform = mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/events/images/{imageId}", imageId)
                .file(newImageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        perform.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("이벤트 사진 수정 실패 - 존재하지 않는 imageId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void updateEventImage_fail() throws Exception {
        // Given
        Long imageId = 1L;
        MockMultipartFile newImageFile = new MockMultipartFile("newImageFile", "newImage.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());

        doThrow(new EasyCheckException(IMAGE_NOT_FOUND)).when(eventService).updateEventImage(imageId, newImageFile);

        // When
        ResultActions perform = mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/events/images/{imageId}", imageId)
                .file(newImageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0].errorType").value(IMAGE_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(IMAGE_NOT_FOUND.getMessage()));
    }


    @Test
    @DisplayName("이벤트 삭제 성공")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void deleteEvent_success() throws Exception {
        // Given
        Long eventId = 1L;

        EventEntity eventEntity = new EventEntity(
                1L,
                accommodationEntity,
                "수영장 파티",
                new ArrayList<>(),
                "여름을 맞아 즐기는 시원한 수영장 파티",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        EventEntity.ImageEntity image1 = new EventEntity.ImageEntity();
        image1.setUrl("https://example.com/images/event/image1.jpg");
        EventEntity.ImageEntity image2 = new EventEntity.ImageEntity();
        image2.setUrl("https://example.com/images/event/image2.jpg");
        eventEntity.setImages(Arrays.asList(image1, image2));

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventEntity));
        doNothing().when(s3Service).deleteFile(any(String.class));
        doNothing().when(eventRepository).delete(any(EventEntity.class));

        // When
        ResultActions perform = mockMvc.perform(delete("/api/v1/events/{id}", eventId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("이벤트 삭제 실패 - 존재하지 않는 eventId")
    @WithEasyCheckMockUser(role = "SUPER_ADMIN")
    void deleteEvent_fail() throws Exception {
        // Given
        Long eventId = 999L;

        doThrow(new EasyCheckException(EVENT_NOT_FOUND)).when(eventService).deleteEvent(eventId);

        // When
        ResultActions perform = mockMvc.perform(delete("/api/v1/events/{id}", eventId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(EVENT_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(EVENT_NOT_FOUND.getMessage()));
    }

}
