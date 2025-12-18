package com.example.pandora.controller;

import com.example.pandora.api.ApiResponse;
import com.example.pandora.enums.JobType;
import com.example.pandora.model.DaySession;
import com.example.pandora.model.DayStep;
import com.example.pandora.model.request.FinishDayRequest;
import com.example.pandora.model.request.SubmitStepRequest;
import com.example.pandora.service.DayGameService;
import com.example.pandora.service.DayStepService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/day/play")
public class DayPlayController {

    private final DayGameService dayGameService;
    private final DayStepService dayStepService;

    public DayPlayController(DayGameService dayGameService, DayStepService dayStepService) {
        this.dayGameService = dayGameService;
        this.dayStepService = dayStepService;
    }

    // POST /api/v1/day/play/start?userId=1&jobType=DOCTOR&level=1
    @PostMapping("/start")
    public ApiResponse<DaySession> start(@RequestParam Long userId,
                                         @RequestParam JobType jobType,
                                         @RequestParam(defaultValue = "1") int level) {
        DaySession session = dayGameService.start(userId, jobType);
        dayStepService.generateStepsFromAI(session, level);
        return ApiResponse.ok("Day started", session);
    }

    // GET /api/v1/day/play/{sessionId}/steps
    @GetMapping("/{sessionId}/steps")
    public ApiResponse<List<DayStep>> steps(@PathVariable Long sessionId) {
        return ApiResponse.ok(dayStepService.getSteps(sessionId));
    }

    // POST /api/v1/day/play/step
    // { "stepId": 10, "answer": "get up" }
    @PostMapping("/step")
    public ApiResponse<DayStep> submit(@RequestBody SubmitStepRequest req) {
        return ApiResponse.ok(dayStepService.submit(req.stepId, req.answer));
    }

    // POST /api/v1/day/play/{sessionId}/finish?userId=1
    @PostMapping("/{sessionId}/finish")
    public ApiResponse<DaySession> finish(@PathVariable Long sessionId,
                                          @RequestParam Long userId,
                                          @RequestBody FinishDayRequest req) {
        return ApiResponse.ok("Session finished", dayGameService.finish(userId, sessionId, req));
    }
}
