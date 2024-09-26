package com.beyond.easycheck.themeparks.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseView<T> {
    private final T data;

    public ApiResponseView(T data) {
        this.data = data;
    }
}