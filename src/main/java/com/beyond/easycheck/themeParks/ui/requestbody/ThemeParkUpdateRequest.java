package com.beyond.easycheck.themeParks.ui.requestbody;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ThemeParkUpdateRequest {

    private String name;

    private String description;

    private String location;
}
