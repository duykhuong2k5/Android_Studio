package com.example.pandora.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pandora.enums.JobType;
import com.example.pandora.enums.RewardType;
import com.example.pandora.model.DaySession;
import com.example.pandora.model.JobProgress;
import com.example.pandora.model.Reward;
import com.example.pandora.model.User;
import com.example.pandora.model.UserProgress;
import com.example.pandora.model.UserReward;
import com.example.pandora.model.request.FinishDayRequest;
import com.example.pandora.repository.DaySessionRepository;
import com.example.pandora.repository.JobProgressRepository;
import com.example.pandora.repository.RewardRepository;
import com.example.pandora.repository.UserProgressRepository;
import com.example.pandora.repository.UserRepository;
import com.example.pandora.repository.UserRewardRepository;

@Service
public class DayGameService {

    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final JobProgressRepository jobProgressRepository;
    private final DaySessionRepository daySessionRepository;

    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;

    private final ProgressService progressService;
    private final JobUnlockService jobUnlockService;

    public DayGameService(UserRepository userRepository,
                          UserProgressRepository userProgressRepository,
                          JobProgressRepository jobProgressRepository,
                          DaySessionRepository daySessionRepository,
                          RewardRepository rewardRepository,
                          UserRewardRepository userRewardRepository,
                          ProgressService progressService,
                          JobUnlockService jobUnlockService) {
        this.userRepository = userRepository;
        this.userProgressRepository = userProgressRepository;
        this.jobProgressRepository = jobProgressRepository;
        this.daySessionRepository = daySessionRepository;
        this.rewardRepository = rewardRepository;
        this.userRewardRepository = userRewardRepository;
        this.progressService = progressService;
        this.jobUnlockService = jobUnlockService;
    }

    @Transactional
    public DaySession start(Long userId, JobType jobType) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (jobType == null) throw new IllegalArgumentException("jobType is required");

        // init progress + job progress nếu chưa có
        progressService.ensureUserProgress(userId);
        progressService.ensureJobProgress(userId);

        // 🔒 TRÁNH TẠO NHIỀU SESSION CÙNG LÚC: nếu có session đang chơi thì trả về luôn
        var openSession = daySessionRepository
                .findFirstByUser_IdAndJobTypeAndCompletedFalseOrderByStartedAtDesc(userId, jobType);
        if (openSession.isPresent()) return openSession.get();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        JobProgress jp = jobProgressRepository.findByUser_IdAndJobType(userId, jobType)
                .orElseThrow(() -> new IllegalStateException("JobProgress not initialized"));

        if (!jp.isUnlocked()) throw new IllegalStateException("Job is locked");

        DaySession s = new DaySession();
        s.setUser(user);
        s.setJobType(jobType);
        s.setScore(0);
        s.setCorrect(0);
        s.setWrong(0);
        s.setStars(0);
        s.setCompleted(false);
        s.setStartedAt(LocalDateTime.now());

        return daySessionRepository.save(s);
    }

    @Transactional
    public DaySession finish(Long userId, Long sessionId, FinishDayRequest req) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (sessionId == null) throw new IllegalArgumentException("sessionId is required");
        if (req == null) throw new IllegalArgumentException("FinishDayRequest is required");

        DaySession s = daySessionRepository.findByIdAndUser_Id(sessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (s.isCompleted()) return s;

        // =========================
        // 3) BẮT BUỘC: VALIDATE HOÀN THÀNH & STARS
        // =========================

        // ✅ clamp stars 0..3
        int stars = Math.max(0, Math.min(3, req.stars));

        // ✅ chống dữ liệu âm
        int score = Math.max(0, req.score);
        int correct = Math.max(0, req.correct);
        int wrong = Math.max(0, req.wrong);

        // ✅ RULE hoàn thành (Level 1 đơn giản):
        // - phải có ít nhất 1 câu trả lời (correct+wrong > 0)
        // - correct không được vượt tổng
        // - stars phải > 0 để tính là hoàn thành
        int totalAnswered = correct + wrong;
        if (totalAnswered <= 0) {
            throw new IllegalArgumentException("Invalid result: totalAnswered must be > 0");
        }
        if (correct > totalAnswered) {
            throw new IllegalArgumentException("Invalid result: correct cannot be greater than totalAnswered");
        }
        if (stars <= 0) {
            throw new IllegalArgumentException("Invalid result: stars must be >= 1 to complete the day");
        }

        // 1) update session
        s.setScore(score);
        s.setCorrect(correct);
        s.setWrong(wrong);
        s.setStars(stars);
        s.setCompleted(true);
        s.setFinishedAt(LocalDateTime.now());
        daySessionRepository.save(s);

        // 2) update job progress
        JobProgress jp = jobProgressRepository.findByUser_IdAndJobType(userId, s.getJobType())
                .orElseThrow(() -> new IllegalStateException("JobProgress not found"));

        jp.setCompleted(true);
        jp.setStars(Math.max(jp.getStars(), stars));
        jobProgressRepository.save(jp);

        // 3) update user progress
        UserProgress up = userProgressRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalStateException("UserProgress not found"));

        up.setTotalStars(up.getTotalStars() + stars);
        up.setTotalXp(up.getTotalXp() + Math.max(0, score / 2)); // rule tùy bạn
        userProgressRepository.save(up);

        // 4) Grant reward badge cho job hoàn thành (Reward master + UserReward)
        Reward badge = getOrCreateRewardMaster(
                RewardType.BADGE,
                "Badge: " + s.getJobType(),
                null
        );
        grantUserReward(userId, badge);

        // 5) unlock job tiếp theo
        JobType next = jobUnlockService.next(s.getJobType());
        if (next != null) {
            JobProgress nextJp = jobProgressRepository.findByUser_IdAndJobType(userId, next)
                    .orElseThrow(() -> new IllegalStateException("Next JobProgress not found"));

            if (!nextJp.isUnlocked()) {
                nextJp.setUnlocked(true);
                jobProgressRepository.save(nextJp);

                Reward unlockReward = getOrCreateRewardMaster(
                        RewardType.SKIN,
                        "Unlocked Job: " + next,
                        null
                );
                grantUserReward(userId, unlockReward);
            }
        }

        return s;
    }

    // =========================
    // Helpers
    // =========================

    private Reward getOrCreateRewardMaster(RewardType type, String name, String imageUrl) {
        Optional<Reward> existing = rewardRepository.findByRewardTypeAndName(type, name);
        if (existing.isPresent()) return existing.get();

        Reward r = new Reward();
        r.setRewardType(type);
        r.setName(name);
        r.setImageUrl(imageUrl);
        return rewardRepository.save(r);
    }

    private void grantUserReward(Long userId, Reward reward) {
        boolean already = userRewardRepository.existsByUser_IdAndReward_Id(userId, reward.getId());
        if (already) return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserReward ur = new UserReward();
        ur.setUser(user);
        ur.setReward(reward);
        userRewardRepository.save(ur);
    }
}
