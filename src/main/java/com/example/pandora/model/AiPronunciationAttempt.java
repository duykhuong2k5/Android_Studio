package com.example.pandora.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.example.pandora.enums.PronunciationFeedbackType;

@Entity
@Table(name = "ai_pronunciation_attempts")
public class AiPronunciationAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String targetText;

    @Column(nullable = false)
    private String audioPath;

    private int score; // 0-100

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PronunciationFeedbackType feedbackType;

    @Column(length = 500)
    private String feedback; // "Good try! Let's say APPLE again."

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (feedbackType == null) feedbackType = PronunciationFeedbackType.GOOD_TRY;
    }

    // ===== GETTER & SETTER =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTargetText() { return targetText; }
    public void setTargetText(String targetText) { this.targetText = targetText; }

    public String getAudioPath() { return audioPath; }
    public void setAudioPath(String audioPath) { this.audioPath = audioPath; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public PronunciationFeedbackType getFeedbackType() { return feedbackType; }
    public void setFeedbackType(PronunciationFeedbackType feedbackType) { this.feedbackType = feedbackType; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
