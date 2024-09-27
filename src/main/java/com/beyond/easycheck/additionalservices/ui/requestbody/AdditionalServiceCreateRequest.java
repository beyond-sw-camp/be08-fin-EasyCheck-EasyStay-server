package com.beyond.easycheck.additionalservices.ui.requestbody;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = lombok.AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
public class AdditionalServiceCreateRequest {

    @NotNull
    private Long accommodationId;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @Min(value = 0, message = "price must be greater than or equal to 0")
    private Integer price;
}
