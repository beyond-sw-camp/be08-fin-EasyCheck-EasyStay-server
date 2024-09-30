package com.beyond.easycheck.payments.ui.requestbody;

import com.beyond.easycheck.payments.infrastructure.entity.CompletionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class PaymentUpdateRequest {

    @Enumerated(EnumType.STRING)
    private CompletionStatus completionStatus;
}
