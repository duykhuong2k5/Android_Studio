package com.example.pandora.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.pandora.api.ApiResponse;
import com.example.pandora.mapper.ProgressMapper;
import com.example.pandora.model.JobProgress;
import com.example.pandora.model.UserProgress;
import com.example.pandora.model.response.ProgressResponse;
import com.example.pandora.model.response.UserProgressResponse;
import com.example.pandora.service.ProgressService;

@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    // GET /api/v1/progress/me?userId=1
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProgressResponse>> me(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(progressService.getByUserId(userId)));
    }
    @GetMapping("/summary")
    public ApiResponse<ProgressResponse> summary(@RequestParam Long userId) {

        UserProgress up = progressService.ensureUserProgress(userId);
        List<JobProgress> jobs = progressService.getJobs(userId);

        ProgressResponse resp = new ProgressResponse(
                ProgressMapper.toUserProgress(up),
                jobs.stream()
                    .map(ProgressMapper::toJob)
                    .toList()
        );

        return ApiResponse.ok(resp);
    }

}

