package com.beyond.easycheck.themeParks.ui.requestbody;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ThemeParkCreateRequest {

    @NotBlank(message = "테마파크 이름은 필수입니다.")
    public String name;

    @NotBlank(message = "테마파크 설명은 필수입니다.")
    public String description;

    @NotBlank(message = "테마파크 위치는 필수입니다.")
    public String location;

    public String image;
}
