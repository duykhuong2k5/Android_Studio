package com.example.pandora.model.response;


import java.time.LocalDateTime;

public class UserRewardResponse {
    private Long id;
    private Long userId;
    private RewardResponse reward;
    private LocalDateTime unlockedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public RewardResponse getReward() { return reward; }
    public void setReward(RewardResponse reward) { this.reward = reward; }

    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }
}

