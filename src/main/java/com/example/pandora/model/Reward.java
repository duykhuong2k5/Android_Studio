package com.example.pandora.model;

import com.example.pandora.enums.RewardType;
import jakarta.persistence.*;

@Entity
@Table(
    name = "rewards",
    indexes = {
    		@Index(name = "idx_reward_type", columnList = "rewardType"),
            @Index(name = "idx_reward_name", columnList = "name")
    }
)
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType rewardType;

    @Column(nullable = false)
    private String name;

    private String imageUrl;

    

    public Reward() {}

    // ===== GETTER & SETTER =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RewardType getRewardType() { return rewardType; }
    public void setRewardType(RewardType rewardType) { this.rewardType = rewardType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

   
}
