package com.example.pandora.model.response;


public class UserProgressResponse {
    private Long id;
    private Long userId;
    private int totalStars;
    private int totalXp;
    private int streakDays;
    private int masteredWords;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public int getTotalStars() { return totalStars; }
    public void setTotalStars(int totalStars) { this.totalStars = totalStars; }

    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }

    public int getStreakDays() { return streakDays; }
    public void setStreakDays(int streakDays) { this.streakDays = streakDays; }

    public int getMasteredWords() { return masteredWords; }
    public void setMasteredWords(int masteredWords) { this.masteredWords = masteredWords; }
}

