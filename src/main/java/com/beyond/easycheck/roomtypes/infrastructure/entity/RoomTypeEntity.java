package com.beyond.easycheck.roomtypes.infrastructure.entity;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Entity
@Builder
@Table(name = "room_type")
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    @JsonManagedReference
    private AccommodationEntity accommodationEntity;

    @Column(nullable = false)
    private String typeName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int maxOccupancy;

}
