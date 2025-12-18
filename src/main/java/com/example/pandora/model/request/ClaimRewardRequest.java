package com.example.pandora.model.request;

public class ClaimRewardRequest {
    private Long userId;
    private Long rewardId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRewardId() { return rewardId; }
    public void setRewardId(Long rewardId) { this.rewardId = rewardId; }
}

