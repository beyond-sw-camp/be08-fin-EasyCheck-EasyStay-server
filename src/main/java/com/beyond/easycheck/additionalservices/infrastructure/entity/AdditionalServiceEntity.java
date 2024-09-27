package com.beyond.easycheck.additionalservices.infrastructure.entity;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.additionalservices.ui.requestbody.AdditionalServiceUpdateRequest;
import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "AdditionalSerivce")
public class AdditionalServiceEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    @JsonManagedReference
    private AccommodationEntity accommodationEntity;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer price;

    public void updateAdditionalService(AdditionalServiceUpdateRequest additionalServiceUpdateRequest) {
        Optional.ofNullable(additionalServiceUpdateRequest.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(additionalServiceUpdateRequest.getDescription()).ifPresent(description -> this.description = description);
        Optional.ofNullable(additionalServiceUpdateRequest.getPrice()).ifPresent(price -> this.price = price);
    }
}
