package com.beyond.easycheck.admin.ui.view;


import com.beyond.easycheck.admin.application.service.AdminReadUseCase;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import static com.beyond.easycheck.admin.application.service.AdminReadUseCase.*;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalServiceView {
    private Long id;
    private String name;
    private String description;
    private Integer price;

    public AdditionalServiceView(FindAdditionalServiceResult result) {
        this.id = result.id();
        this.name = result.name();
        this.description = result.description();
        this.price = result.price();
    }
}
