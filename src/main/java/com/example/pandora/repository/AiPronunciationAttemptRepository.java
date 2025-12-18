package com.example.pandora.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pandora.enums.PronunciationFeedbackType;
import com.example.pandora.model.AiPronunciationAttempt;

import java.util.List;

public interface AiPronunciationAttemptRepository extends JpaRepository<AiPronunciationAttempt, Long> {

    List<AiPronunciationAttempt> findByUser_IdOrderByCreatedAtDesc(Long userId);

    List<AiPronunciationAttempt> findByUser_IdAndTargetTextOrderByCreatedAtDesc(Long userId, String targetText);

    List<AiPronunciationAttempt> findByUser_IdAndScoreGreaterThanEqual(Long userId, int score);

    List<AiPronunciationAttempt> findByUser_IdAndFeedbackTypeOrderByCreatedAtDesc(
            Long userId, PronunciationFeedbackType feedbackType
    );
}


