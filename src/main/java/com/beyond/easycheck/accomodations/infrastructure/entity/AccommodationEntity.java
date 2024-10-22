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

    @Column(nullable = false)
    private String responseTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationType accommodationType;

    public AccommodationEntity(Long id, String name, String address, AccommodationType accommodationType) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.accommodationType = accommodationType;
    }

    public AccommodationEntity(Long id, String name, List<String> thumbnailUrls, List<String> landscapeUrls, String directionsUrl, String address, String latitude, String longitude, String responseTime, AccommodationType accommodationType) {
        this.id = id;
        this.name = name;
        this.thumbnailUrls = thumbnailUrls;
        this.landscapeUrls = landscapeUrls;
        this.directionsUrl = directionsUrl;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.responseTime = responseTime;
        this.accommodationType = accommodationType;
    }

    public void updateAccommodation(AccommodationUpdateRequest accommodationUpdateRequest) {
        Optional.ofNullable(accommodationUpdateRequest.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(accommodationUpdateRequest.getAddress()).ifPresent(address -> this.address = address);
        Optional.ofNullable(accommodationUpdateRequest.getLatitude()).ifPresent(latitude -> this.latitude = latitude);
        Optional.ofNullable(accommodationUpdateRequest.getLongitude()).ifPresent(longitude -> this.longitude = longitude);
        Optional.ofNullable(accommodationUpdateRequest.getResponseTime()).ifPresent(responseTime -> this.responseTime = responseTime);
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
