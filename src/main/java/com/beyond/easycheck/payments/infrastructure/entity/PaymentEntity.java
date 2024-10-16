package com.beyond.easycheck.payments.infrastructure.entity;

import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.beyond.easycheck.payments.ui.requestbody.PaymentUpdateRequest;
import com.beyond.easycheck.reservationrooms.infrastructure.entity.ReservationRoomEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

import static jakarta.persistence.FetchType.LAZY;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
@Table(name = "Payment")
public class PaymentEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    @JsonManagedReference
    private ReservationRoomEntity reservationRoomEntity;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    private LocalDateTime cancelDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompletionStatus completionStatus;

    public void updatePayment(PaymentUpdateRequest paymentUpdateRequest) {
        Optional.ofNullable(paymentUpdateRequest.getCompletionStatus()).ifPresent(completionStatus -> this.completionStatus = completionStatus);
    }
}
