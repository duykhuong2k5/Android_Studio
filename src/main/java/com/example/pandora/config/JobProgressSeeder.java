package com.example.pandora.config;

import com.example.pandora.enums.JobType;
import com.example.pandora.model.JobProgress;
import com.example.pandora.model.User;
import com.example.pandora.repository.JobProgressRepository;
import com.example.pandora.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JobProgressSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final JobProgressRepository jobRepo;

    public JobProgressSeeder(UserRepository userRepo,
                             JobProgressRepository jobRepo) {
        this.userRepo = userRepo;
        this.jobRepo = jobRepo;
    }

    @Override
    public void run(String... args) {
        User user = userRepo.findById(1L).orElse(null);
        if (user == null) return;
        if (jobRepo.count() > 0) return;

        for (JobType job : JobType.values()) {
            JobProgress jp = new JobProgress();
            jp.setUser(user);
            jp.setJobType(job);
            jp.setUnlocked(job == JobType.DOCTOR); // mở DOCTOR
            jp.setCompleted(false);
            jp.setStars(0);
            jobRepo.save(jp);
        }
    }
}
