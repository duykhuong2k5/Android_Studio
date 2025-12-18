package com.example.pandora.model.response;


import java.time.LocalDateTime;

import com.example.pandora.enums.PronunciationFeedbackType;

public class AiAttemptResponse {
    private Long id;
    private Long userId;
    private String targetText;
    private int score;
    private PronunciationFeedbackType feedbackType;
    private String feedback;
    private String audioPath;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTargetText() { return targetText; }
    public void setTargetText(String targetText) { this.targetText = targetText; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public PronunciationFeedbackType getFeedbackType() { return feedbackType; }
    public void setFeedbackType(PronunciationFeedbackType feedbackType) { this.feedbackType = feedbackType; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getAudioPath() { return audioPath; }
    public void setAudioPath(String audioPath) { this.audioPath = audioPath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

