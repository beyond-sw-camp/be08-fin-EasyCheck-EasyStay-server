package com.beyond.easycheck.events.ui.controller;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.events.application.service.EventService;
import com.beyond.easycheck.events.ui.requestbody.EventCreateRequest;
import com.beyond.easycheck.events.ui.requestbody.EventUpdateRequest;
import com.beyond.easycheck.events.ui.view.EventView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.beyond.easycheck.events.exception.EventMessageType.ARGUMENT_NOT_VALID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Event", description = "이벤트 관리 API")
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이벤트 생성 API")
    public ResponseEntity<Void> createEvent(
            @RequestPart("description") EventCreateRequest eventCreateRequest,
            @RequestPart("Image") List<MultipartFile> imageFiles) {

        eventService.createEvent(eventCreateRequest, imageFiles);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "이벤트 단일 조회 API")
    public ResponseEntity<EventView> readEvent(@PathVariable Long id) {
        EventView eventView = eventService.readEvent(id);
        return ResponseEntity.ok().body(eventView);
    }

    @GetMapping("")
    @Operation(summary = "이벤트 전체 조회 API")
    public ResponseEntity<List<EventView>> readEvents() {
        List<EventView> eventViews = eventService.readEvents();
        return ResponseEntity.ok().body(eventViews);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "이벤트 수정 API")
    public ResponseEntity<Void> updateEvent(@PathVariable Long id, @RequestBody EventUpdateRequest eventUpdateRequest) {
        if (eventUpdateRequest.getEventName() == null || eventUpdateRequest.getDetail() == null
                || eventUpdateRequest.getStartDate() == null || eventUpdateRequest.getEndDate() == null) {
            throw new EasyCheckException(ARGUMENT_NOT_VALID);
        }
        eventService.updateEvent(id, eventUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/images/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이벤트 사진 수정 API")
    public ResponseEntity<Void> updateEventImage(@PathVariable Long imageId, @RequestPart MultipartFile newImageFile) {
        eventService.updateEventImage(imageId, newImageFile);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "이벤트 삭제 API")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

}
