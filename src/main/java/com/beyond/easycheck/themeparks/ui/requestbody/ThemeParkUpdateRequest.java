package com.beyond.easycheck.themeparks.ui.requestbody;

import lombok.Getter;

@Getter
public class ThemeParkUpdateRequest {

    public String name;

    public String description;

    public String location;

    public String image;
}