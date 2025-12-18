package com.example.pandora.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.pandora.api.ApiResponse;
import com.example.pandora.model.request.GameFinishRequest;
import com.example.pandora.model.request.GameStartRequest;
import com.example.pandora.model.response.GameSessionResponse;
import com.example.pandora.service.GameSessionService;

@RestController
@RequestMapping("/api/v1/game/sessions")
public class GameSessionController {

    private final GameSessionService gameSessionService;

    public GameSessionController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    // POST /api/v1/game/sessions/start
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<GameSessionResponse>> start(@RequestBody GameStartRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Session started", gameSessionService.start(req)));
    }

    // POST /api/v1/game/sessions/{id}/finish
    @PostMapping("/{id}/finish")
    public ResponseEntity<ApiResponse<GameSessionResponse>> finish(
            @PathVariable("id") Long sessionId,
            @RequestBody GameFinishRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Session finished", gameSessionService.finish(sessionId, req)));
    }
}

