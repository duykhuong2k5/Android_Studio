package com.example.pandora.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pandora.model.UserReward;

import java.util.List;
import java.util.Optional;

public interface UserRewardRepository extends JpaRepository<UserReward, Long> {

    boolean existsByUser_IdAndReward_Id(Long userId, Long rewardId);

    Optional<UserReward> findByUser_IdAndReward_Id(Long userId, Long rewardId);

    List<UserReward> findByUser_IdOrderByUnlockedAtDesc(Long userId);
}



