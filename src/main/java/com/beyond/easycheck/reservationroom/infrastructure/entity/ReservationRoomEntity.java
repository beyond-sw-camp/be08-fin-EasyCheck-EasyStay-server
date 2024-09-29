package com.beyond.easycheck.reservationroom.infrastructure.entity;

import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.beyond.easycheck.reservationroom.ui.requestbody.ReservationRoomUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
@Table(name = "ReservationRoom")
public class ReservationRoomEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    @Column(nullable = false)
    private LocalDate checkinDate;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public void updateReservationRoom(ReservationRoomUpdateRequest reservationRoomUpdateRequest) {
        Optional.ofNullable(reservationRoomUpdateRequest.getCheckinDate()).ifPresent(checkinDate -> this.checkinDate = checkinDate);
        Optional.ofNullable(reservationRoomUpdateRequest.getCheckoutDate()).ifPresent(checkoutDate -> this.checkoutDate = checkoutDate);
        Optional.ofNullable(reservationRoomUpdateRequest.getReservationStatus()).ifPresent(reservationStatus -> this.reservationStatus = reservationStatus);
        Optional.ofNullable(reservationRoomUpdateRequest.getTotalPrice()).ifPresent(totalPrice -> this.totalPrice = totalPrice);
        Optional.ofNullable(reservationRoomUpdateRequest.getPaymentStatus()).ifPresent(paymentStatus -> this.paymentStatus = paymentStatus);
    }
}
