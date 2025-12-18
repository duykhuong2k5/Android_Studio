package com.example.pandora.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.pandora.api.ApiResponse;
import com.example.pandora.dto.VocabularyWordResponse;
import com.example.pandora.enums.VocabularyTopic;
import com.example.pandora.service.VocabularyService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vocab")
public class VocabularyController {

    private final VocabularyService vocabularyService;

    public VocabularyController(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    // GET /api/v1/vocab?topic=FOOD&level=1
    @GetMapping
    public ResponseEntity<ApiResponse<List<VocabularyWordResponse>>> getVocab(
            @RequestParam(value = "topic", required = false) VocabularyTopic topic,
            @RequestParam(value = "level", required = false) Integer level
    ) {
        return ResponseEntity.ok(ApiResponse.ok(vocabularyService.getVocab(topic, level)));
    }
}

