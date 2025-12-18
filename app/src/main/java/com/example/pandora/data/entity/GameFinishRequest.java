package com.example.pandora.data.entity;

public class GameFinishRequest {
    public int score;
    public int correctCount;
    public int wrongCount;
    public int earnedStars;

    public GameFinishRequest(int score, int correctCount, int wrongCount, int earnedStars) {
        this.score = score;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.earnedStars = earnedStars;
    }
}

