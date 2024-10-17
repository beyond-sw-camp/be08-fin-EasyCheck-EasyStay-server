package com.beyond.easycheck.accomodations.ui.requestbody;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class AccommodationUpdateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String latitude;

    @NotBlank
    private String longitude;

    @NotBlank
    private String responseTime;

    @Enumerated(EnumType.STRING)
    private AccommodationType accommodationType;
}
