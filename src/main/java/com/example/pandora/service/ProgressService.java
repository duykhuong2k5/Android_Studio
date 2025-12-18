package com.example.pandora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pandora.enums.JobType;
import com.example.pandora.model.JobProgress;
import com.example.pandora.model.User;
import com.example.pandora.model.UserProgress;
import com.example.pandora.model.response.UserProgressResponse;
import com.example.pandora.repository.JobProgressRepository;
import com.example.pandora.repository.UserProgressRepository;
import com.example.pandora.repository.UserRepository;

@Service
public class ProgressService {

    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;
    private final JobProgressRepository jobProgressRepository;

    public ProgressService(UserProgressRepository userProgressRepository,
                           UserRepository userRepository,
                           JobProgressRepository jobProgressRepository) {
        this.userProgressRepository = userProgressRepository;
        this.userRepository = userRepository;
        this.jobProgressRepository = jobProgressRepository;
    }

    // ===== API /me dùng DTO =====
    @Transactional
    public UserProgressResponse getByUserId(Long userId) {
        UserProgress progress = ensureUserProgress(userId);
        ensureJobProgress(userId);
        return toDto(progress);
    }

    // ===== dùng cho summary =====
    @Transactional
    public UserProgress ensureUserProgress(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId is required");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userProgressRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    UserProgress p = new UserProgress();
                    p.setUser(user);
                    p.setTotalStars(0);
                    p.setTotalXp(0);
                    p.setStreakDays(0);
                    p.setMasteredWords(0);
                    return userProgressRepository.save(p);
                });
    }

    @Transactional
    public void ensureJobProgress(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        for (JobType jt : JobType.values()) {
            jobProgressRepository.findByUser_IdAndJobType(userId, jt).orElseGet(() -> {
                JobProgress jp = new JobProgress();
                jp.setUser(user);
                jp.setJobType(jt);
                jp.setUnlocked(jt == JobType.DOCTOR);
                jp.setCompleted(false);
                jp.setStars(0);
                return jobProgressRepository.save(jp);
            });
        }
    }

    @Transactional(readOnly = true)
    public List<JobProgress> getJobs(Long userId) {
        return jobProgressRepository.findByUser_Id(userId);
    }

    private UserProgressResponse toDto(UserProgress p) {
        UserProgressResponse dto = new UserProgressResponse();
        dto.setId(p.getId());
        dto.setUserId(p.getUser().getId());
        dto.setTotalStars(p.getTotalStars());
        dto.setTotalXp(p.getTotalXp());
        dto.setStreakDays(p.getStreakDays());
        dto.setMasteredWords(p.getMasteredWords());
        return dto;
    }
}
