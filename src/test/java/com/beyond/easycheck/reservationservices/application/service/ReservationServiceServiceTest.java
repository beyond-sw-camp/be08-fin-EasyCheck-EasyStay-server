package com.beyond.easycheck.reservationservices.application.service;

import com.beyond.easycheck.additionalservices.infrastructure.repository.AdditionalServiceRepository;
import com.beyond.easycheck.reservationrooms.infrastructure.repository.ReservationRoomRepository;
import com.beyond.easycheck.reservationservices.infrastructure.repository.ReservationServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceServiceTest {

    @Mock
    ReservationServiceRepository reservationServiceRepository;

    @Mock
    ReservationRoomRepository reservationRoomRepository;

    @Mock
    AdditionalServiceRepository additionalServiceRepository;

    @InjectMocks
    ReservationServiceService reservationServiceService;

    @Test
    void createReservationRoom() {
    }

    @Test
    void getAllReservationServices() {
    }

    @Test
    void getReservationServiceById() {
    }

    @Test
    void cancelReservationService() {
    }
}