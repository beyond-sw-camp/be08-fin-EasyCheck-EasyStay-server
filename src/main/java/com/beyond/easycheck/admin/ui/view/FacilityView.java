package com.beyond.easycheck.admin.ui.view;

import com.beyond.easycheck.admin.application.service.AdminReadUseCase;
import com.beyond.easycheck.facilities.infrastructure.entity.AvailableStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

import static com.beyond.easycheck.admin.application.service.AdminReadUseCase.*;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacilityView {

    private Long id;

    private String name;

    private String description;

    private List<String> images;

    private String accommodationName;

    private AvailableStatus availableStatus;

    public FacilityView(FindFacilitiesResult result) {
        this.id = result.id();
        this.name = result.name();
        this.images = result.images();
        this.description = result.description();
        this.availableStatus = result.availableStatus();
        this.accommodationName = result.accommodationName();
    }

}
