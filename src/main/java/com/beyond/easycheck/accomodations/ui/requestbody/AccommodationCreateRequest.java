package com.beyond.easycheck.accomodations.ui.requestbody;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Enumerated(EnumType.STRING)
    private AccommodationType accommodationType;
}
