package com.beyond.easycheck.reservationrooms.infrastructure.entity;

import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.beyond.easycheck.payments.infrastructure.entity.CompletionStatus;
import com.beyond.easycheck.payments.infrastructure.entity.PaymentEntity;
import com.beyond.easycheck.reservationrooms.ui.requestbody.ReservationRoomUpdateRequest;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static jakarta.persistence.FetchType.LAZY;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private UserEntity userEntity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonManagedReference
    private RoomEntity roomEntity;

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
        Optional.ofNullable(reservationRoomUpdateRequest.getReservationStatus()).ifPresent(reservationStatus -> this.reservationStatus = reservationStatus);
    }

    public void processPayment(PaymentEntity paymentEntity) {
        if (this.totalPrice.equals(paymentEntity.getAmount()) && paymentEntity.getCompletionStatus() == CompletionStatus.COMPLETE) {
            this.paymentStatus = PaymentStatus.PAID;
        } else {
            throw new IllegalArgumentException("결제 금액이 맞지 않거나 결제가 완료되지 않았습니다.");
        }
    }

    public void updateReservationRoomAndProcessPayment(ReservationRoomUpdateRequest reservationRoomUpdateRequest, PaymentEntity paymentEntity) {
        this.updateReservationRoom(reservationRoomUpdateRequest);
        this.processPayment(paymentEntity);
    }

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
