package com.beyond.easycheck.themeparks.ui.controller;

import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase;
import com.beyond.easycheck.themeparks.application.service.ThemeParkReadUseCase;
import com.beyond.easycheck.themeparks.application.service.ThemeParkReadUseCase.FindThemeParkResult;
import com.beyond.easycheck.themeparks.ui.requestbody.ThemeParkCreateRequest;
import com.beyond.easycheck.themeparks.ui.requestbody.ThemeParkUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ThemeParkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ThemeParkOperationUseCase themeParkOperationUseCase;

    @MockBean
    private ThemeParkReadUseCase themeParkReadUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // 자원을 해제
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCreateThemeParkSuccessfully_withImage() throws Exception {
        // Given
        ThemeParkCreateRequest request = new ThemeParkCreateRequest("테마파크 1", "재미있는 테마파크", "서울");

        FindThemeParkResult result = FindThemeParkResult.builder()
                .id(1L)
                .name("테마파크 1")
                .description("재미있는 테마파크")
                .location("서울")
                .build();

        when(themeParkOperationUseCase.saveThemePark(any(), any(), any())).thenReturn(result);

        // Mocking multipart file for images
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFiles", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        // Mocking request part as JSON
        MockMultipartFile requestPart = new MockMultipartFile(
                "request", null, "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        // When & Then
        mockMvc.perform(multipart("/api/v1/accommodations/1/parks")
                        .file(imageFile)  // 이미지 파일 추가
                        .file(requestPart)  // JSON 데이터 추가
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("테마파크 1"))
                .andExpect(jsonPath("$.data.description").value("재미있는 테마파크"))
                .andExpect(jsonPath("$.data.location").value("서울"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldGetAllThemeParksSuccessfully() throws Exception {
        // Given
        FindThemeParkResult themePark1 = FindThemeParkResult.builder()
                .id(1L)
                .name("테마파크 1")
                .description("재미있는 테마파크")
                .location("서울")
                .build();

        FindThemeParkResult themePark2 = FindThemeParkResult.builder()
                .id(2L)
                .name("테마파크 2")
                .description("즐거운 테마파크")
                .location("부산")
                .build();

        List<FindThemeParkResult> themeParks = List.of(themePark1, themePark2);
        when(themeParkReadUseCase.getThemeParks(1L)).thenReturn(themeParks);

        // When & Then
        mockMvc.perform(get("/api/v1/accommodations/1/parks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("테마파크 1"))
                .andExpect(jsonPath("$.data[1].name").value("테마파크 2"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldGetThemeParkByIdSuccessfully() throws Exception {
        // Given
        FindThemeParkResult result = FindThemeParkResult.builder()
                .id(1L)
                .name("테마파크 1")
                .description("재미있는 테마파크")
                .location("서울")
                .build();

        when(themeParkReadUseCase.getFindThemePark(1L, 1L)).thenReturn(result);

        // When & Then
        mockMvc.perform(get("/api/v1/accommodations/1/parks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("테마파크 1"))
                .andExpect(jsonPath("$.data.description").value("재미있는 테마파크"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldUpdateThemeParkSuccessfully() throws Exception {
        // Given
        ThemeParkUpdateRequest request = new ThemeParkUpdateRequest("새로운 테마파크", "새로운 설명", "서울");

        // When & Then
        mockMvc.perform(patch("/api/v1/accommodations/1/parks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldUpdateThemeParkImagesSuccessfully() throws Exception {
        // Mocking multipart file for images
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFiles", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        MockMultipartFile imageIdsToDelete = new MockMultipartFile(
                "imageIdsToDelete", null, "application/json", "[1, 2]".getBytes(StandardCharsets.UTF_8));

        // When & Then
        mockMvc.perform(multipart("/api/v1/accommodations/1/parks/1/images")
                        .file(imageFile)  // 이미지 파일 추가
                        .file(imageIdsToDelete)  // 삭제할 이미지 ID 추가
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldDeleteThemeParkSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/accommodations/1/parks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
