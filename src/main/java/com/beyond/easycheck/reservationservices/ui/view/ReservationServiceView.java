package com.beyond.easycheck.reservationservices.ui.view;

import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceEntity;
import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationServiceView {

    private Long id;

    private Long reservationRoomId;

    private Long additionalServiceId;

    private Integer quantity;

    private Integer totalPrice;

    private ReservationServiceStatus reservationServiceStatus;

    public static ReservationServiceView of(ReservationServiceEntity reservationServiceEntity) {

        return new ReservationServiceView(

                reservationServiceEntity.getId(),
                reservationServiceEntity.getReservationRoomEntity().getId(),
                reservationServiceEntity.getAdditionalServiceEntity().getId(),
                reservationServiceEntity.getQuantity(),
                reservationServiceEntity.getTotalPrice(),
                reservationServiceEntity.getReservationServiceStatus()
        );
    }
}
