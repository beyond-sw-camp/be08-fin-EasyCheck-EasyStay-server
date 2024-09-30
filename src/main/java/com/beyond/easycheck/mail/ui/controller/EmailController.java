package com.beyond.easycheck.mail.ui.controller;


import com.beyond.easycheck.mail.application.service.MailService;
import com.beyond.easycheck.mail.ui.requestbody.VerificationCodeRequest;
import com.beyond.easycheck.mail.ui.requestbody.VerificationTargetEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EmailController {

    private final MailService mailService;

    @PatchMapping("/verification-code")
    public ResponseEntity<Void> requestVerificationCode(@RequestBody @Validated VerificationTargetEmailRequest request) {

        log.info("[EmailController - requestVerificationCode] request = {}", request);
        mailService.sendVerificationCode(request.targetEmail());

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PatchMapping("/verify-code")
    public ResponseEntity<Void> verifyCode(@RequestBody @Validated VerificationCodeRequest request) {

        log.info("[EmailController - verifyCode] request = {}", request);
        mailService.verifyEmail(request.code());

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
