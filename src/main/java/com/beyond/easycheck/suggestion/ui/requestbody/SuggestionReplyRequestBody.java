package com.beyond.easycheck.suggestion.ui.requestbody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
@Setter
public class SuggestionReplyRequestBody {

    @NotNull
    private Long suggestionId;

    @NotBlank
    private String replyContent;

}
