package com.beyond.easycheck.suggestion.ui.requestbody;

import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.AgreementType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
public class SuggestionCreateRequest {

    @NotNull
    private Long accommodationId;

    @NotBlank
    private String type;

    @NotBlank
    private String subject;

    @NotBlank
    private String email;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String attachmentPath;

    @Enumerated(EnumType.STRING)
    private AgreementType agreementType;

}
