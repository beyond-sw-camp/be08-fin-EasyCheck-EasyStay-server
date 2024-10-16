package com.beyond.easycheck.accomodations.infrastructure.entity;

import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationUpdateRequest;
import com.beyond.easycheck.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
@Table(name = "Accommodation")
public class AccommodationEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @Column(name = "thumbnail_urls", nullable = false)
    private List<String> thumbnailUrls;

    @ElementCollection
    @Column(name = "landscape_urls", nullable = false)
    private List<String> landscapeUrls;

    @Column(nullable = false)
    private String directionsUrl;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String latitude;

    @Column(nullable = false)
    private String longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationType accommodationType;

    public void updateAccommodation(AccommodationUpdateRequest accommodationUpdateRequest) {
        Optional.ofNullable(accommodationUpdateRequest.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(accommodationUpdateRequest.getAddress()).ifPresent(address -> this.address = address);
        Optional.ofNullable(accommodationUpdateRequest.getAccommodationType()).ifPresent(accommodationType -> this.accommodationType = accommodationType);
    }

    public void setThumbnailUrls(List<String> thumbnailUrls) {
        this.thumbnailUrls = thumbnailUrls;
    }

    public void setLandscapeUrls(List<String> landscapeUrls) {
        this.landscapeUrls = landscapeUrls;
    }

    public void setDirectionsUrl(String directionsUrl) {
        this.directionsUrl = directionsUrl;
    }
}
