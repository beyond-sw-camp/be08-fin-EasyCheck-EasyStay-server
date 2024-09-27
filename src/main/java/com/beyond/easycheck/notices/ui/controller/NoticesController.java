package com.beyond.easycheck.notices.ui.controller;

import com.beyond.easycheck.notices.application.service.NoticesService;
import com.beyond.easycheck.notices.ui.requestbody.NoticesCreateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notices", description = "공지사항 관리 API")
@RestController
@RequestMapping("/api/v1/notices-reply")
public class NoticesController {

    @Autowired
    private NoticesService noticesService;

    @PostMapping("")
    public ResponseEntity<Void> createNotices(@RequestBody @Validated NoticesCreateRequest noticesCreateRequest) {

        noticesService.createNotices(noticesCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
