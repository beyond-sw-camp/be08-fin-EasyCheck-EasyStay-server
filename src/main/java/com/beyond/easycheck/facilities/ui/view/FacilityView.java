package com.beyond.easycheck.facilities.ui.view;

import com.beyond.easycheck.facilities.infrastructure.entity.AvailableStatus;
import com.beyond.easycheck.facilities.infrastructure.entity.FacilityEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FacilityView {

    private Long id;

    private String name;

    private String description;

    private String accommodationName;

    private AvailableStatus availableStatus;

    public static List<FacilityView> listOf(List<FacilityEntity> filteredfacilities) {
        return filteredfacilities.stream()
                .map(FacilityView::of)
                .toList();
    }

    public static FacilityView of(FacilityEntity facilityEntity) {

        return new FacilityView(

                facilityEntity.getId(),
                facilityEntity.getName(),
                facilityEntity.getDescription(),
                facilityEntity.getAccommodationEntity().getName(),
                facilityEntity.getAvailableStatus()
        );
    }
}
