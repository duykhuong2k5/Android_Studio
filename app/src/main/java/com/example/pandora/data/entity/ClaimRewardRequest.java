package com.example.pandora.data.entity;


public class ClaimRewardRequest {
    public Long userId;
    public Long rewardId;

    public ClaimRewardRequest(Long userId, Long rewardId) {
        this.userId = userId;
        this.rewardId = rewardId;
    }
}

