package com.beyond.easycheck.notices.ui.requestbody;

import com.beyond.easycheck.notices.infrastructure.persistence.entity.AgreementType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
public class NoticesCreateRequest {

    @NotBlank
    private String type;

    @NotBlank
    private String subject;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String attachmentPath;

    @Enumerated(EnumType.STRING)
    private AgreementType agreementType;

}
