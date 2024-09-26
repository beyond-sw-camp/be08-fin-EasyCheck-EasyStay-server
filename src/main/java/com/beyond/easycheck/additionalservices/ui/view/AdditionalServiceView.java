package com.beyond.easycheck.additionalservices.ui.view;

import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AdditionalServiceView {

    private Long id;

    private String accommodationName;

    private String name;

    private String description;

    private Integer price;

    public static List<AdditionalServiceView> listof(List<AdditionalServiceEntity> filteredAdditionalServices) {

        return filteredAdditionalServices.stream()
                .map(AdditionalServiceView::of)
                .toList();
    }

    public static AdditionalServiceView of(AdditionalServiceEntity additionalServiceEntity) {

        return new AdditionalServiceView(

                additionalServiceEntity.getId(),
                additionalServiceEntity.getAccommodationEntity().getName(),
                additionalServiceEntity.getName(),
                additionalServiceEntity.getDescription(),
                additionalServiceEntity.getPrice()
        );
    }
}
