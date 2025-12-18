package com.example.pandora.service;

import org.springframework.stereotype.Service;

import com.example.pandora.enums.RewardType;
import com.example.pandora.model.Reward;
import com.example.pandora.model.User;
import com.example.pandora.model.UserReward;
import com.example.pandora.model.response.RewardResponse;
import com.example.pandora.model.response.UserRewardResponse;
import com.example.pandora.repository.RewardRepository;
import com.example.pandora.repository.UserRepository;
import com.example.pandora.repository.UserRewardRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RewardsService {

    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;
    private final UserRepository userRepository;

    public RewardsService(RewardRepository rewardRepository,
                          UserRewardRepository userRewardRepository,
                          UserRepository userRepository) {
        this.rewardRepository = rewardRepository;
        this.userRewardRepository = userRewardRepository;
        this.userRepository = userRepository;
    }

    public List<RewardResponse> listRewards(RewardType rewardType) {
        List<Reward> rewards = (rewardType == null)
                ? rewardRepository.findAll()
                : rewardRepository.findByRewardType(rewardType);

        List<RewardResponse> res = new ArrayList<>();
        for (Reward r : rewards) res.add(toRewardDto(r));
        return res;
    }

    public UserRewardResponse claimReward(Long userId, Long rewardId) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (rewardId == null) throw new IllegalArgumentException("rewardId is required");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("Reward not found"));

        boolean exists = userRewardRepository.existsByUser_IdAndReward_Id(userId, rewardId);
        if (exists) {
            // đã claim rồi -> trả về bản ghi hiện có
            UserReward ur = userRewardRepository.findByUser_IdAndReward_Id(userId, rewardId)
                    .orElseThrow(() -> new IllegalArgumentException("Already claimed but not found"));
            return toUserRewardDto(ur);
        }

        UserReward ur = new UserReward();
        ur.setUser(user);
        ur.setReward(reward);
        // unlockedAt sẽ tự set ở @PrePersist
        UserReward saved = userRewardRepository.save(ur);

        return toUserRewardDto(saved);
    }

    public List<UserRewardResponse> myRewards(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId is required");

        List<UserReward> list = userRewardRepository.findByUser_IdOrderByUnlockedAtDesc(userId);
        List<UserRewardResponse> res = new ArrayList<>();
        for (UserReward ur : list) res.add(toUserRewardDto(ur));
        return res;
    }

    private RewardResponse toRewardDto(Reward r) {
        RewardResponse dto = new RewardResponse();
        dto.setId(r.getId());
        dto.setRewardType(r.getRewardType());
        dto.setName(r.getName());
        dto.setImageUrl(r.getImageUrl());
        return dto;
    }

    private UserRewardResponse toUserRewardDto(UserReward ur) {
        UserRewardResponse dto = new UserRewardResponse();
        dto.setId(ur.getId());
        dto.setUserId(ur.getUser().getId());
        dto.setUnlockedAt(ur.getUnlockedAt());
        dto.setReward(toRewardDto(ur.getReward()));
        return dto;
    }
}

