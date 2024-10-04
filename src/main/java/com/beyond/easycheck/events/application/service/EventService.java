package com.beyond.easycheck.events.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.repository.AccommodationRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.events.infrastructure.entity.EventEntity;
import com.beyond.easycheck.events.infrastructure.repository.EventRepository;
import com.beyond.easycheck.events.ui.requestbody.EventCreateRequest;
import com.beyond.easycheck.events.ui.requestbody.EventUpdateRequest;
import com.beyond.easycheck.events.ui.view.EventView;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.beyond.easycheck.accomodations.exception.AccommodationMessageType.ACCOMMODATION_NOT_FOUND;
import static com.beyond.easycheck.events.exception.EventMessageType.EVENT_NOT_FOUND;

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

    public EventView readEvent(Long id) {

        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(EVENT_NOT_FOUND));

        EventView eventView = EventView.builder()
                .id(event.getId())
                .accommodationName(event.getAccommodationEntity().getName())
                .eventName(event.getEventName())
                .image(event.getImage())
                .detail(event.getDetail())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .build();

        return eventView;
    }

    @Transactional
    public List<EventView> readEvents() {

        List<EventEntity> eventEntities = eventRepository.findAll();

        if (eventEntities.isEmpty()) {
            throw new EasyCheckException(EVENT_NOT_FOUND);
        }
        List<EventView> eventViews = eventEntities.stream()
                .map(eventEntity -> new EventView(
                        eventEntity.getId(),
                        eventEntity.getAccommodationEntity().getName(),
                        eventEntity.getEventName(),
                        eventEntity.getDetail(),
                        eventEntity.getImage(),
                        eventEntity.getStartDate(),
                        eventEntity.getEndDate()
                )).collect(Collectors.toList());

        return eventViews;
    }

    @Transactional
    public void updateEvent(Long id, EventUpdateRequest eventUpdateRequest) {

        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(EVENT_NOT_FOUND));

        AccommodationEntity accommodationEntity = accommodationRepository.findById(eventUpdateRequest.getAccommodationId())
                .orElseThrow(() -> new EasyCheckException(ACCOMMODATION_NOT_FOUND));

        event.update(eventUpdateRequest, accommodationEntity);
    }

    @Transactional
    public void deleteEvent(Long id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new EasyCheckException(EVENT_NOT_FOUND));

        eventRepository.delete(event);
    }
}
