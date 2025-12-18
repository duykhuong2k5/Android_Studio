package com.example.pandora.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.pandora.api.ApiResponse;
import com.example.pandora.enums.RewardType;
import com.example.pandora.model.request.ClaimRewardRequest;
import com.example.pandora.model.response.RewardResponse;
import com.example.pandora.model.response.UserRewardResponse;
import com.example.pandora.service.RewardsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rewards")
public class RewardsController {

    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    // GET /api/v1/rewards?rewardType=SKIN
    @GetMapping
    public ResponseEntity<ApiResponse<List<RewardResponse>>> list(
            @RequestParam(value = "rewardType", required = false) RewardType rewardType
    ) {
        return ResponseEntity.ok(ApiResponse.ok(rewardsService.listRewards(rewardType)));
    }

    // POST /api/v1/rewards/claim
    // body: { "userId": 1, "rewardId": 10 }
    @PostMapping("/claim")
    public ResponseEntity<ApiResponse<UserRewardResponse>> claim(@RequestBody ClaimRewardRequest req) {
        UserRewardResponse res = rewardsService.claimReward(req.getUserId(), req.getRewardId());
        return ResponseEntity.ok(ApiResponse.ok("Reward claimed", res));
    }

    // GET /api/v1/rewards/me?userId=1
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<UserRewardResponse>>> myRewards(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(rewardsService.myRewards(userId)));
    }
}

