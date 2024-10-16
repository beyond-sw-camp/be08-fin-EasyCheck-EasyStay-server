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

    private String name;

    private List<String> thumbnailUrls;

    private List<String> landscapeUrls;

    private String address;

    private String directionsUrl;

    private String latitude;

    private String longitude;

    private AccommodationType accommodationType;

    public static List<AccommodationView> listOf(List<AccommodationEntity> filteredAccommodation) {
        return filteredAccommodation.stream()
                .map(AccommodationView::of)
                .toList();
    }

    public static AccommodationView of(AccommodationEntity accommodationEntity) {

        return new AccommodationView(

                accommodationEntity.getId(),
                accommodationEntity.getName(),
                accommodationEntity.getThumbnailUrls(),
                accommodationEntity.getLandscapeUrls(),
                accommodationEntity.getAddress(),
                accommodationEntity.getDirectionsUrl(),
                accommodationEntity.getLatitude(),
                accommodationEntity.getLongitude(),
                accommodationEntity.getAccommodationType()
        );
    }
}
