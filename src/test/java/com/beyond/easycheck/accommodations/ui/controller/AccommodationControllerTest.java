//package com.beyond.easycheck.accommodations.ui.controller;
//
//import com.beyond.easycheck.accomodations.application.service.AccommodationService;
//import com.beyond.easycheck.accomodations.exception.AccommodationMessageType;
//import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
//import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationCreateRequest;
//import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationUpdateRequest;
//import com.beyond.easycheck.accomodations.ui.view.AccommodationView;
//import com.beyond.easycheck.common.exception.EasyCheckException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class AccommodationControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private AccommodationService accommodationService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void testCreateAccommodation() throws Exception {
//
//        // given
//        AccommodationCreateRequest createRequest = new AccommodationCreateRequest("Test Resort", "Test Address", AccommodationType.RESORT);
//
//        // when & then
//        mockMvc.perform(post("/api/v1/accommodations")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(createRequest)))
//                .andExpect(status().isCreated());
//
//        verify(accommodationService).createAccommodation(any(AccommodationCreateRequest.class));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void testCreateAccommodation_WithInvalidData() throws Exception {
//
//        // given
//        AccommodationCreateRequest createRequest = new AccommodationCreateRequest("", "", null);
//
//        // when & then
//        mockMvc.perform(post("/api/v1/accommodations")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(createRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    public void testGetAllAccommodations() throws Exception {
//
//        // given
//        AccommodationView accommodation1 = new AccommodationView(1L, "Resort 1", "Address 1", AccommodationType.RESORT);
//        AccommodationView accommodation2 = new AccommodationView(2L, "Resort 2", "Address 2", AccommodationType.HOTEL);
//        List<AccommodationView> accommodations = Arrays.asList(accommodation1, accommodation2);
//
//        when(accommodationService.getAllAccommodations(0, 10)).thenReturn(accommodations);
//
//        // when & then
//        mockMvc.perform(get("/api/v1/accommodations")
//                        .param("page", "0")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(2))
//                .andExpect(jsonPath("$[0].name").value("Resort 1"))
//                .andExpect(jsonPath("$[1].name").value("Resort 2"));
//
//        verify(accommodationService).getAllAccommodations(0, 10);
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    public void testGetAccommodationById() throws Exception {
//
//        // given
//        AccommodationView accommodationView = new AccommodationView(1L, "Test Resort", "Test Address", AccommodationType.RESORT);
//
//        when(accommodationService.getAccommodationById(1L)).thenReturn(accommodationView);
//
//        // when & then
//        mockMvc.perform(get("/api/v1/accommodations/{id}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Test Resort"));
//
//        verify(accommodationService).getAccommodationById(1L);
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    public void testGetAccommodationById_NotFound() throws Exception {
//
//        // given
//        when(accommodationService.getAccommodationById(1L)).thenThrow(new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND));
//
//        // when & then
//        mockMvc.perform(get("/api/v1/accommodations/{id}", 1L))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.errors[0].errorMessage").value("Accommodation not found"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void testUpdateAccommodation() throws Exception {
//
//        // given
//        AccommodationUpdateRequest updateRequest = new AccommodationUpdateRequest("Updated Resort", "Updated Address", AccommodationType.HOTEL);
//
//        // when & then
//        mockMvc.perform(put("/api/v1/accommodations/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isNoContent());
//
//        verify(accommodationService).updateAccommodation(any(Long.class), any(AccommodationUpdateRequest.class));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void testUpdateAccommodation_WithInvalidData() throws Exception {
//
//        // given
//        AccommodationUpdateRequest updateRequest = new AccommodationUpdateRequest("", "", null);
//
//        // when & then
//        mockMvc.perform(put("/api/v1/accommodations/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void testDeleteAccommodation() throws Exception {
//
//        // when & then
//        mockMvc.perform(delete("/api/v1/accommodations/{id}", 1L))
//                .andExpect(status().isNoContent());
//
//        verify(accommodationService).deleteAccommodation(1L);
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void testDeleteAccommodation_NotFound() throws Exception {
//
//        // given
//        doThrow(new EasyCheckException(AccommodationMessageType.ACCOMMODATION_NOT_FOUND))
//                .when(accommodationService).deleteAccommodation(1L);
//
//        // when & then
//        mockMvc.perform(delete("/api/v1/accommodations/{id}", 1L))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.errors[0].errorMessage").value("Accommodation not found"));
//    }
//}