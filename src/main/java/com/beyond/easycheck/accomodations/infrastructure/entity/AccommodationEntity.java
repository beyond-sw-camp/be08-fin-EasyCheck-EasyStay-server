package com.beyond.easycheck.accomodations.infrastructure.entity;

import com.beyond.easycheck.accomodations.ui.requestbody.AccommodationUpdateRequest;
import com.beyond.easycheck.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationType accommodationType;

    public void updateAccommodation(AccommodationUpdateRequest accommodationUpdateRequest) {
        Optional.ofNullable(accommodationUpdateRequest.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(accommodationUpdateRequest.getAddress()).ifPresent(address -> this.address = address);
        Optional.ofNullable(accommodationUpdateRequest.getAccommodationType()).ifPresent(accommodationType -> this.accommodationType = accommodationType);
    }
}
