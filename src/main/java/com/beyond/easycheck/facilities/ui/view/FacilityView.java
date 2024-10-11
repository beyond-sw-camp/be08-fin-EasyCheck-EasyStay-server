package com.beyond.easycheck.facilities.ui.view;

import com.beyond.easycheck.facilities.infrastructure.entity.AvailableStatus;
import com.beyond.easycheck.facilities.infrastructure.entity.FacilityEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FacilityView {

    private Long id;

    private String name;

    private List<String> images;

    private String description;

    private String accommodationName;

    private AvailableStatus availableStatus;

    public static List<FacilityView> listOf(List<FacilityEntity> filteredfacilities) {
        return filteredfacilities.stream()
                .map(FacilityView::of)
                .toList();
    }

    public static FacilityView of(FacilityEntity facilityEntity) {

        List<String> imageUrls = facilityEntity.getImages().stream()
                .map(FacilityEntity.ImageEntity::getUrl)
                .collect(Collectors.toList());

        return new FacilityView(
                facilityEntity.getId(),
                facilityEntity.getName(),
                imageUrls,
                facilityEntity.getDescription(),
                facilityEntity.getAccommodationEntity().getName(),
                facilityEntity.getAvailableStatus()
        );
    }
}
