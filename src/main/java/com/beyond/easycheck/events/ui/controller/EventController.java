package com.beyond.easycheck.events.ui.controller;

import com.beyond.easycheck.events.application.service.EventService;
import com.beyond.easycheck.events.ui.requestbody.EventCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Event", description = "이벤트 관리 API")
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    @PostMapping("")
    @Operation(summary = "객실 유형 생성 API")
    public ResponseEntity<Void> createEvent(@RequestBody EventCreateRequest eventCreateRequest) {
        eventService.createEvent(eventCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
