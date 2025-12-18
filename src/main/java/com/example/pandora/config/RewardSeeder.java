package com.example.pandora.config;

import com.example.pandora.enums.RewardType;
import com.example.pandora.model.Reward;
import com.example.pandora.repository.RewardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RewardSeeder implements CommandLineRunner {

    private final RewardRepository repo;

    public RewardSeeder(RewardRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;

        save(RewardType.BADGE, "Badge: DOCTOR");
        save(RewardType.BADGE, "Badge: CHEF");
        save(RewardType.SKIN, "Unlocked Job: FIREFIGHTER");
        save(RewardType.SKIN, "Unlocked Job: POLICE");
    }

    private void save(RewardType type, String name) {
        Reward r = new Reward();
        r.setRewardType(type);
        r.setName(name);
        r.setImageUrl(null);
        repo.save(r);
    }
}
