package com.beyond.easycheck.events.infrastructure.entity;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.beyond.easycheck.events.ui.requestbody.EventUpdateRequest;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@Builder
@Table(name = "event")
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    @JsonManagedReference
    private AccommodationEntity accommodationEntity;

    @Column(nullable = false)
    private String eventName;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventEntity.ImageEntity> images = new ArrayList<>();

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    public void update(EventUpdateRequest eventUpdateRequest, AccommodationEntity newAccommodationEntity) {
        this.accommodationEntity = newAccommodationEntity;
        this.eventName = eventUpdateRequest.getEventName();
        this.detail = eventUpdateRequest.getDetail();
        this.startDate = eventUpdateRequest.getStartDate();
        this.endDate = eventUpdateRequest.getEndDate();
    }

    public void addImage(EventEntity.ImageEntity imageEntity) {
        this.images.add(imageEntity);
        imageEntity.setEvent(this);
    }

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "event_image")
    public static class ImageEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "image_id")
        private Long id;

        @Column(nullable = false)
        private String url;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "event", nullable = false)
        private EventEntity event;

        public static EventEntity.ImageEntity createImage(String url, EventEntity event) {
            return new EventEntity.ImageEntity(null, url, event);
        }

    }

}
