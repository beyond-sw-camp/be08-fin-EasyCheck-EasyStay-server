package com.beyond.easycheck.common.ui.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class CommonController {

    @GetMapping("/health-check")
    @Operation(
            summary = "통신 상태 확인",
            description = "EasyCheck 에플리케이션 통신 상태 확인용"
    )
    public String healthCheck() {
        log.info("[health-check] 요청 수신");
        return "OK";
    }

}
