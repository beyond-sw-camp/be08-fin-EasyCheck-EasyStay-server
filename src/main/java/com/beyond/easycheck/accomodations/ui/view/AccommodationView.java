package com.beyond.easycheck.accomodations.ui.view;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AccommodationView {

    private Long id;

    private Long userId;

    private String name;

    private String address;

    private AccommodationType accommodationType;

    public static List<AccommodationView> listOf(List<AccommodationEntity> filteredAccommodation) {
        return filteredAccommodation.stream()
                .map(AccommodationView::of)
                .toList();
    }

    public static AccommodationView of(AccommodationEntity accommodationEntity) {

        return new AccommodationView(

                accommodationEntity.getId(),
                accommodationEntity.getUserEntity().getId(),
                accommodationEntity.getName(),
                accommodationEntity.getAddress(),
                accommodationEntity.getAccommodationType()
        );
    }
}
