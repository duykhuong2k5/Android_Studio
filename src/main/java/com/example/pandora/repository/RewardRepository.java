package com.example.pandora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pandora.enums.RewardType;
import com.example.pandora.model.Reward;

import java.util.List;
import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    
    // Lọc theo loại reward
    List<Reward> findByRewardType(RewardType rewardType);

    // Tìm theo tên (search)
    List<Reward> findByNameContainingIgnoreCase(String keyword);
    Optional<Reward> findByRewardTypeAndName(RewardType rewardType, String name);

}
