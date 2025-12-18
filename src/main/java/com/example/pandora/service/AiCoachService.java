package com.example.pandora.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.pandora.enums.PronunciationFeedbackType;
import com.example.pandora.model.AiPronunciationAttempt;
import com.example.pandora.model.User;
import com.example.pandora.model.response.AiAttemptResponse;
import com.example.pandora.repository.AiPronunciationAttemptRepository;
import com.example.pandora.repository.UserRepository;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AiCoachService {

    private final AiPronunciationAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    // nơi lưu audio local (dev). Prod bạn thay S3/Cloud
    private final String uploadDir = "uploads/audio";

    public AiCoachService(AiPronunciationAttemptRepository attemptRepository,
                          UserRepository userRepository) {
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
    }

    public AiAttemptResponse createAttempt(Long userId, String targetText, MultipartFile audioFile) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (targetText == null || targetText.trim().isEmpty()) throw new IllegalArgumentException("targetText is required");
        if (audioFile == null || audioFile.isEmpty()) throw new IllegalArgumentException("audioFile is required");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String savedPath = saveAudio(audioFile);

        // (MVP) giả lập chấm điểm đơn giản
        int score = fakeScore(targetText);
        PronunciationFeedbackType type = score >= 80 ? PronunciationFeedbackType.GREAT
                : (score >= 55 ? PronunciationFeedbackType.GOOD_TRY : PronunciationFeedbackType.TRY_AGAIN);

        String feedback = buildFriendlyFeedback(targetText, score);

        AiPronunciationAttempt attempt = new AiPronunciationAttempt();
        attempt.setUser(user);
        attempt.setTargetText(targetText.trim());
        attempt.setAudioPath(savedPath);
        attempt.setScore(score);
        attempt.setFeedbackType(type);
        attempt.setFeedback(feedback);
        attempt.setCreatedAt(LocalDateTime.now());

        AiPronunciationAttempt saved = attemptRepository.save(attempt);
        return toDto(saved);
    }

    private String saveAudio(MultipartFile file) {
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String ext = getFileExt(file.getOriginalFilename());
            String name = UUID.randomUUID() + (ext.isEmpty() ? ".wav" : ext);
            File dest = new File(dir, name);
            file.transferTo(dest);

            return uploadDir + "/" + name;
        } catch (Exception e) {
            throw new RuntimeException("Cannot save audio file: " + e.getMessage());
        }
    }

    private String getFileExt(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        if (i < 0) return "";
        return filename.substring(i).toLowerCase();
    }

    private int fakeScore(String targetText) {
        // MVP: cứ cho random nhẹ dựa trên độ dài (để demo)
        int base = Math.min(90, 40 + targetText.length() * 5);
        return Math.max(30, base);
    }

    private String buildFriendlyFeedback(String targetText, int score) {
        if (score >= 80) return "Great! Your pronunciation sounds good.";
        if (score >= 55) return "Good try! Let's say it again slowly.";
        return "Nice effort! Try again. Say it slowly and clearly.";
    }

    private AiAttemptResponse toDto(AiPronunciationAttempt a) {
        AiAttemptResponse dto = new AiAttemptResponse();
        dto.setId(a.getId());
        dto.setUserId(a.getUser().getId());
        dto.setTargetText(a.getTargetText());
        dto.setScore(a.getScore());
        dto.setFeedbackType(a.getFeedbackType());
        dto.setFeedback(a.getFeedback());
        dto.setAudioPath(a.getAudioPath());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}

