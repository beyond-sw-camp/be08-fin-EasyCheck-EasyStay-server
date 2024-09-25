package com.beyond.easycheck.accomodations.ui.requestbody;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class AccommodationUpdateRequest {

    private String name;

    private String address;

    private AccommodationType accommodationType;
}
