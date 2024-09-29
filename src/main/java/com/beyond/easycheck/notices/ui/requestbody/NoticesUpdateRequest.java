package com.beyond.easycheck.notices.ui.requestbody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class NoticesUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
