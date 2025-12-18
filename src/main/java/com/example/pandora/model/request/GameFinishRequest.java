package com.example.pandora.model.request;


public class GameFinishRequest {
    private int score;
    private int correctCount;
    private int wrongCount;
    private int earnedStars; // sao được trong lượt này

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }

    public int getEarnedStars() { return earnedStars; }
    public void setEarnedStars(int earnedStars) { this.earnedStars = earnedStars; }
}

