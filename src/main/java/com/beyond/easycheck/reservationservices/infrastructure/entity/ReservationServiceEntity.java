package com.beyond.easycheck.reservationservices.infrastructure.entity;

import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.beyond.easycheck.reservationroom.infrastructure.entity.ReservationRoomEntity;
import com.beyond.easycheck.reservationservices.ui.requestbody.ReservationServiceUpdateRequest;
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
@Table(name = "ReservationService")
public class ReservationServiceEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    @JsonManagedReference
    private ReservationRoomEntity reservationRoomEntity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "additional_service_id", nullable = false)
    @JsonManagedReference
    private AdditionalServiceEntity additionalServiceEntity;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationServiceStatus reservationServiceStatus;

    public void cancelReservationService(ReservationServiceUpdateRequest reservationServiceUpdateRequest) {
        Optional.ofNullable(reservationServiceUpdateRequest.getReservationServiceStatus()).ifPresent(reservationServiceStatus -> this.reservationServiceStatus = reservationServiceStatus);
    }
}
