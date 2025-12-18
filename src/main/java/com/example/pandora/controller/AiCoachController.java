package com.example.pandora.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.pandora.api.ApiResponse;
import com.example.pandora.model.response.AiAttemptResponse;
import com.example.pandora.service.AiCoachService;

@RestController
@RequestMapping("/api/v1/ai-coach")
public class AiCoachController {

    private final AiCoachService aiCoachService;

    public AiCoachController(AiCoachService aiCoachService) {
        this.aiCoachService = aiCoachService;
    }

    // POST /api/v1/ai-coach/attempt (multipart/form-data)
    // fields: userId, targetText, audioFile
    @PostMapping("/attempt")
    public ResponseEntity<ApiResponse<AiAttemptResponse>> attempt(
            @RequestParam("userId") Long userId,
            @RequestParam("targetText") String targetText,
            @RequestParam("audioFile") MultipartFile audioFile
    ) {
        AiAttemptResponse res = aiCoachService.createAttempt(userId, targetText, audioFile);
        return ResponseEntity.ok(ApiResponse.ok("Attempt created", res));
    }
}

