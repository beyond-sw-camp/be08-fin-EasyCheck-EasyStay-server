package com.beyond.easycheck.facilities.infrastructure.entity;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.FetchType.LAZY;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
public class FacilityEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private AccommodationEntity accommodationEntity;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "facilityEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageEntity> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailableStatus availableStatus;

    public void updateFacility(FacilityUpdateRequest facilityUpdateRequest) {
        Optional.ofNullable(facilityUpdateRequest.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(facilityUpdateRequest.getDescription()).ifPresent(description -> this.description = description);
        Optional.ofNullable(facilityUpdateRequest.getAvailableStatus()).ifPresent(availableStatus -> this.availableStatus = availableStatus);
    }

    public void addImage(ImageEntity imageEntity) {
        this.images.add(imageEntity);
        imageEntity.setFacilityEntity(this);
    }

    public void setImages(List<ImageEntity> images) {

        this.images = images;
    }

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "facility_image")
    public static class ImageEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "image_id")
        private Long id;

        @Column(nullable = false)
        private String url;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "facility_id", nullable = false)
        private FacilityEntity facilityEntity;

        public static ImageEntity createImage(String url, FacilityEntity facilityEntity) {
            return new ImageEntity(null, url, facilityEntity);
        }
    }
}
