package com.beyond.easycheck.attractions.ui.controller;

import com.beyond.easycheck.attractions.application.service.AttractionOperationUseCase;
import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase;
import com.beyond.easycheck.attractions.application.service.AttractionReadUseCase.FindAttractionResult;
import com.beyond.easycheck.attractions.ui.requestbody.AttractionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AttractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttractionOperationUseCase attractionOperationUseCase;

    @MockBean
    private AttractionReadUseCase attractionReadUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCreateAttractionSuccessfully() throws Exception {
        // Given
        MockMultipartFile mockImage = new MockMultipartFile("imageFiles", "test-image.jpg", "image/jpeg", "test image content".getBytes());

        String requestData = "{\"name\": \"롤러코스터\", \"description\": \"빠른 롤러코스터\", \"themeParkId\": 1}";

        MockMultipartFile request = new MockMultipartFile("request", "", "application/json", requestData.getBytes(StandardCharsets.UTF_8));

        FindAttractionResult result = FindAttractionResult.builder()
                .id(1L)
                .name("롤러코스터")
                .description("빠른 롤러코스터")
                .themeParkId(1L)
                .imageUrls(List.of("이미지_주소"))
                .build();

        when(attractionOperationUseCase.createAttraction(any(), any())).thenReturn(result);

        // When & Then
        mockMvc.perform(multipart("/api/v1/parks/1/attractions")
                        .file(mockImage)  // 이미지 파일 전달
                        .file(request)  // request 데이터 전달 (UTF-8 인코딩)
                        .contentType(MediaType.MULTIPART_FORM_DATA))  // multipart/form-data로 요청 설정
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("롤러코스터"))
                .andExpect(jsonPath("$.data.description").value("빠른 롤러코스터"))
                .andExpect(jsonPath("$.data.imageUrls[0]").value("이미지_주소"));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldGetAllAttractionsSuccessfully() throws Exception {
        // Given
        FindAttractionResult attraction1 = FindAttractionResult.builder()
                .id(1L)
                .name("롤러코스터")
                .description("빠른 롤러코스터")
                .themeParkId(1L)
                .imageUrls(List.of("이미지_주소1"))
                .build();

        FindAttractionResult attraction2 = FindAttractionResult.builder()
                .id(2L)
                .name("바이킹")
                .description("무서운 바이킹")
                .themeParkId(1L)
                .imageUrls(List.of("이미지_주소2"))
                .build();

        List<FindAttractionResult> attractionList = List.of(attraction1, attraction2);
        when(attractionReadUseCase.getAttractionsByThemePark(1L)).thenReturn(attractionList);

        // When & Then
        mockMvc.perform(get("/api/v1/parks/1/attractions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("롤러코스터"))
                .andExpect(jsonPath("$.data[0].themeParkId").value(1L))
                .andExpect(jsonPath("$.data[1].name").value("바이킹"))
                .andExpect(jsonPath("$.data[1].themeParkId").value(1L));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldGetAttractionByIdSuccessfully() throws Exception {
        // Given
        FindAttractionResult result = FindAttractionResult.builder()
                .id(1L)
                .name("롤러코스터")
                .description("빠른 롤러코스터")
                .themeParkId(1L)
                .imageUrls(List.of("이미지_주소"))
                .build();

        when(attractionReadUseCase.getAttractionById(1L)).thenReturn(result);

        // When & Then
        mockMvc.perform(get("/api/v1/parks/1/attractions/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("롤러코스터"))
                .andExpect(jsonPath("$.data.themeParkId").value(1L))
                .andExpect(jsonPath("$.data.description").value("빠른 롤러코스터"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldUpdateAttractionSuccessfully() throws Exception {
        // Given
        AttractionRequest request = new AttractionRequest("바이킹", "무서운 바이킹", "새로운 이미지_주소");
        FindAttractionResult updatedResult = FindAttractionResult.builder()
                .id(1L)
                .name("바이킹")
                .description("무서운 바이킹")
                .themeParkId(1L)
                .imageUrls(List.of("새로운 이미지_주소"))
                .build();

        when(attractionOperationUseCase.updateAttraction(any(), any())).thenReturn(updatedResult);

        // When & Then
        mockMvc.perform(patch("/api/v1/parks/1/attractions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldDeleteAttractionSuccessfully() throws Exception {
        // Given
        doNothing().when(attractionOperationUseCase).deleteAttraction(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/parks/1/attractions/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
