package com.beyond.easycheck.themeparks.ui.requestbody;

import lombok.Getter;

@Getter
public class ThemeParkUpdateRequest {

    private String name;

    private String description;

    private String location;

    private String image;
}
