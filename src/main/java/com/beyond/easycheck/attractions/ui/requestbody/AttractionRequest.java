package com.beyond.easycheck.attractions.ui.requestbody;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttractionRequest {

    public String name;

    public String image;

    public String introduction;
    public String information;
    public String standardUse;
}