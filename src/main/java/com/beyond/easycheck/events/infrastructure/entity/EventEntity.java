package com.beyond.easycheck.events.infrastructure.entity;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Getter
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

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

}
