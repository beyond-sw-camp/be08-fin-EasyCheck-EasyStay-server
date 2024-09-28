package com.beyond.easycheck.notices.ui.controller;

import com.beyond.easycheck.notices.application.service.NoticesService;
import com.beyond.easycheck.notices.ui.requestbody.NoticesCreateRequest;
import com.beyond.easycheck.notices.ui.view.NoticesView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notices", description = "공지사항 관리 API")
@RestController
@RequestMapping("/api/v1/notices-reply")
public class NoticesController {

    @Autowired
    private NoticesService noticesService;

    @Operation(summary = "공지사항을 등록하는 API")
    @PostMapping("")
    public ResponseEntity<Void> createNotices(@RequestBody @Validated NoticesCreateRequest noticesCreateRequest) {

        noticesService.createNotices(noticesCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "모든 공지사항의 리스트를 반환하는 API")
    @GetMapping("")
    public ResponseEntity<List<NoticesView>> getAllNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        List<NoticesView> notices = noticesService.getAllNotices(page, size);
        return ResponseEntity.ok(notices);
    }

    @Operation(summary = "특정 공지사항의 정보를 반환하는 API")
    @GetMapping("/{id}")
    public ResponseEntity<NoticesView> getNotices(@PathVariable("id") long id) {

        NoticesView noticesView = noticesService.getNotices(id);

        return ResponseEntity.ok(noticesView);
    }

    @Operation(summary = "공지사항을 삭제하는 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotices(@PathVariable("id") Long id) {

        noticesService.deleteNotices(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
