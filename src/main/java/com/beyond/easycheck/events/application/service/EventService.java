package com.beyond.easycheck.events.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.events.infrastructure.entity.EventEntity;
import com.beyond.easycheck.events.infrastructure.repository.EventRepository;
import com.beyond.easycheck.events.ui.requestbody.EventCreateRequest;
import com.beyond.easycheck.roomtypes.infrastructure.entity.RoomtypeEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.beyond.easycheck.accomodations.exception.AccommodationMessageType.ACCOMMODATION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AccommodationRepository accommodationRepository;

    @Transactional
    public void createEvent(EventCreateRequest eventCreateRequest) {

        AccommodationEntity accommodationEntity = accommodationRepository.findById(eventCreateRequest.getAccommodationEntity())
                .orElseThrow(() -> new EasyCheckException(ACCOMMODATION_NOT_FOUND));

        EventEntity event = EventEntity.builder()
                .accommodationEntity(accommodationEntity)
                .eventName(eventCreateRequest.getEventName())
                .detail(eventCreateRequest.getDetail())
                .image(eventCreateRequest.getImage())
                .startDate(eventCreateRequest.getStartDate())
                .endDate(eventCreateRequest.getEndDate())
                .build();

        eventRepository.save(event);
    }
}
