package com.beyond.easycheck.facilities.infrastructure.entity;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.beyond.easycheck.facilities.ui.requestbody.FacilityUpdateRequest;
import com.beyond.easycheck.facilities.ui.view.FacilityView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailableStatus availableStatus;

    public void updateFacility(FacilityUpdateRequest facilityUpdateRequest) {
        Optional.ofNullable(facilityUpdateRequest.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(facilityUpdateRequest.getDescription()).ifPresent(description -> this.description = description);
        Optional.ofNullable(facilityUpdateRequest.getAvailableStatus()).ifPresent(availableStatus -> this.availableStatus = availableStatus);
    }
}
