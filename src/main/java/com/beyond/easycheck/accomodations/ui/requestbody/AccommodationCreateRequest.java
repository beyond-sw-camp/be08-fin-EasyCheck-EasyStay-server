package com.beyond.easycheck.accomodations.ui.requestbody;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = lombok.AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
public class AccommodationCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private AccommodationType accommodationType;
}
