package com.beyond.easycheck.additionalservices.ui.controller;

import com.beyond.easycheck.accomodations.exception.AccommodationMessageType;
import com.beyond.easycheck.additionalservices.application.service.AdditionalServiceService;
import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceCreateRequest;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceUpdateRequest;
import com.beyond.easycheck.additionalservices.ui.view.AdditionalServiceView;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.user.application.mock.WithEasyCheckMockUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithEasyCheckMockUser(role = "ADMIN")
class AdditionalServiceControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AdditionalServiceService additionalServiceService;

    @Test
    @DisplayName("부가 서비스 등록 - 성공")
    void createAdditionalService_Success() throws Exception {
        AdditionalServiceCreateRequest request = new AdditionalServiceCreateRequest(1L, "WiFi", "고속 인터넷", 10000);
        AdditionalServiceEntity createdEntity = AdditionalServiceEntity.builder()
                .id(1L)
                .name("WiFi")
                .description("고속 인터넷")
                .price(10000)
                .build();

        when(additionalServiceService.createAdditionalService(any(AdditionalServiceCreateRequest.class)))
                .thenReturn(Optional.of(createdEntity));

        mvc.perform(post("/api/v1/additional-services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(additionalServiceService).createAdditionalService(any(AdditionalServiceCreateRequest.class));
    }
    @Test
    @DisplayName("모든 부가 서비스 조회 - 성공")
    void getAllAdditionalService_Success() throws Exception {
        List<AdditionalServiceView> services = Arrays.asList(
                new AdditionalServiceView(1L, "호텔A", "WiFi", "고속 인터넷", 10000),
                new AdditionalServiceView(2L, "호텔B", "조식", "풍성한 아침 식사", 20000)
        );

        when(additionalServiceService.getAllAdditionalService(anyInt(), anyInt())).thenReturn(services);

        mvc.perform(get("/api/v1/additional-services")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(additionalServiceService).getAllAdditionalService(0, 10);
    }

    @Test
    @DisplayName("특정 부가 서비스 조회 - 성공")
    void getAdditionalServiceById_Success() throws Exception {
        AdditionalServiceView service = new AdditionalServiceView(1L, "호텔A", "WiFi", "고속 인터넷", 10000);

        when(additionalServiceService.getAdditionalServiceById(1L)).thenReturn(service);

        mvc.perform(get("/api/v1/additional-services/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accommodationName").value("호텔A"))
                .andExpect(jsonPath("$.name").value("WiFi"));

        verify(additionalServiceService).getAdditionalServiceById(1L);
    }

    @Test
    @DisplayName("부가 서비스 수정 - 성공")
    void updateAdditionalService_Success() throws Exception {
        AdditionalServiceUpdateRequest request = new AdditionalServiceUpdateRequest("Updated WiFi", "업데이트된 고속 인터넷", 15000);
        AdditionalServiceView updatedService = new AdditionalServiceView(1L, "호텔A", "Updated WiFi", "업데이트된 고속 인터넷", 15000);

        doNothing().when(additionalServiceService).updateAdditionalService(eq(1L), any(AdditionalServiceUpdateRequest.class));
        when(additionalServiceService.getAdditionalServiceById(1L)).thenReturn(updatedService);

        mvc.perform(put("/api/v1/additional-services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated WiFi"))
                .andExpect(jsonPath("$.price").value(15000));

        verify(additionalServiceService).updateAdditionalService(eq(1L), any(AdditionalServiceUpdateRequest.class));
        verify(additionalServiceService).getAdditionalServiceById(1L);
    }

    @Test
    @DisplayName("부가 서비스 삭제 - 성공")
    void deleteAdditionalService_Success() throws Exception {
        doNothing().when(additionalServiceService).deleteAdditionalService(1L);

        mvc.perform(delete("/api/v1/additional-services/1"))
                .andExpect(status().isNoContent());

        verify(additionalServiceService).deleteAdditionalService(1L);
    }

    @Test
    @DisplayName("부가 서비스 등록 - 실패 (유효하지 않은 입력)")
    void createAdditionalService_InvalidInput() throws Exception {
        AdditionalServiceCreateRequest request = new AdditionalServiceCreateRequest(1L, "", "", -1000);

        mvc.perform(post("/api/v1/additional-services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(additionalServiceService, never()).createAdditionalService(any(AdditionalServiceCreateRequest.class));
    }

    @Test
    @DisplayName("부가 서비스 등록 - 실패 (존재하지 않는 숙소)")
    void createAdditionalService_AccommodationNotFound() throws Exception {
        AdditionalServiceCreateRequest request = new AdditionalServiceCreateRequest(999L, "WiFi", "고속 인터넷", 10000);

        when(additionalServiceService.createAdditionalService(any(AdditionalServiceCreateRequest.class)))
                .thenThrow(new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND));

        mvc.perform(post("/api/v1/additional-services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorType").value(AccommodationMessageType.ACCOMMODATION_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errors[0].errorMessage").value(AccommodationMessageType.ACCOMMODATION_NOT_FOUND.getMessage()));

        verify(additionalServiceService).createAdditionalService(any(AdditionalServiceCreateRequest.class));
    }

    @Test
    @DisplayName("부가 서비스 등록 - 실패 (서버 내부 오류)")
    void createAdditionalService_InternalServerError() throws Exception {
        AdditionalServiceCreateRequest request = new AdditionalServiceCreateRequest(1L, "WiFi", "고속 인터넷", 10000);

        when(additionalServiceService.createAdditionalService(any(AdditionalServiceCreateRequest.class)))
                .thenThrow(new RuntimeException("내부 서버 오류"));

        mvc.perform(post("/api/v1/additional-services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(additionalServiceService).createAdditionalService(any(AdditionalServiceCreateRequest.class));
    }
}