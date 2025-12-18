package com.example.pandora.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.pandora.api.ApiResponse;
import com.example.pandora.model.DaySession;
import com.example.pandora.model.request.FinishDayRequest;
import com.example.pandora.model.request.StartDayRequest;
import com.example.pandora.service.DayGameService;

@RestController
@RequestMapping("/api/v1/day")
public class DayGameController {

    private final DayGameService dayGameService;

    public DayGameController(DayGameService dayGameService) {
        this.dayGameService = dayGameService;
    }

    // POST /api/v1/day/start
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<DaySession>> start(@RequestBody StartDayRequest req) {
        DaySession s = dayGameService.start(req.userId, req.jobType);
        return ResponseEntity.ok(ApiResponse.ok("Session started", s));
    }

    // POST /api/v1/day/{sessionId}/finish?userId=1
    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<ApiResponse<DaySession>> finish(@PathVariable Long sessionId,
                                                          @RequestParam Long userId,
                                                          @RequestBody FinishDayRequest req) {
        DaySession s = dayGameService.finish(userId, sessionId, req);
        return ResponseEntity.ok(ApiResponse.ok("Session finished", s));
    }
}
