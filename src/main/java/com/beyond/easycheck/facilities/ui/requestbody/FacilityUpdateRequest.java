package com.beyond.easycheck.facilities.ui.requestbody;

import com.beyond.easycheck.facilities.infrastructure.entity.AvailableStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = lombok.AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
public class FacilityUpdateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Enumerated(EnumType.STRING)
    private AvailableStatus availableStatus;
}
