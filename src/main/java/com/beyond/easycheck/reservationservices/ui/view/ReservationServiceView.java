package com.beyond.easycheck.reservationservices.ui.view;

import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceEntity;
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

    public static ReservationServiceView of(ReservationServiceEntity reservationServiceEntity) {

        return new ReservationServiceView(

                reservationServiceEntity.getId(),
                reservationServiceEntity.getReservationRoomEntity().getId(),
                reservationServiceEntity.getAdditionalServiceEntity().getId(),
                reservationServiceEntity.getQuantity(),
                reservationServiceEntity.getTotalPrice()
        );
    }
}
