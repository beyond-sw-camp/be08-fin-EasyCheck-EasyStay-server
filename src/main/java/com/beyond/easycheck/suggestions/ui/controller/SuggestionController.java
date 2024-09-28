package com.beyond.easycheck.suggestions.ui.controller;

import com.beyond.easycheck.suggestions.application.service.SuggestionService;
import com.beyond.easycheck.suggestions.ui.requestbody.SuggestionCreateRequest;
import com.beyond.easycheck.suggestions.ui.view.SuggestionView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Suggestions", description = "건의사항 관리 API")
@RestController
@RequestMapping("/api/v1/suggestions")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @PostMapping("")
    public ResponseEntity<Void> createSuggestion(@RequestBody @Validated SuggestionCreateRequest suggestionCreateRequest) {

        suggestionService.createSuggestion(suggestionCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "모든 건의사항 리스트를 반환하는 API")
    @GetMapping("")
    public ResponseEntity<List<SuggestionView>> getAllSuggestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<SuggestionView> suggestion = suggestionService.getAllsuggestions(page, size);
        return ResponseEntity.ok(suggestion);

    }

    @Operation(summary = "특정 건의사항 정보를 반환하는 API")
    @GetMapping("/{id}")
    public ResponseEntity<SuggestionView> getSuggestionById(@PathVariable Long id) {

        SuggestionView suggestionView = suggestionService.getSuggestionById(id);

        return ResponseEntity.ok(suggestionView);
    }


}
