package com.beyond.easycheck.ui.controller;

import com.beyond.easycheck.application.service.SuggestionService;
import com.beyond.easycheck.ui.requestbody.SuggestionCreateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Suggestions", description = "건의사항 관리 API")
@RestController
@RequestMapping("/api/v1/suggestions")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @PostMapping("")
    public ResponseEntity<Void> createSuggestion(@RequestBody SuggestionCreateRequest suggestionCreateRequest) {

        suggestionService.createSuggestion(suggestionCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
